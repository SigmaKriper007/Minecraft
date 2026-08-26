package com.opus.entity.omega;

import com.opus.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Кольцевой импульс Омеги. По запросу игрока (2026-08-22):
 * модель раскатывается МГНОВЕННО (не постепенно) до радиуса = 6 × прежнего
 * максимума (34 × 6 = 204 блока), урон наносится один раз на весь фронт сразу.
 * После мгновенного раската кольцо живёт ещё 2 секунды с «законченной»
 * статичной анимацией, чисто визуально; линза лишь шевелит искры.
 */
public class OmegaRingWaveEntity extends Entity implements GeoAnimatable {

    /** Окно раската (входит в fold) — фоном идёт звук и урон. */
    public static final int LIFETIME_TICKS = 62;
    /** Физический максимум кольца — 6× прежнего (34 → 204). */
    public static final float MAX_RADIUS = 204.0F;
    /** Визуальный радиус модели: кольцо ~3.6× ширины хитбокса босса
     *  (диаметр 30 блоков; задача 19 — крупнее прежних 12.45). Геометрия
     *  записана на 204 блока, рендер масштабирует её до этого значения;
     *  зона урона остаётся MAX_RADIUS. */
    public static final float VISUAL_RADIUS = 15.0F;
    /** Визуальный максимум модели (геометрия записана в натуральную величину
     *  и рендерится с одинаковым масштабом). */
    public static final float MODEL_MAX_RADIUS = 204.0F;
    public static final float DAMAGE = 8.0F;
    public static final double KNOCKBACK = 2.4D;
    public static final double LAUNCH = 0.9D;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation PULSE_ANIM = RawAnimation.begin().thenLoop("pulse");

    private LivingEntity owner;
    private final Set<UUID> damaged = new HashSet<>();

    public OmegaRingWaveEntity(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    /** Физический радиус фронта волны — мгновенно максимум. */
    public float currentRadius() {
        return MAX_RADIUS;
    }

    /** Визуальный радиус модели — компактный, 1.5× ширины босса. */
    public float modelRadius() {
        return VISUAL_RADIUS;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        if (this.tickCount == 1) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.OMEGA_RING_WAVE, SoundSource.HOSTILE, 1.9F, 1.0F);
            this.dealDamage();
        }
        if (this.tickCount >= LIFETIME_TICKS) {
            this.discard();
            return;
        }
        // Янтарные искры по фронту — шевелятся по всей окружности 2 секунды
        if (this.tickCount % 3 == 0) {
            for (int i = 0; i < 40; i++) {
                double a = this.random.nextDouble() * Math.PI * 2;
                double rad = MAX_RADIUS * (0.75 + 0.25 * this.random.nextDouble());
                this.level().addParticle(ParticleTypes.END_ROD,
                        this.getX() + Math.cos(a) * rad,
                        this.getY() + 0.7 + this.random.nextDouble() * 3.0,
                        this.getZ() + Math.sin(a) * rad,
                        Math.cos(a) * 0.12, 0.08, Math.sin(a) * 0.12);
            }
        }
    }

    /** Спорт: урон + отброс каждому живому существу внутри полного радиуса. */
    private void dealDamage() {
        double r = MAX_RADIUS;
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(r + 2.0, 2.0, r + 2.0),
                e -> e.isAlive() && !e.isSpectator()
                        && !e.getTags().contains("omega_minion")
                        && (this.owner == null || !e.getUUID().equals(this.owner.getUUID())));
        DamageSource src = this.owner != null
                ? this.level().damageSources().mobAttack(this.owner)
                : this.level().damageSources().generic();
        for (LivingEntity target : targets) {
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > r) {
                continue;
            }
            this.damaged.add(target.getUUID());
            target.hurt(src, DAMAGE);
            if (dist > 0.01) {
                target.push(dx / dist * KNOCKBACK, LAUNCH, dz / dist * KNOCKBACK);
            }
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "ring", 0, state -> {
            // Волна энергии бежит по кольцу всё время его жизни (pulse, loop)
            state.getController().setAnimation(PULSE_ANIM);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public double getTick(Object object) {
        return this.tickCount;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}

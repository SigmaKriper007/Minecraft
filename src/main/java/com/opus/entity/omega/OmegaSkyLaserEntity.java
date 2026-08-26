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
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

/**
 * Орбитальный луч Омеги (задача 13, масштаб 2026-08-22): маркер-звезда на
 * земле (телеграф 4s), затем столб энергии с неба на 2s.
 * По обращению игрока: высота — как у небесного лазера из пушки (80 блоков),
 * ширина/длина столба в 5 раз больше прежней (радиус поражения 2.5 → 12.5,
 * диаметр столба 6 → 30 блоков).
 * При появлении метки — sky_laser_warn, в момент удара — sky_laser_omega.
 */
public class OmegaSkyLaserEntity extends Entity implements GeoAnimatable {

    public static final int PREVIEW_TICKS = 80;    // 4s телеграф
    public static final int STRIKE_TICKS = 40;     // 2s активная колонна
    public static final int LIFETIME_TICKS = PREVIEW_TICKS + STRIKE_TICKS;
    public static final float DAMAGE = 14.0F;
    public static final int DAMAGE_INTERVAL_TICKS = 10;
    /** Радиус поражения колонны — совпадает с видимой геометрией (4×4 блока,
     *  метка-телеграф r=2.49), а не былые 30 блоков (задача 19). */
    public static final float RADIUS = 3.0F;
    /** Высота столба — как у небесного лазера из пушки (SkyLaserEntity.MAX_HEIGHT_BLOCKS). */
    public static final float PILLAR_HEIGHT = 80.0F;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation PREVIEW_ANIM = RawAnimation.begin().thenLoop("preview");
    private static final RawAnimation STRIKE_ANIM = RawAnimation.begin().thenPlay("strike");

    private LivingEntity owner;
    private boolean announced = false;

    public OmegaSkyLaserEntity(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
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
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(40.0D, 4.0D, 40.0D).expandTowards(0.0D, PILLAR_HEIGHT + 4.0D, 0.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        if (this.tickCount >= LIFETIME_TICKS) {
            this.discard();
            return;
        }
        if (this.tickCount == 1) {
            // Появление предупреждающей метки (начало preview-анимации)
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.SKY_LASER_WARN, SoundSource.HOSTILE, 1.5F, 1.0F);
        }
        if (this.tickCount == PREVIEW_TICKS) {
            this.announced = true;
            // Момент удара лазера
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.SKY_LASER_OMEGA, SoundSource.HOSTILE, 1.7F, 1.0F);
            for (int i = 0; i < 40; i++) {
                double a = this.random.nextDouble() * Math.PI * 2;
                this.level().addParticle(ParticleTypes.LAVA,
                        this.getX() + Math.cos(a) * RADIUS, this.getY() + 0.2,
                        this.getZ() + Math.sin(a) * RADIUS, 0, 0, 0);
            }
        } else if (this.tickCount < PREVIEW_TICKS && this.tickCount % 20 == 10) {
            // периодическое напоминание, пока метка «заряжается»
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.BOSS_ORBITAL_WARN, net.minecraft.sounds.SoundSource.HOSTILE, 1.0F, 1.0F);
        }
        if (this.tickCount > PREVIEW_TICKS
                && (this.tickCount - PREVIEW_TICKS) % DAMAGE_INTERVAL_TICKS == 0) {
            List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(RADIUS, PILLAR_HEIGHT, RADIUS),
                    e -> e.isAlive() && !e.isSpectator()
                            && !e.getTags().contains("omega_minion")
                            && (this.owner == null || !e.getUUID().equals(this.owner.getUUID())));
            DamageSource src = this.owner != null
                    ? this.level().damageSources().mobAttack(this.owner)
                    : this.level().damageSources().generic();
            for (LivingEntity target : targets) {
                target.hurt(src, DAMAGE);
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
        controllers.add(new AnimationController<>(this, "sky_laser", 0, state -> {
            if (this.tickCount < PREVIEW_TICKS) {
                state.getController().setAnimation(PREVIEW_ANIM);
            } else {
                // 2s окно удара при 4s анимации → ускорение ×2
                state.getController().setAnimationSpeed(2.0F);
                state.getController().setAnimation(STRIKE_ANIM);
            }
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

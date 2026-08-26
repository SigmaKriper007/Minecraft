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
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

/**
 * Дуговой лазерный взмах Омеги (задача 13, масштаб 2026-08-22 ×12):
 * серповидная волна перед боссом. Фаза 1 — телеграф (0.25s spawn-анимация),
 * фаза 2 — мгновенное поражение сектора ~110° радиусом 138 блоков
 * (в 12 раз больше прежних 11.5). Спавнится боссом на уровне ног;
 * ориентация — по yaw владельца.
 */
public class OmegaSlashEntity extends Entity implements GeoAnimatable {

    public static final int TELEGRAPH_TICKS = 5;      // ~0.25s, совпадает с anim spawn
    public static final int ACTIVE_TICKS = 5;         // окно поражения
    public static final int LIFETIME_TICKS = TELEGRAPH_TICKS + ACTIVE_TICKS + 8;
    /** Радиус дуги — 12× прежнего (11.5 → 138). */
    public static final float REACH = 138.0F;
    /** Окно поражения по высоте (фронт клинка). */
    public static final float HEIGHT = 6.0F;
    public static final float DAMAGE = 16.0F;
    public static final float ARC_HALF_DEG = 55.0F;
    /** Рендер-масштаб модели: геометрия записана на дугу 138 блоков, а визуально
     *  слэш должен быть 1.5 × ширины хитбокса босса (12.45 блока). Зона урона
     *  (REACH) не меняется. */
    public static final float RENDER_SCALE = 12.45F / 138.0F;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation SPAWN_ANIM = RawAnimation.begin().thenPlay("spawn");
    private static final RawAnimation HOLD_ANIM = RawAnimation.begin().thenLoop("hold");
    private static final RawAnimation OUT_ANIM = RawAnimation.begin().thenPlay("out");

    private LivingEntity owner;
    private boolean dealt = false;

    public OmegaSlashEntity(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    /** Устанавливает направление взмаха: yaw в ванильной конвенции Entity,
     *  где вектор движения = (-sin(yaw), 0, cos(yaw)). */
    public void aimAt(Vec3 target) {
        double dx = target.x - this.getX();
        double dz = target.z - this.getZ();
        this.setYRot((float) (Math.atan2(-dx, dz) * 180.0D / Math.PI));
    }

    /** Единичный центральный вектор дуги (по yaw сущности). */
    public Vec3 arcDirection() {
        float yaw = this.getYRot();
        return new Vec3(-Math.sin(Math.toRadians(yaw)), 0.0D, Math.cos(Math.toRadians(yaw)));
    }

    /** Его дополнительная метка босса для зоны поражения/частиц. */
    public Vec3 centralDir = null;

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
        return this.getBoundingBox().inflate(REACH + 2.0D, 90.0D, REACH + 2.0D);
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
        if (!this.dealt && this.tickCount == TELEGRAPH_TICKS) {
            this.dealt = true;
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.OMEGA_SLASH_HIT, SoundSource.HOSTILE, 1.7F, 1.0F);
            // янтарная дуга искр
            Vec3 c = this.centralDir != null ? this.centralDir : this.arcDirection();
            double cYaw = Math.atan2(-c.x, c.z);
            for (int i = 0; i < 60; i++) {
                double a = cYaw + Math.toRadians(-ARC_HALF_DEG + i * (2 * ARC_HALF_DEG / 59.0));
                this.level().addParticle(ParticleTypes.FLAME,
                        this.getX() - Math.sin(a) * REACH * 0.9, this.getY() + 1.5 + this.random.nextDouble() * 4.0,
                        this.getZ() + Math.cos(a) * REACH * 0.9, 0, 0.2, 0);
            }
            List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(REACH, HEIGHT, REACH),
                    e -> e.isAlive() && !e.isSpectator()
                            && !e.getTags().contains("omega_minion")
                            && (this.owner == null || !e.getUUID().equals(this.owner.getUUID())));
            DamageSource src = this.owner != null
                    ? this.level().damageSources().mobAttack(this.owner)
                    : this.level().damageSources().generic();
            Vec3 central = this.centralDir != null ? this.centralDir : this.arcDirection();
            double centralYaw = Math.atan2(-central.x, central.z);
            for (LivingEntity target : targets) {
                double dx = target.getX() - this.getX();
                double dz = target.getZ() - this.getZ();
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > REACH) {
                    continue;
                }
                double ang = Math.atan2(-dx, dz);
                double diff = Math.abs(normalizeAngle(ang - centralYaw));
                if (diff <= Math.toRadians(ARC_HALF_DEG)) {
                    target.hurt(src, DAMAGE);
                }
            }
        }
    }

    private static double normalizeAngle(double a) {
        while (a > Math.PI) a -= 2 * Math.PI;
        while (a < -Math.PI) a += 2 * Math.PI;
        return a;
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
        controllers.add(new AnimationController<>(this, "slash", 0, state -> {
            if (this.tickCount < TELEGRAPH_TICKS) {
                state.getController().setAnimation(SPAWN_ANIM);
            } else if (this.tickCount < TELEGRAPH_TICKS + ACTIVE_TICKS) {
                state.getController().setAnimation(HOLD_ANIM);
            } else {
                state.getController().setAnimation(OUT_ANIM);
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

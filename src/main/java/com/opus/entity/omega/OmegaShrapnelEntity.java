package com.opus.entity.omega;

import com.opus.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
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
 * Янтарный шар-снаряд наплечной турели Омеги (задача 13, модель v2 — задача 15).
 * Летит по прямой, при контакте с блоком/сущностью взрывается: проигрывается
 * анимация impact (0.3s), наносится урон в небольшом радиусе, затем снаряд
 * исчезает. Игнорирует стрелка (босса).
 */
public class OmegaShrapnelEntity extends Entity implements GeoAnimatable {

    public static final float BASE_DAMAGE = 6.0F;
    /** Сплэш взрыва снаряда — пропорционален размеру колосса. */
    public static final float SPLASH_RADIUS = 7.0F;
    public static final int LIFETIME_TICKS = 240;
    /** Тиков жизни после взрыва — пока играется анимация impact. */
    public static final int IMPACT_TICKS = 6;
    /** Рендер-масштаб модели (снаряд соразмерен корпусу босса). */
    public static final float RENDER_SCALE = 3.0F;

    private static final EntityDataAccessor<Boolean> EXPLODING =
            SynchedEntityData.defineId(OmegaShrapnelEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation FLIGHT_ANIM = RawAnimation.begin().thenLoop("flight");
    private static final RawAnimation IMPACT_ANIM = RawAnimation.begin().thenPlay("impact");

    private net.minecraft.world.entity.LivingEntity owner;
    private int explodeElapsed = 0;

    public OmegaShrapnelEntity(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    public void shoot(Vec3 dir, double speed) {
        Vec3 n = dir.normalize();
        this.setDeltaMovement(n.scale(speed));
        this.setYRot((float) (Mth.atan2(n.x, n.z) * 180.0F / (float) Math.PI));
        double horizontal = Math.sqrt(n.x * n.x + n.z * n.z);
        this.setXRot((float) (Mth.atan2(n.y, horizontal) * 180.0F / (float) Math.PI));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(EXPLODING, false);
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
        if (!this.level().isClientSide) {
            if (this.tickCount >= LIFETIME_TICKS) {
                this.discard();
                return;
            }
            if (this.entityData.get(EXPLODING)) {
                this.explodeElapsed++;
                if (this.explodeElapsed >= IMPACT_TICKS) {
                    this.discard();
                }
                return;
            }
            // след-частицы
            if (this.tickCount % 2 == 0) {
                this.level().addParticle(ParticleTypes.ELECTRIC_SPARK,
                        this.getX(), this.getY() + 0.2, this.getZ(),
                        rand()-0.5, rand(), rand()-0.5);
                this.level().addParticle(ParticleTypes.FLAME,
                        this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            }
            this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
            // столкновение с блоками или сущностями
            boolean hitBlock = !this.level().noCollision(this.getBoundingBox().deflate(0.05D));
            List<LivingEntity> hits = this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(0.15D),
                    e -> e.isAlive() && !e.getTags().contains("omega_minion")
                            && (this.owner == null || !e.getUUID().equals(this.owner.getUUID())));
            if (hitBlock || !hits.isEmpty()) {
                // сообщаем клиенту о взрыве (impact-анимация) и бьём по площади
                this.entityData.set(EXPLODING, true);
                this.explode();
                return;
            }
        } else {
            if (!this.entityData.get(EXPLODING)) {
                this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
            }
        }
    }

    private double rand() {
        return this.random.nextDouble();
    }

    private void explode() {
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.BOSS_TURRET_SHOT, net.minecraft.sounds.SoundSource.HOSTILE, 1.1F, 1.2F);
        this.level().addParticle(ParticleTypes.EXPLOSION,
                this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(SPLASH_RADIUS),
                e -> e.isAlive() && !e.getTags().contains("omega_minion")
                        && (this.owner == null || !e.getUUID().equals(this.owner.getUUID())));
        DamageSource src = this.owner instanceof LivingEntity
                ? this.level().damageSources().mobAttack(this.owner)
                : this.level().damageSources().generic();
        for (LivingEntity target : targets) {
            target.hurt(src, BASE_DAMAGE);
            target.knockback(0.6D, -this.getDeltaMovement().x, -this.getDeltaMovement().z);
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

    // GeckoLib ---

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "detector", 0, state -> {
            if (this.entityData.get(EXPLODING)) {
                state.getController().setAnimation(IMPACT_ANIM);
            } else {
                state.getController().setAnimation(FLIGHT_ANIM);
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

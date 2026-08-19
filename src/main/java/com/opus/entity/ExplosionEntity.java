package com.opus.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ExplosionEntity extends Entity implements GeoAnimatable {
    public static final int LIFETIME_TICKS = 960;
    public static final double RADIUS_BLOCKS = 37.5D;
    public static final float BASE_DAMAGE = 150.0F;
    public static final int EFFECT_DURATION_TICKS = 600;

    private static final int NUKE_ANIM_TICKS = 45;
    private static final RawAnimation NUKE_ANIM = RawAnimation.begin().thenPlay("nuke");

    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);

    private static final String TAG_SHOOTER = "Shooter";

    private final Set<UUID> damaged = new HashSet<>();
    private UUID shooter;

    public ExplosionEntity(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public void setShooter(UUID shooter) {
        this.shooter = shooter;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID(TAG_SHOOTER)) {
            this.shooter = tag.getUUID(TAG_SHOOTER);
        }
        if (tag.contains("Damaged")) {
            long[] ids = tag.getLongArray("Damaged");
            for (int i = 0; i < ids.length - 1; i += 2) {
                this.damaged.add(new UUID(ids[i], ids[i + 1]));
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (this.shooter != null) {
            tag.putUUID(TAG_SHOOTER, this.shooter);
        }
        long[] longs = new long[this.damaged.size() * 2];
        int idx = 0;
        for (UUID id : this.damaged) {
            longs[idx++] = id.getMostSignificantBits();
            longs[idx++] = id.getLeastSignificantBits();
        }
        tag.putLongArray("Damaged", longs);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount >= LIFETIME_TICKS) {
            this.discard();
        }
        if (!this.level().isClientSide && this.tickCount > 0) {
            double radius = currentRadius();
            if (radius > 0.1D) {
                AABB box = new AABB(this.getX() - radius, this.getY() - radius, this.getZ() - radius,
                    this.getX() + radius, this.getY() + radius + 4.0D, this.getZ() + radius);
                List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, box,
                    entity -> entity.isAlive() && !entity.isSpectator() && !entity.getUUID().equals(this.shooter));
                if (!targets.isEmpty() && this.tickCount % 20 == 1) {
                    Player player = this.shooter != null ? this.level().getPlayerByUUID(this.shooter) : null;
                    DamageSource source = player != null
                        ? this.level().damageSources().playerAttack(player)
                        : this.level().damageSources().generic();
                    for (LivingEntity target : targets) {
                        if (this.damaged.add(target.getUUID())) {
                            target.hurt(source, BASE_DAMAGE);
                        }
                        target.addEffect(new MobEffectInstance(MobEffects.WITHER, EFFECT_DURATION_TICKS, 0));
                        target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, EFFECT_DURATION_TICKS, 0));
                        target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, EFFECT_DURATION_TICKS, 0));
                    }
                }
            }
        }
    }

    private double currentRadius() {
        double t = Math.min(this.tickCount / (double) LIFETIME_TICKS, 1.0D);
        return RADIUS_BLOCKS * ease(t);
    }

    private static double ease(double t) {
        return t * t * (3.0D - 2.0D * t);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<ExplosionEntity>(this, "nuke_controller", 0, ExplosionEntity::animationPredicate)
            .setAnimationSpeed(NUKE_ANIM_TICKS / (float) LIFETIME_TICKS));
    }

    private static PlayState animationPredicate(AnimationState<ExplosionEntity> state) {
        state.getController().setAnimation(NUKE_ANIM);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animatableCache;
    }

    @Override
    public double getTick(Object object) {
        return this.tickCount;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d = 512.0D * 512.0D;
        return distance < d;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(40.0D, 40.0D, 40.0D);
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }
}
package com.opus.entity;

import org.joml.Vector3f;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public class BlasterBeamEntity extends Entity implements GeoAnimatable {
    public static final int LIFETIME_TICKS = 60;
    public static final float MAX_LENGTH_BLOCKS = 75.0F;
    public static final float DIAMETER_BLOCKS = 0.5F;
    public static final float BASE_DAMAGE = 6.0F;
    public static final int DAMAGE_START_TICK = 20;
    public static final int DAMAGE_INTERVAL_TICKS = 20;

    private static final String TAG_SHOOTER = "Shooter";
    private static final String TAG_BEAM_LENGTH = "BeamLength";
    private static final RawAnimation BLAST_ANIM = RawAnimation.begin().thenPlay("blast");

    private static final EntityDataAccessor<Vector3f> DATA_DIRECTION =
        SynchedEntityData.defineId(BlasterBeamEntity.class, EntityDataSerializers.VECTOR3);

    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);

    private UUID shooter;
    private float beamLength = MAX_LENGTH_BLOCKS;

    public BlasterBeamEntity(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public void setShooter(UUID shooter) {
        this.shooter = shooter;
    }

    public UUID getShooterUUID() {
        return this.shooter;
    }

    public void setBeamLength(float beamLength) {
        this.beamLength = Mth.clamp(beamLength, 1.0F, MAX_LENGTH_BLOCKS);
    }

    public float getBeamLength() {
        return this.beamLength;
    }

    public Vec3 getBeamDirection() {
        float yaw = (float) Math.toRadians(this.getYRot());
        float pitch = (float) Math.toRadians(this.getXRot());
        return new Vec3(-Mth.sin(yaw) * Mth.cos(pitch), -Mth.sin(pitch), Mth.cos(yaw) * Mth.cos(pitch));
    }

    public void faceTo(float yRot, float xRot) {
        this.setRot(yRot, xRot);
    }

    public Vec3 getSyncedDirection() {
        Vector3f v = this.entityData.get(DATA_DIRECTION);
        return new Vec3(v.x, v.y, v.z);
    }

    public void setSyncedDirection(Vec3 direction) {
        this.entityData.set(DATA_DIRECTION,
            new Vector3f((float) direction.x, (float) direction.y, (float) direction.z));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_DIRECTION, new Vector3f());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID(TAG_SHOOTER)) {
            this.shooter = tag.getUUID(TAG_SHOOTER);
        }
        if (tag.contains(TAG_BEAM_LENGTH)) {
            this.beamLength = tag.getFloat(TAG_BEAM_LENGTH);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (this.shooter != null) {
            tag.putUUID(TAG_SHOOTER, this.shooter);
        }
        tag.putFloat(TAG_BEAM_LENGTH, this.beamLength);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount >= LIFETIME_TICKS) {
            this.discard();
            return;
        }
        if (this.level().isClientSide) {
            return;
        }
        if (this.tickCount <= 4 && this.random.nextInt(3) == 0) {
            Vec3 p = this.position();
            this.level().addParticle(new DustParticleOptions(new Vector3f(1.0F, 0.22F, 0.15F), 1.0F),
                p.x + (this.random.nextFloat() - 0.5D) * 0.2D,
                p.y + (this.random.nextFloat() - 0.5D) * 0.2D,
                p.z + (this.random.nextFloat() - 0.5D) * 0.2D,
                0.0D, 0.02D, 0.0D);
        }
        if (this.tickCount >= DAMAGE_START_TICK && this.tickCount % DAMAGE_INTERVAL_TICKS == 0) {
            this.damageInBeam();
        }
    }

    private void damageInBeam() {
        Vec3 start = this.position();
        Vec3 end = start.add(this.getBeamDirection().scale(this.beamLength));
        AABB box = new AABB(start, end).inflate(DIAMETER_BLOCKS / 2.0F + 0.2D);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, box,
            entity -> entity.isAlive() && !entity.isSpectator() && !entity.getUUID().equals(this.shooter));
        if (targets.isEmpty()) {
            return;
        }
        Player player = this.shooter != null ? this.level().getPlayerByUUID(this.shooter) : null;
        DamageSource source = player != null
            ? this.level().damageSources().playerAttack(player)
            : this.level().damageSources().generic();
        for (LivingEntity target : targets) {
            target.hurt(source, BASE_DAMAGE);
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<BlasterBeamEntity>(this, "blaster_controller", 0,
            BlasterBeamEntity::animationPredicate));
    }

    private static PlayState animationPredicate(AnimationState<BlasterBeamEntity> state) {
        state.getController().setAnimation(BLAST_ANIM);
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
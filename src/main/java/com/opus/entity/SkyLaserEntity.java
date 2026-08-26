package com.opus.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;

public class SkyLaserEntity extends Entity {
    public static final int LIFETIME_TICKS = 40;
    public static final float MAX_HEIGHT_BLOCKS = 80.0F;
    public static final float MAX_DIAMETER_BLOCKS = 12.0F;
    public static final float BASE_DAMAGE = 15.0F;
    public static final int DAMAGE_INTERVAL_TICKS = 10;

    private static final String TAG_SHOOTER = "Shooter";

    private UUID shooter;

    public SkyLaserEntity(EntityType<? extends Entity> entityType, Level level) {
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
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (this.shooter != null) {
            tag.putUUID(TAG_SHOOTER, this.shooter);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount >= LIFETIME_TICKS) {
            this.discard();
        }
        if (!this.level().isClientSide && this.tickCount > 0 && this.tickCount % DAMAGE_INTERVAL_TICKS == 0) {
            float radius = currentRadius();
            if (radius > 0.1F) {
                AABB box = new AABB(this.getX() - radius, this.getY(), this.getZ() - radius,
                    this.getX() + radius, this.getY() + MAX_HEIGHT_BLOCKS, this.getZ() + radius);
                List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, box,
                    entity -> entity.isAlive() && !entity.isSpectator() && !entity.getUUID().equals(this.shooter));
                if (!targets.isEmpty()) {
                    Player player = this.shooter != null ? this.level().getPlayerByUUID(this.shooter) : null;
                    DamageSource source = player != null
                        ? this.level().damageSources().playerAttack(player)
                        : this.level().damageSources().generic();
                    for (LivingEntity target : targets) {
                        target.hurt(source, BASE_DAMAGE);
                    }
                }
            }
        }
    }

    /**
     * Width flare progress (0..1): the beam flashes to full width in the first
     * 20% of its life, then narrows from 75% onward.
     */
    public float flareProgress() {
        float t = Math.min(this.tickCount / (LIFETIME_TICKS * 0.2F), 1.0F);
        return ease(t);
    }

    /**
     * Close progress (0..1): narrowing during the last quarter of the lifetime.
     */
    public float closeProgress() {
        float t = Math.max((this.tickCount / (float) LIFETIME_TICKS - 0.75F) / 0.25F, 0.0F);
        return ease(Math.min(t, 1.0F));
    }

    public float currentRadius() {
        float width = flareProgress() * (1.0F - closeProgress());
        return MAX_DIAMETER_BLOCKS / 2.0F * Math.max(width, 0.0F);
    }

    private static float ease(float t) {
        return t * t * (3.0F - 2.0F * t);
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
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d = 256.0D * 256.0D;
        return distance < d;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(8.0D, 1.0D, 8.0D).expandTowards(0.0D, 90.0D, 0.0D);
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }
}

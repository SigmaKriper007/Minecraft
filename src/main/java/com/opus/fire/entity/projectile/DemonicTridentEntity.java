package com.opus.fire.entity.projectile;

import com.opus.fire.registry.FireEntities;
import com.opus.fire.registry.FireParticles;
import com.opus.fire.sound.FireSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public final class DemonicTridentEntity extends Projectile implements GeoAnimatable {
    private static final EntityDataAccessor<Boolean> RETURNING = SynchedEntityData.defineId(DemonicTridentEntity.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
    private static final RawAnimation RETURN = RawAnimation.begin().thenLoop("return");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private UUID savedOwner;
    private int lifetime;
    private boolean impacted;

    public DemonicTridentEntity(EntityType<? extends Projectile> type, Level level) { super(type, level); }

    public DemonicTridentEntity(EntityType<? extends Projectile> type, Level level, LivingEntity owner) {
        this(type, level);
        setOwner(owner);
        savedOwner = owner.getUUID();
    }

    @Override protected void defineSynchedData() { entityData.define(RETURNING, false); }
    public boolean isReturning() { return entityData.get(RETURNING); }

    private void beginReturn() {
        entityData.set(RETURNING, true);
        noPhysics = true;
        setNoGravity(true);
    }

    @Override
    public boolean canHitEntity(Entity entity) {
        Entity owner = getOwner();
        return !isReturning() && entity != owner && (owner == null || !owner.isAlliedTo(entity)) && super.canHitEntity(entity);
    }

    @Override
    public void tick() {
        super.tick();
        lifetime++;
        resolveOwner();
        Entity owner = getOwner();
        if (owner == null || !owner.isAlive() || lifetime > 240) { discard(); return; }

        if (!isReturning()) {
            HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hit.getType() != HitResult.Type.MISS) impact(hit);
            if (!isReturning()) setDeltaMovement(getDeltaMovement().add(0.0, -0.045, 0.0));
        } else {
            Vec3 target = owner.getEyePosition().subtract(position());
            if (target.lengthSqr() < 1.5) { discard(); return; }
            setDeltaMovement(getDeltaMovement().scale(0.68).add(target.normalize().scale(0.48)));
        }

        Vec3 velocity = getDeltaMovement();
        setPos(getX() + velocity.x, getY() + velocity.y, getZ() + velocity.z);
        if (velocity.lengthSqr() > 1.0E-6) {
            setYRot((float) (Mth.atan2(velocity.x, velocity.z) * Mth.RAD_TO_DEG));
            setXRot((float) (Mth.atan2(velocity.y, Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z)) * Mth.RAD_TO_DEG));
        }
        if (level().isClientSide) {
            level().addParticle(FireParticles.EMBER, getX() - velocity.x * 0.25, getY() - velocity.y * 0.25,
                getZ() - velocity.z * 0.25, 0.0, 0.01, 0.0);
        }
    }

    private void resolveOwner() {
        if (getOwner() == null && savedOwner != null && level() instanceof ServerLevel server) {
            Entity entity = server.getEntity(savedOwner);
            if (entity != null) setOwner(entity);
        }
    }

    private void impact(HitResult hit) {
        if (level().isClientSide || impacted) return;
        impacted = true;
        if (hit instanceof EntityHitResult entityHit) {
            Entity target = entityHit.getEntity();
            DamageSource source = getOwner() instanceof LivingEntity owner
                ? damageSources().mobProjectile(this, owner) : damageSources().magic();
            target.hurt(source, 10.0f);
            target.setSecondsOnFire(6);
        }
        if (level() instanceof ServerLevel server) {
            ProtectedFireExplosion.explode(server, this, getOwner(), 4.0f);
            server.sendParticles(FireParticles.EMBER, getX(), getY(), getZ(), 28, 0.8, 0.8, 0.8, 0.2);
            server.playSound(null, this, FireSounds.FIRE_EXPLODE, SoundSource.HOSTILE, 1.7f, 0.76f);
        }
        beginReturn();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "flight", 0, state -> {
            state.getController().setAnimation(isReturning() ? RETURN : FLY); return PlayState.CONTINUE;
        }));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
    @Override public double getTick(Object ignored) { return tickCount; }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Owner")) savedOwner = tag.getUUID("Owner");
        lifetime = tag.getInt("Life");
        impacted = tag.getBoolean("Impacted");
        entityData.set(RETURNING, tag.getBoolean("Returning"));
        noPhysics = isReturning();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        Entity owner = getOwner();
        UUID id = owner != null ? owner.getUUID() : savedOwner;
        if (id != null) tag.putUUID("Owner", id);
        tag.putInt("Life", lifetime);
        tag.putBoolean("Impacted", impacted);
        tag.putBoolean("Returning", isReturning());
    }
}

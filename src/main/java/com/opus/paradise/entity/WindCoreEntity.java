package com.opus.paradise.entity;

import com.opus.paradise.registry.ParadiseEntities;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public final class WindCoreEntity extends Projectile implements GeoEntity {
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
    private static final DustParticleOptions CYAN = new DustParticleOptions(new Vector3f(0.25F, 0.95F, 1.0F), 0.9F);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private UUID wyvernId;
    private int lifetime;

    public WindCoreEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public WindCoreEntity(Level level, LivingEntity owner, ParadiseWyvernEntity wyvern, Vec3 direction) {
        this(ParadiseEntities.WIND_CORE, level);
        setOwner(owner);
        wyvernId = wyvern.getUUID();
        shoot(direction.x, direction.y, direction.z, 1.45F, 0.0F);
    }

    @Override protected void defineSynchedData() { }

    @Override
    protected boolean canHitEntity(Entity entity) {
        Entity owner = getOwner();
        return (wyvernId == null || !entity.getUUID().equals(wyvernId)) && entity != owner
            && (owner == null || !owner.isAlliedTo(entity))
            && super.canHitEntity(entity);
    }

    @Override
    public void tick() {
        super.tick();
        if (++lifetime > 100) {
            discard();
            return;
        }
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS) {
            onHit(hit);
            if (isRemoved()) return;
        }
        Vec3 motion = getDeltaMovement();
        setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
        updateRotation();
        if (level().isClientSide) {
            level().addParticle(CYAN, getX() - motion.x * 0.35, getY() - motion.y * 0.35, getZ() - motion.z * 0.35,
                -motion.x * 0.025, -motion.y * 0.025, -motion.z * 0.025);
            if ((tickCount & 1) == 0) level().addParticle(ParticleTypes.END_ROD, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void onHit(HitResult hit) {
        if (!level().isClientSide) impact(hit.getLocation());
    }

    private void impact(Vec3 location) {
        if (level().isClientSide || isRemoved()) return;
        HurricaneEntity hurricane = new HurricaneEntity(ParadiseEntities.HURRICANE, level());
        hurricane.setPos(location);
        hurricane.setCaster(getOwner(), wyvernId);
        level().addFreshEntity(hurricane);
        discard();
    }

    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        lifetime = tag.getInt("Life");
        if (tag.hasUUID("Wyvern")) wyvernId = tag.getUUID("Wyvern");
    }

    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Life", lifetime);
        if (wyvernId != null) tag.putUUID("Wyvern", wyvernId);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "core", 0, state -> {
            state.getController().setAnimation(FLY);
            return PlayState.CONTINUE;
        }));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}

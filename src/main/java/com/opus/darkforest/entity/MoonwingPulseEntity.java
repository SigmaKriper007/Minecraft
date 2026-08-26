package com.opus.darkforest.entity;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Expanding visible sonar ring that marks each target once and never affects blocks. */
public final class MoonwingPulseEntity extends Entity implements GeoEntity {
    private static final RawAnimation ACTIVE = RawAnimation.begin().thenLoop("active");
    private static final DustParticleOptions CYAN = new DustParticleOptions(new Vector3f(.34F, .92F, 1F), 1F);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Set<UUID> hit = new HashSet<>();
    private UUID ownerId;

    public MoonwingPulseEntity(EntityType<? extends MoonwingPulseEntity> type, Level level) {
        super(type, level); noPhysics = true; setNoGravity(true);
    }

    public MoonwingPulseEntity configure(LivingEntity owner) { ownerId = owner.getUUID(); return this; }
    public double radius() { return .25 + tickCount * .38; }
    @Override protected void defineSynchedData() { }

    @Override
    public void tick() {
        super.tick();
        double radius = radius();
        if (level().isClientSide) {
            int points = 18;
            for (int i = 0; i < points; i += 2) {
                double angle = Math.PI * 2 * (i + tickCount % 2) / points;
                level().addParticle(CYAN, getX() + Math.cos(angle) * radius, getY(), getZ() + Math.sin(angle) * radius, 0, .01, 0);
            }
            return;
        }
        AABB area = getBoundingBox().inflate(radius + 1.0, 2.0, radius + 1.0);
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, area, this::validTarget)) {
            double horizontal = Math.sqrt(target.distanceToSqr(getX(), target.getY(), getZ()));
            if (Math.abs(horizontal - radius) > .72 || Math.abs(target.getY() - getY()) > 2) continue;
            applyTo(target);
        }
        if (tickCount >= 28) discard();
    }

    private boolean validTarget(LivingEntity target) {
        return target.isAlive() && (ownerId == null || !ownerId.equals(target.getUUID()))
            && !(target instanceof ShadeSpiderlingEntity) && !(target instanceof GloomBroodmotherEntity)
            && !(target instanceof MoonwingBatEntity);
    }
    boolean applyTo(LivingEntity target) {
        if (!validTarget(target) || !hit.add(target.getUUID())) return false;
        target.hurt(damageSource(), 2F);
        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0));
        return true;
    }
    private DamageSource damageSource() {
        Entity owner = ownerId != null && level() instanceof ServerLevel server ? server.getEntity(ownerId) : null;
        return owner instanceof LivingEntity living ? damageSources().mobAttack(living) : damageSources().magic();
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        tickCount = tag.getInt("Age"); if (tag.hasUUID("Owner")) ownerId = tag.getUUID("Owner");
        hit.clear(); ListTag list = tag.getList("Hit", Tag.TAG_INT_ARRAY); for (Tag entry : list) hit.add(NbtUtils.loadUUID(entry));
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Age", tickCount); if (ownerId != null) tag.putUUID("Owner", ownerId);
        ListTag list = new ListTag(); for (UUID id : hit) list.add(NbtUtils.createUUID(id)); tag.put("Hit", list);
    }
    @Override public boolean isPickable() { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public boolean hurt(DamageSource source, float amount) { return false; }
    @Override public PushReaction getPistonPushReaction() { return PushReaction.IGNORE; }
    @Override public AABB getBoundingBoxForCulling() { return getBoundingBox().inflate(radius() + 1); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) { controllers.add(new AnimationController<>(this, "pulse", 0, state -> { state.getController().setAnimation(ACTIVE); return PlayState.CONTINUE; })); }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}

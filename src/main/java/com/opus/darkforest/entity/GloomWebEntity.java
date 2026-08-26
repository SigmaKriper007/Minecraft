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

/** Temporary, entity-only web hazard. It never places or removes world blocks. */
public final class GloomWebEntity extends Entity implements GeoEntity {
    private static final RawAnimation ACTIVE = RawAnimation.begin().thenLoop("active");
    private static final DustParticleOptions WEB = new DustParticleOptions(new Vector3f(.48F, .82F, .86F), .8F);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Set<UUID> hit = new HashSet<>();
    private UUID ownerId;
    private int lifetime = 48;

    public GloomWebEntity(EntityType<? extends GloomWebEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
    }

    public GloomWebEntity configure(LivingEntity owner, int ticks) {
        ownerId = owner.getUUID();
        lifetime = ticks;
        return this;
    }

    @Override protected void defineSynchedData() { }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if ((tickCount & 1) == 0) level().addParticle(WEB, getX() + (random.nextDouble() - .5) * 1.2,
                getY() + random.nextDouble() * .35, getZ() + (random.nextDouble() - .5) * 1.2, 0, .01, 0);
            return;
        }
        if (tickCount >= 8) {
            AABB area = getBoundingBox().inflate(.72, .45, .72);
            for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, area, this::validTarget)) {
                applyTo(target);
            }
        }
        if (tickCount >= lifetime) discard();
    }

    private boolean validTarget(LivingEntity target) {
        return target.isAlive() && (ownerId == null || !ownerId.equals(target.getUUID()))
            && !(target instanceof ShadeSpiderlingEntity) && !(target instanceof GloomBroodmotherEntity)
            && !(target instanceof MoonwingBatEntity);
    }

    boolean applyTo(LivingEntity target) {
        if (!validTarget(target) || !hit.add(target.getUUID())) return false;
        target.hurt(damageSource(), 3F);
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
        return true;
    }

    private DamageSource damageSource() {
        Entity owner = ownerId != null && level() instanceof ServerLevel server ? server.getEntity(ownerId) : null;
        return owner instanceof LivingEntity living ? damageSources().mobAttack(living) : damageSources().magic();
    }

    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        tickCount = tag.getInt("Age"); lifetime = tag.getInt("Lifetime"); if (tag.hasUUID("Owner")) ownerId = tag.getUUID("Owner");
        hit.clear(); ListTag list = tag.getList("Hit", Tag.TAG_INT_ARRAY); for (Tag entry : list) hit.add(NbtUtils.loadUUID(entry));
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Age", tickCount); tag.putInt("Lifetime", lifetime); if (ownerId != null) tag.putUUID("Owner", ownerId);
        ListTag list = new ListTag(); for (UUID id : hit) list.add(NbtUtils.createUUID(id)); tag.put("Hit", list);
    }
    @Override public boolean isPickable() { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public boolean hurt(DamageSource source, float amount) { return false; }
    @Override public PushReaction getPistonPushReaction() { return PushReaction.IGNORE; }
    @Override public AABB getBoundingBoxForCulling() { return getBoundingBox().inflate(2); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) { controllers.add(new AnimationController<>(this, "web", 0, state -> { state.getController().setAnimation(ACTIVE); return PlayState.CONTINUE; })); }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}

package com.opus.entity;

import com.opus.registry.ModEntities;
import com.opus.sound.ModSounds;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PunchShockwaveEntity extends Entity implements GeoAnimatable {
    public static final int LIFETIME = 12;
    private static final EntityDataAccessor<Integer> TIER =
            SynchedEntityData.defineId(PunchShockwaveEntity.class, EntityDataSerializers.INT);
    private static final RawAnimation BURST = RawAnimation.begin().thenPlay("burst");
    private static final DustParticleOptions BLUE_DUST =
            new DustParticleOptions(new Vector3f(0.12F, 0.48F, 1.0F), 1.25F);
    private static final DustParticleOptions WHITE_DUST =
            new DustParticleOptions(new Vector3f(0.88F, 0.96F, 1.0F), 1.0F);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Set<UUID> hitEntities = new HashSet<>();
    private UUID ownerId;
    private UUID pilotId;
    private ExosuitEntity owner;
    private float damage;
    private float maxRadius;

    public PunchShockwaveEntity(EntityType<? extends PunchShockwaveEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TIER, 0);
    }

    public int getTierIndex() {
        return this.entityData.get(TIER);
    }

    public float getVisualProgress(float partialTick) {
        return Math.min(1.0F, (this.tickCount + partialTick) / (float) LIFETIME);
    }

    public static PunchShockwaveEntity spawn(ExosuitEntity owner, Player pilot) {
        PunchShockwaveEntity wave = new PunchShockwaveEntity(ModEntities.PUNCH_SHOCKWAVE, owner.level());
        int tierIndex = owner.getTier().ordinal();
        Vec3 direction = pilot.getViewVector(1.0F);
        direction = new Vec3(direction.x, direction.y * 0.25D, direction.z).normalize();
        Vec3 origin = owner.getEyePosition().add(direction.scale(owner.getBbWidth() * 0.55D + 1.0D));
        wave.setPos(origin);
        wave.setYRot((float) (Math.atan2(-direction.x, direction.z) * 180.0D / Math.PI));
        wave.entityData.set(TIER, tierIndex);
        wave.owner = owner;
        wave.ownerId = owner.getUUID();
        wave.pilotId = pilot.getUUID();
        wave.damage = owner.getAttackDamage() * 1.15F;
        wave.maxRadius = 3.0F + tierIndex * 0.6F;
        owner.level().addFreshEntity(wave);
        return wave;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > LIFETIME) {
            this.discard();
            return;
        }
        if (this.level().isClientSide) {
            return;
        }
        resolveOwner();
        float progress = this.tickCount / (float) LIFETIME;
        double radius = Math.max(0.8D, this.maxRadius * progress);
        damageTargets(radius);
        if (this.level() instanceof ServerLevel server) {
            spawnPressureRing(server, radius, progress);
        }
        if (this.tickCount == 1) {
            this.level().playSound(null, this.blockPosition(), ModSounds.SHOCKWAVE,
                    SoundSource.PLAYERS, 1.25F, 1.45F - getTierIndex() * 0.06F);
        }
    }

    private void damageTargets(double radius) {
        AABB box = this.getBoundingBox().inflate(radius, radius * 0.8D, radius);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, box, this::isValidTarget)) {
            Vec3 delta = target.getEyePosition().subtract(this.position());
            if (delta.lengthSqr() > radius * radius || !this.hitEntities.add(target.getUUID())) {
                continue;
            }
            DamageSource source = this.owner != null
                    ? this.level().damageSources().mobAttack(this.owner)
                    : this.level().damageSources().magic();
            if (target.hurt(source, this.damage)) {
                Vec3 forward = Vec3.directionFromRotation(0.0F, this.getYRot()).normalize();
                target.push(forward.x * (1.5D + getTierIndex() * 0.18D),
                        0.25D + getTierIndex() * 0.08D,
                        forward.z * (1.5D + getTierIndex() * 0.18D));
            }
        }
    }

    private boolean isValidTarget(LivingEntity target) {
        if (!target.isAlive() || target.isSpectator() || target instanceof ExosuitEntity) {
            return false;
        }
        UUID id = target.getUUID();
        return !id.equals(this.ownerId) && !id.equals(this.pilotId)
                && (this.owner == null || !target.isAlliedTo(this.owner));
    }

    private void spawnPressureRing(ServerLevel server, double radius, float progress) {
        double yaw = Math.toRadians(this.getYRot());
        Vec3 side = new Vec3(Math.cos(yaw), 0.0D, Math.sin(yaw));
        for (int i = 0; i < 18; i++) {
            double angle = Math.PI * 2.0D * i / 18.0D;
            double sideOffset = Math.cos(angle) * radius;
            double yOffset = Math.sin(angle) * radius * 0.8D;
            Vec3 point = this.position().add(side.scale(sideOffset)).add(0.0D, yOffset, 0.0D);
            server.sendParticles((i & 3) == 0 ? WHITE_DUST : BLUE_DUST,
                    point.x, point.y, point.z, 1, 0.03D, 0.03D, 0.03D, 0.0D);
        }
        if (progress < 0.35F) {
            server.sendParticles(ParticleTypes.FLASH, this.getX(), this.getY(), this.getZ(),
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private void resolveOwner() {
        if (this.owner == null && this.ownerId != null && this.level() instanceof ServerLevel server) {
            Entity entity = server.getEntity(this.ownerId);
            if (entity instanceof ExosuitEntity exo) {
                this.owner = exo;
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.entityData.set(TIER, tag.getInt("PunchTier"));
        this.damage = tag.getFloat("PunchDamage");
        this.maxRadius = tag.getFloat("PunchRadius");
        if (tag.hasUUID("PunchOwner")) this.ownerId = tag.getUUID("PunchOwner");
        if (tag.hasUUID("PunchPilot")) this.pilotId = tag.getUUID("PunchPilot");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("PunchTier", getTierIndex());
        tag.putFloat("PunchDamage", this.damage);
        tag.putFloat("PunchRadius", this.maxRadius);
        if (this.ownerId != null) tag.putUUID("PunchOwner", this.ownerId);
        if (this.pilotId != null) tag.putUUID("PunchPilot", this.pilotId);
    }

    @Override public boolean isPickable() { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public boolean hurt(DamageSource source, float amount) { return false; }
    @Override public PushReaction getPistonPushReaction() { return PushReaction.IGNORE; }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(8.0D);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "punch_wave", 0, state -> {
            state.getController().setAnimation(BURST);
            return PlayState.CONTINUE;
        }));
    }

    @Override public double getTick(Object object) { return this.tickCount; }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }
}

package com.opus.entity;

import com.opus.registry.ModEntities;
import com.opus.sound.ModSounds;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
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

public class KatanaSlashEntity extends Entity implements GeoAnimatable {
    public static final int OPUS = 0;
    public static final int REFINED = 1;
    public static final int GOLD = 2;

    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(KatanaSlashEntity.class, EntityDataSerializers.INT);
    private static final RawAnimation TRAVEL = RawAnimation.begin().thenLoop("travel");
    private static final DustParticleOptions OPUS_DUST =
            new DustParticleOptions(new Vector3f(0.48F, 0.08F, 0.82F), 1.15F);
    private static final DustParticleOptions REFINED_DUST =
            new DustParticleOptions(new Vector3f(0.12F, 0.76F, 0.92F), 0.9F);
    private static final DustParticleOptions GOLD_DUST =
            new DustParticleOptions(new Vector3f(1.0F, 0.62F, 0.08F), 1.35F);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Set<UUID> hitEntities = new HashSet<>();
    private UUID ownerId;
    private LivingEntity owner;
    private float slashDamage;
    private int maxLife;
    private boolean ruptureFinished;

    public KatanaSlashEntity(EntityType<? extends KatanaSlashEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(VARIANT, OPUS);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void configure(Player owner, int variant, Vec3 direction, float damage, int lifetime) {
        this.owner = owner;
        this.ownerId = owner.getUUID();
        this.entityData.set(VARIANT, variant);
        this.slashDamage = damage;
        this.maxLife = lifetime;
        Vec3 normalized = direction.lengthSqr() > 0.01D ? direction.normalize() : owner.getLookAngle().normalize();
        if (variant == GOLD) {
            normalized = new Vec3(normalized.x, 0.0D, normalized.z).normalize();
        }
        double speed = variant == GOLD ? 0.82D : variant == REFINED ? 1.25D : 1.4D;
        this.setDeltaMovement(normalized.scale(speed));
        this.setYRot((float) (Math.atan2(-normalized.x, normalized.z) * 180.0D / Math.PI));
    }

    public static KatanaSlashEntity spawn(Player owner, int variant, Vec3 origin,
                                          Vec3 direction, float damage, int lifetime) {
        KatanaSlashEntity slash = new KatanaSlashEntity(ModEntities.KATANA_SLASH, owner.level());
        slash.setPos(origin.x, origin.y, origin.z);
        slash.configure(owner, variant, direction, damage, lifetime);
        owner.level().addFreshEntity(slash);
        return slash;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.maxLife <= 0) {
            this.maxLife = getVariant() == GOLD ? 22 : getVariant() == REFINED ? 14 : 24;
        }
        if (this.tickCount > this.maxLife) {
            if (!this.level().isClientSide && getVariant() == GOLD) {
                finishGoldenRupture();
            }
            this.discard();
            return;
        }

        Vec3 movement = this.getDeltaMovement();
        if (getVariant() == GOLD) {
            followGround();
            movement = this.getDeltaMovement();
        }
        this.move(MoverType.SELF, movement);
        if (this.horizontalCollision || this.verticalCollision) {
            if (!this.level().isClientSide && getVariant() == GOLD) {
                finishGoldenRupture();
            }
            this.discard();
            return;
        }

        if (this.level().isClientSide) {
            spawnClientTrail();
            return;
        }
        resolveOwner();
        damageNearby();
        if (getVariant() == GOLD && this.tickCount % 2 == 0 && this.level() instanceof ServerLevel server) {
            for (int y = 0; y < 7; y++) {
                server.sendParticles(GOLD_DUST, this.getX(), this.getY() + y * 0.65D, this.getZ(),
                        2, 0.18D, 0.12D, 0.18D, 0.02D);
            }
        }
    }

    private void followGround() {
        Vec3 horizontal = new Vec3(this.getDeltaMovement().x, 0.0D, this.getDeltaMovement().z);
        Vec3 rayStart = this.position().add(horizontal).add(0.0D, 2.25D, 0.0D);
        Vec3 rayEnd = rayStart.add(0.0D, -5.0D, 0.0D);
        BlockHitResult ground = this.level().clip(new ClipContext(rayStart, rayEnd,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (ground.getType() == HitResult.Type.BLOCK) {
            double targetY = ground.getLocation().y + 0.12D;
            double correction = Math.max(-0.55D, Math.min(0.55D, targetY - this.getY()));
            this.setDeltaMovement(horizontal.x, correction, horizontal.z);
        } else {
            this.setDeltaMovement(horizontal.x, -0.22D, horizontal.z);
        }
    }

    private void damageNearby() {
        double inflate = getVariant() == GOLD ? 2.1D : getVariant() == REFINED ? 0.9D : 1.25D;
        AABB hitbox = this.getBoundingBox().inflate(inflate, getVariant() == GOLD ? 2.5D : 1.0D, inflate);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, hitbox,
                living -> living.isAlive() && !living.isSpectator() && !isOwner(living))) {
            if (!this.hitEntities.add(target.getUUID())) {
                continue;
            }
            DamageSource source = this.owner instanceof Player player
                    ? this.level().damageSources().playerAttack(player)
                    : this.level().damageSources().magic();
            if (target.hurt(source, this.slashDamage)) {
                Vec3 push = this.getDeltaMovement().normalize();
                target.push(push.x * 1.3D, getVariant() == GOLD ? 0.55D : 0.18D, push.z * 1.3D);
            }
        }
    }

    private void finishGoldenRupture() {
        if (this.ruptureFinished || !(this.level() instanceof ServerLevel server)) {
            return;
        }
        this.ruptureFinished = true;
        this.level().playSound(null, this.blockPosition(), ModSounds.KATANA_ULTIMATE,
                SoundSource.PLAYERS, 1.4F, 1.3F);
        server.sendParticles(ParticleTypes.FLASH, this.getX(), this.getY() + 2.0D, this.getZ(),
                1, 0.0D, 0.0D, 0.0D, 0.0D);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(4.5D, 7.0D, 4.5D),
                living -> living.isAlive() && !living.isSpectator() && !isOwner(living))) {
            DamageSource source = this.owner instanceof Player player
                    ? this.level().damageSources().playerAttack(player)
                    : this.level().damageSources().magic();
            target.hurt(source, Math.max(10.0F, this.slashDamage));
            target.push(0.0D, 1.35D, 0.0D);
        }
    }

    private boolean isOwner(LivingEntity living) {
        return this.ownerId != null && this.ownerId.equals(living.getUUID());
    }

    private void resolveOwner() {
        if (this.owner == null && this.ownerId != null && this.level() instanceof ServerLevel server) {
            Entity entity = server.getEntity(this.ownerId);
            if (entity instanceof LivingEntity living) {
                this.owner = living;
            }
        }
    }

    private void spawnClientTrail() {
        DustParticleOptions dust = getVariant() == GOLD ? GOLD_DUST
                : getVariant() == REFINED ? REFINED_DUST : OPUS_DUST;
        int count = getVariant() == GOLD ? 4 : 2;
        for (int i = 0; i < count; i++) {
            this.level().addParticle(dust,
                    this.getX() + (this.random.nextDouble() - 0.5D) * 0.7D,
                    this.getY() + 0.3D + this.random.nextDouble() * 1.1D,
                    this.getZ() + (this.random.nextDouble() - 0.5D) * 0.7D,
                    -this.getDeltaMovement().x * 0.12D, 0.02D,
                    -this.getDeltaMovement().z * 0.12D);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.entityData.set(VARIANT, tag.getInt("SlashVariant"));
        this.slashDamage = tag.getFloat("SlashDamage");
        this.maxLife = tag.getInt("SlashLife");
        if (tag.hasUUID("SlashOwner")) {
            this.ownerId = tag.getUUID("SlashOwner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("SlashVariant", getVariant());
        tag.putFloat("SlashDamage", this.slashDamage);
        tag.putInt("SlashLife", this.maxLife);
        if (this.ownerId != null) {
            tag.putUUID("SlashOwner", this.ownerId);
        }
    }

    @Override public boolean isPickable() { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public boolean hurt(DamageSource source, float amount) { return false; }
    @Override public PushReaction getPistonPushReaction() { return PushReaction.IGNORE; }

    @Override
    public AABB getBoundingBoxForCulling() {
        double size = getVariant() == GOLD ? 7.0D : 4.0D;
        return this.getBoundingBox().inflate(size);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "katana_slash", 0, state -> {
            state.getController().setAnimation(TRAVEL);
            return PlayState.CONTINUE;
        }));
    }

    @Override public double getTick(Object object) { return this.tickCount; }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }
}

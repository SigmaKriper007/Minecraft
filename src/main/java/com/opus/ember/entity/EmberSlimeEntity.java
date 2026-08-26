package com.opus.ember.entity;

import com.opus.ember.registry.EmberParticles;
import com.opus.ember.sound.EmberSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class EmberSlimeEntity extends Monster implements GeoAnimatable {
    private static final EntityDataAccessor<Integer> SIZE = SynchedEntityData.defineId(EmberSlimeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CHARGE = SynchedEntityData.defineId(EmberSlimeEntity.class, EntityDataSerializers.INT);
    private static final int CHARGE_DURATION = 50;

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation HOP = RawAnimation.begin().thenLoop("hop");
    private static final RawAnimation CHARGING = RawAnimation.begin().thenLoop("charge");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean detonated;

    public EmberSlimeEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 3;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SIZE, 1);
        entityData.define(CHARGE, 0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.15, false));
        goalSelector.addGoal(2, new RandomStrollGoal(this, 0.75));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 10.0f));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 12.0)
            .add(Attributes.MOVEMENT_SPEED, 0.25)
            .add(Attributes.ATTACK_DAMAGE, 3.0)
            .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    public int getSlimeSize() {
        return entityData.get(SIZE);
    }

    public int getChargeTicks() {
        return entityData.get(CHARGE);
    }

    public boolean isCharging() {
        return getChargeTicks() > 0;
    }

    public float getChargeProgress() {
        return isCharging() ? 1.0f - getChargeTicks() / (float) CHARGE_DURATION : 0.0f;
    }

    public void setSize(int requested) {
        setSize(requested, true);
    }

    private void setSize(int requested, boolean heal) {
        int size = Math.max(1, Math.min(3, requested));
        entityData.set(SIZE, size);
        refreshDimensions();
        if (!level().isClientSide) {
            setBase(Attributes.MAX_HEALTH, switch (size) { case 1 -> 12.0; case 2 -> 24.0; default -> 42.0; });
            setBase(Attributes.ATTACK_DAMAGE, switch (size) { case 1 -> 3.0; case 2 -> 5.0; default -> 8.0; });
            setBase(Attributes.MOVEMENT_SPEED, switch (size) { case 1 -> 0.27; case 2 -> 0.23; default -> 0.19; });
            if (heal) setHealth(getMaxHealth());
        }
    }

    private void setBase(net.minecraft.world.entity.ai.attributes.Attribute attribute, double value) {
        var instance = getAttribute(attribute);
        if (instance != null) instance.setBaseValue(value);
    }

    @Override
    public SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType reason, SpawnGroupData data, CompoundTag tag) {
        setSize(reason == MobSpawnType.SPAWN_EGG ? 1 : random.nextInt(10) == 0 ? 3 : random.nextBoolean() ? 2 : 1);
        return super.finalizeSpawn(level, difficulty, reason, data, tag);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return switch (getSlimeSize()) {
            case 1 -> EntityDimensions.scalable(0.70f, 0.65f);
            case 2 -> EntityDimensions.scalable(1.15f, 1.05f);
            default -> EntityDimensions.scalable(1.70f, 1.55f);
        };
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (SIZE.equals(key)) refreshDimensions();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide) return super.hurt(source, amount);
        if (isCharging()) {
            entityData.set(CHARGE, Math.max(6, getChargeTicks() - Math.min(8, Math.max(1, (int) Math.ceil(amount)))));
            return true;
        }
        if (amount >= getHealth() && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            setHealth(1.0f);
            beginCharge();
            return true;
        }
        return super.hurt(source, amount);
    }

    private void beginCharge() {
        entityData.set(CHARGE, CHARGE_DURATION);
        navigation.stop();
        setTarget(null);
        level().playSound(null, this, EmberSounds.EMBER_SLIME_CHARGE, SoundSource.HOSTILE, 1.1f, 1.25f - getSlimeSize() * 0.14f);
    }

    @Override
    public void tick() {
        super.tick();
        if (!isCharging()) return;
        setDeltaMovement(0.0, getDeltaMovement().y, 0.0);
        if (level() instanceof ServerLevel server) {
            int left = getChargeTicks() - 1;
            entityData.set(CHARGE, left);
            int density = 1 + (CHARGE_DURATION - left) / 12;
            if (tickCount % 3 == 0) {
                server.sendParticles(EmberParticles.EMBER_SPARK, getX(), getY() + getBbHeight() * 0.52, getZ(), density,
                    getBbWidth() * 0.3, getBbHeight() * 0.2, getBbWidth() * 0.3, 0.03);
            }
            if (left <= 0) detonate();
        }
    }

    private void detonate() {
        if (detonated) return;
        detonated = true;
        float strength = switch (getSlimeSize()) { case 1 -> 2.8f; case 2 -> 4.2f; default -> 6.0f; };
        if (level() instanceof ServerLevel server) {
            double y = getY() + getBbHeight() * 0.4;
            int scale = getSlimeSize();
            server.sendParticles(EmberParticles.EMBER_SPARK, getX(), y, getZ(), 18 + scale * 14,
                getBbWidth() * 0.55, getBbHeight() * 0.4, getBbWidth() * 0.55, 0.22 + scale * 0.04);
            server.sendParticles(EmberParticles.EMBER_ASH, getX(), y, getZ(), 10 + scale * 7,
                getBbWidth() * 0.45, getBbHeight() * 0.32, getBbWidth() * 0.45, 0.12);
            server.sendParticles(net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE, getX(), y, getZ(),
                6 + scale * 5, getBbWidth() * 0.5, 0.3, getBbWidth() * 0.5, 0.07);
        }
        level().explode(this, getX(), getY() + getBbHeight() * 0.4, getZ(), strength, true, Level.ExplosionInteraction.BLOCK);
        level().playSound(null, this, EmberSounds.EMBER_SLIME_EXPLODE, SoundSource.HOSTILE, 1.6f, 1.15f - getSlimeSize() * 0.1f);
        discard();
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "body", 2, this::selectAnimation));
    }

    private PlayState selectAnimation(AnimationState<EmberSlimeEntity> state) {
        state.getController().setAnimation(isCharging() ? CHARGING : state.isMoving() ? HOP : IDLE);
        return PlayState.CONTINUE;
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
    @Override public double getTick(Object ignored) { return tickCount; }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Size", getSlimeSize());
        tag.putInt("Charge", getChargeTicks());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setSize(tag.getInt("Size"), false);
        entityData.set(CHARGE, Math.max(0, tag.getInt("Charge")));
    }
}

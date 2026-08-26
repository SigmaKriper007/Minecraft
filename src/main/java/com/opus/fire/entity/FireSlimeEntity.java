package com.opus.fire.entity;

import com.opus.fire.registry.FireParticles;
import com.opus.fire.sound.FireSounds;
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
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Cinder Slime — мультяшный прыгающий слайм в огненном биоме.
 *
 * Передвижение «рывками»: пауза → быстрое приседание → прыжок → полёт → удар
 * о землю с расплющиванием → снова пауза. Состояние синхронизируется на клиент
 * и выбирает отдельный клип анимации.
 *
 * Смертельная детонация «как у крипера»: раздувание раковины с трещинами,
 * шипение-зарядка, белая вспышка в конце, затем блоковый взрыв с эмбер-искрами,
 * пеплом и ударной вспышкой.
 */
public final class FireSlimeEntity extends Monster implements GeoAnimatable {
    private static final EntityDataAccessor<Integer> SIZE = SynchedEntityData.defineId(FireSlimeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CHARGE = SynchedEntityData.defineId(FireSlimeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(FireSlimeEntity.class, EntityDataSerializers.INT);
    private static final int CHARGE_DURATION = 50;
    private static final int FLASH_FROM = 14;

    private static final int ANIM_IDLE = 0;
    private static final int ANIM_COMPRESS = 1;
    private static final int ANIM_AIRBORNE = 2;
    private static final int ANIM_LAND = 3;
    private static final int ANIM_ATTACK = 4;
    private static final int ANIM_CHARGE = 5;
    private static final int ANIM_DEATH = 6;

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation COMPRESSION = RawAnimation.begin().thenPlay("compression");
    private static final RawAnimation AIRBORNE = RawAnimation.begin().thenLoop("airborne");
    private static final RawAnimation LAND = RawAnimation.begin().thenPlay("land");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    private static final RawAnimation CHARGING = RawAnimation.begin().thenLoop("charge");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlay("death");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean detonated;
    private boolean wasOnGround;
    private int hopCooldown = 14;
    private int compressionTicks;
    private int landingTicks;
    private int attackAnimationTicks;
    private int deathTicks;

    public FireSlimeEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 3;
    }

    @Override protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SIZE, 1);
        entityData.define(CHARGE, 0);
        entityData.define(ANIM_STATE, ANIM_IDLE);
    }

    @Override protected void registerGoals() {
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

    public int getSlimeSize() { return entityData.get(SIZE); }
    public int getChargeTicks() { return entityData.get(CHARGE); }
    public boolean isCharging() { return getChargeTicks() > 0; }
    /** 0..1 прогресс детонации (для раздувания раковины в модели). */
    public float getChargeProgress() { return isCharging() ? 1.0f - getChargeTicks() / (float) CHARGE_DURATION : 0.0f; }
    /** Последние {FLASH_FROM} тиков — криперская белая вспышка. */
    public boolean isFlashing() { return isCharging() && getChargeTicks() <= FLASH_FROM; }
    public int getAnimState() { return entityData.get(ANIM_STATE); }

    public void setSize(int requested) { setSize(requested, true); }

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

    @Override public SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level, DifficultyInstance difficulty,
                                                  MobSpawnType reason, SpawnGroupData data, CompoundTag tag) {
        setSize(reason == MobSpawnType.SPAWN_EGG ? 1 : random.nextInt(10) == 0 ? 3 : random.nextBoolean() ? 2 : 1);
        return super.finalizeSpawn(level, difficulty, reason, data, tag);
    }

    @Override public EntityDimensions getDimensions(Pose pose) {
        return switch (getSlimeSize()) {
            case 1 -> EntityDimensions.scalable(0.70f, 0.65f);
            case 2 -> EntityDimensions.scalable(1.15f, 1.05f);
            default -> EntityDimensions.scalable(1.70f, 1.55f);
        };
    }

    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (SIZE.equals(key)) refreshDimensions();
    }

    @Override public boolean hurt(DamageSource source, float amount) {
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

    /** Криперская зарядка: шипение + стоп, в конце — белая вспышка. */
    private void beginCharge() {
        entityData.set(CHARGE, CHARGE_DURATION);
        entityData.set(ANIM_STATE, ANIM_CHARGE);
        navigation.stop();
        setTarget(null);
        level().playSound(null, this, FireSounds.SLIME_CHARGE, SoundSource.HOSTILE, 1.1f, 1.25f - getSlimeSize() * 0.14f);
    }

    @Override public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (isDeadOrDying()) {
            entityData.set(ANIM_STATE, ANIM_DEATH);
            if (++deathTicks > 22) discard();
            return;
        }
        if (isCharging()) {
            tickCharge();
            return;
        }
        tickHopMotion();
    }

    private void tickCharge() {
        if (level() instanceof ServerLevel server) {
            int left = getChargeTicks() - 1;
            entityData.set(CHARGE, left);
            entityData.set(ANIM_STATE, ANIM_CHARGE);
            setDeltaMovement(0.0, getDeltaMovement().y, 0.0);
            double y = getY() + getBbHeight() * 0.52;
            int density = 1 + (CHARGE_DURATION - left) / 12;
            if (tickCount % 3 == 0) {
                server.sendParticles(FireParticles.EMBER, getX(), y, getZ(), density,
                    getBbWidth() * 0.3, getBbHeight() * 0.2, getBbWidth() * 0.3, 0.03);
            }
            // криперская белая вспышка в последние тики
            if (left <= FLASH_FROM && tickCount % 2 == 0) {
                server.sendParticles(net.minecraft.core.particles.ParticleTypes.FLASH, getX(), y, getZ(), 1, 0, 0, 0, 0);
                server.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD, getX(), y, getZ(),
                    5, getBbWidth() * 0.3, getBbHeight() * 0.2, getBbWidth() * 0.3, 0.05);
            }
            if (left == FLASH_FROM) {
                level().playSound(null, this, FireSounds.SLIME_CHARGE, SoundSource.HOSTILE, 1.5f,
                    1.9f - getSlimeSize() * 0.1f);
            }
            if (left <= 0) detonate();
        }
    }

    /** Рывковый цикл: пауза → присед → прыжок → полёт → приземление. */
    private void tickHopMotion() {
        if (attackAnimationTicks > 0) {
            attackAnimationTicks--;
            entityData.set(ANIM_STATE, ANIM_ATTACK);
            return;
        }

        if (!onGround()) {
            wasOnGround = false;
            entityData.set(ANIM_STATE, ANIM_AIRBORNE);
            return;
        }

        if (!wasOnGround) {
            wasOnGround = true;
            landingTicks = 5;
            hopCooldown = 10 + random.nextInt(8);
        }
        if (landingTicks > 0) {
            landingTicks--;
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            entityData.set(ANIM_STATE, ANIM_LAND);
            return;
        }
        if (compressionTicks > 0) {
            compressionTicks--;
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            entityData.set(ANIM_STATE, ANIM_COMPRESS);
            if (compressionTicks == 0) launchHop();
            return;
        }

        if (hopCooldown > 0) hopCooldown--;
        boolean wantsToMove = (getTarget() != null && getTarget().isAlive() && distanceToSqr(getTarget()) > 1.4D)
            || !getNavigation().isDone();
        if (wantsToMove && hopCooldown <= 0) {
            compressionTicks = 5;
            entityData.set(ANIM_STATE, ANIM_COMPRESS);
            return;
        }
        setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
        entityData.set(ANIM_STATE, ANIM_IDLE);
    }

    private void launchHop() {
        Vec3 direction;
        if (getTarget() != null && getTarget().isAlive()) {
            direction = getTarget().position().subtract(position()).multiply(1.0D, 0.0D, 1.0D);
        } else {
            direction = getLookAngle().multiply(1.0D, 0.0D, 1.0D);
        }
        if (direction.lengthSqr() < 1.0E-4D) direction = new Vec3(0.0D, 0.0D, 1.0D);
        double horizontal = 0.22D + getSlimeSize() * 0.04D;
        double vertical = 0.42D + getSlimeSize() * 0.05D;
        direction = direction.normalize();
        setDeltaMovement(direction.x * horizontal, vertical, direction.z * horizontal);
        hasImpulse = true;
        wasOnGround = false;
        entityData.set(ANIM_STATE, ANIM_AIRBORNE);
    }

    @Override public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && !level().isClientSide) {
            attackAnimationTicks = 8;
            entityData.set(ANIM_STATE, ANIM_ATTACK);
        }
        return hit;
    }

    private void detonate() {
        if (detonated) return;
        detonated = true;
        float strength = switch (getSlimeSize()) { case 1 -> 2.8f; case 2 -> 4.2f; default -> 6.0f; };
        if (level() instanceof ServerLevel server) {
            double y = getY() + getBbHeight() * 0.4;
            int scale = getSlimeSize();
            // криперский взрыв: вспышка-эмиттер + искры + пепел + дым
            server.sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER, getX(), y, getZ(), 1, 0, 0, 0, 0);
            server.sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION, getX(), y, getZ(), 6 + scale * 3,
                getBbWidth() * 0.4, getBbHeight() * 0.3, getBbWidth() * 0.4, 0);
            server.sendParticles(net.minecraft.core.particles.ParticleTypes.FLASH, getX(), y, getZ(), 3, 0, 0, 0, 0);
            server.sendParticles(FireParticles.EMBER, getX(), y, getZ(), 22 + scale * 18,
                getBbWidth() * 0.55, getBbHeight() * 0.4, getBbWidth() * 0.55, 0.24 + scale * 0.05);
            server.sendParticles(FireParticles.ASH, getX(), y, getZ(), 12 + scale * 8,
                getBbWidth() * 0.45, getBbHeight() * 0.32, getBbWidth() * 0.45, 0.12);
            server.sendParticles(net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE, getX(), y, getZ(),
                8 + scale * 6, getBbWidth() * 0.5, 0.3, getBbWidth() * 0.5, 0.08);
        }
        level().explode(this, getX(), getY() + getBbHeight() * 0.4, getZ(), strength, true, Level.ExplosionInteraction.BLOCK);
        level().playSound(null, this, FireSounds.SLIME_EXPLODE, SoundSource.HOSTILE, 1.7f, 1.15f - getSlimeSize() * 0.1f);
        discard();
    }

    @Override public boolean fireImmune() { return true; }

    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "body", 2, this::selectAnimation));
    }

    private PlayState selectAnimation(AnimationState<FireSlimeEntity> state) {
        RawAnimation animation = switch (getAnimState()) {
            case ANIM_COMPRESS -> COMPRESSION;
            case ANIM_AIRBORNE -> AIRBORNE;
            case ANIM_LAND -> LAND;
            case ANIM_ATTACK -> ATTACK;
            case ANIM_CHARGE -> CHARGING;
            case ANIM_DEATH -> DEATH;
            default -> IDLE;
        };
        state.getController().setAnimation(animation);
        return PlayState.CONTINUE;
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
    @Override public double getTick(Object ignored) { return tickCount; }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Size", getSlimeSize());
        tag.putInt("Charge", getChargeTicks());
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setSize(tag.getInt("Size"), false);
        entityData.set(CHARGE, Math.max(0, tag.getInt("Charge")));
    }
}
package com.opus.ember.entity;

import com.opus.ember.EmberLine;
import com.opus.ember.entity.projectile.BlazingTridentEntity;
import com.opus.ember.entity.projectile.EmberAuraWaveEntity;
import com.opus.ember.entity.projectile.EmberFireballProjectile;
import com.opus.ember.registry.EmberEntities;
import com.opus.ember.registry.EmberParticles;
import com.opus.ember.sound.EmberSounds;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class FlameDemonEntity extends Monster implements GeoAnimatable {
    public static final float MAX_ICE_HEALTH = 150.0f;

    public enum Action {
        NONE(0, 0), AWAKEN(1, 40), MELEE(2, 18), TOSS(3, 30), FIREBALL(4, 34),
        AURA(5, 52), TRIDENT(6, 36), SUMMON(7, 44), DEATH(8, 50);
        final int id;
        final int duration;
        Action(int id, int duration) { this.id = id; this.duration = duration; }
        static Action byId(int id) {
            for (Action action : values()) if (action.id == id) return action;
            return NONE;
        }
    }

    private static final EntityDataAccessor<Boolean> AWAKENED = SynchedEntityData.defineId(FlameDemonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> ICE_HEALTH = SynchedEntityData.defineId(FlameDemonEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> ACTION = SynchedEntityData.defineId(FlameDemonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ACTION_TICK = SynchedEntityData.defineId(FlameDemonEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation SEALED = RawAnimation.begin().thenLoop("sealed");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final ServerBossEvent iceBar = new ServerBossEvent(Component.translatable("entity.opusvsexe.flame_demon_ice"),
        BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
    private final ServerBossEvent demonBar = new ServerBossEvent(Component.translatable("entity.opusvsexe.flame_demon"),
        BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.PROGRESS);

    private int tossCooldown;
    private int fireballCooldown;
    private int auraCooldown;
    private int tridentCooldown;
    private int summonCooldown;

    public FlameDemonEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        xpReward = 300;
        demonBar.setVisible(false);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(AWAKENED, false);
        entityData.define(ICE_HEALTH, MAX_ICE_HEALTH);
        entityData.define(ACTION, Action.NONE.id);
        entityData.define(ACTION_TICK, 0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.05, false));
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.65));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 24.0f));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 400.0)
            .add(Attributes.MOVEMENT_SPEED, 0.30)
            .add(Attributes.ATTACK_DAMAGE, 12.0)
            .add(Attributes.ARMOR, 8.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
            .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    public boolean isAwakened() { return entityData.get(AWAKENED); }
    public float getIceHealth() { return entityData.get(ICE_HEALTH); }
    public float getIceProgress() { return getIceHealth() / MAX_ICE_HEALTH; }
    public Action getAction() { return Action.byId(entityData.get(ACTION)); }
    public int getActionTick() { return entityData.get(ACTION_TICK); }

    private void setAction(Action action) {
        entityData.set(ACTION, action.id);
        entityData.set(ACTION_TICK, 0);
        navigation.stop();
    }

    @Override
    protected net.minecraft.resources.ResourceLocation getDefaultLootTable() {
        return EmberLine.id("entities/flame_demon");
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide) return false;
        if (!isAwakened()) {
            if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return super.hurt(source, amount);
            if (getAction() == Action.AWAKEN || source.is(DamageTypeTags.IS_FIRE)) return false;
            float remaining = Math.max(0.0f, getIceHealth() - amount);
            entityData.set(ICE_HEALTH, remaining);
            iceBar.setProgress(remaining / MAX_ICE_HEALTH);
            if (level() instanceof ServerLevel server) {
                server.sendParticles(EmberParticles.EMBER_ASH, getX(), getY() + 1.8, getZ(), 8, 0.65, 1.2, 0.65, 0.04);
                server.playSound(null, this, EmberSounds.FLAME_DEMON_HIT, SoundSource.HOSTILE, 0.9f, 1.45f);
            }
            if (remaining <= 0.0f) beginAwakening();
            return true;
        }
        boolean accepted = super.hurt(source, amount);
        if (accepted) level().playSound(null, this, EmberSounds.FLAME_DEMON_HURT, SoundSource.HOSTILE, 1.1f, 0.86f);
        return accepted;
    }

    private void beginAwakening() {
        setAction(Action.AWAKEN);
        iceBar.setVisible(false);
        if (level() instanceof ServerLevel server) {
            server.playSound(null, this, EmberSounds.FLAME_DEMON_ROAR, SoundSource.HOSTILE, 2.8f, 0.78f);
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!isAwakened() || getAction() != Action.NONE) return false;
        setAction(Action.MELEE);
        swing(InteractionHand.MAIN_HAND);
        return true;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (!isAwakened()) {
            navigation.stop();
            setDeltaMovement(0.0, getDeltaMovement().y, 0.0);
            if (getAction() == Action.AWAKEN) tickAction();
            return;
        }
        decrementCooldowns();
        if (getAction() != Action.NONE) {
            tickAction();
            return;
        }
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) return;
        double distance = distanceToSqr(target);
        if (tickCount % 10 == 0) chooseAction(distance);
    }

    private void decrementCooldowns() {
        if (tossCooldown > 0) tossCooldown--;
        if (fireballCooldown > 0) fireballCooldown--;
        if (auraCooldown > 0) auraCooldown--;
        if (tridentCooldown > 0) tridentCooldown--;
        if (summonCooldown > 0) summonCooldown--;
    }

    private void chooseAction(double distance) {
        int roll = random.nextInt(100);
        if (distance <= 20.0 && tossCooldown == 0 && roll < 24) {
            tossCooldown = 140; setAction(Action.TOSS);
        } else if (distance <= 110.0 && auraCooldown == 0 && roll < 42) {
            auraCooldown = 200; setAction(Action.AURA);
        } else if (distance >= 120.0 && tridentCooldown == 0 && roll < 62) {
            tridentCooldown = 160; setAction(Action.TRIDENT);
        } else if (summonCooldown == 0 && roll < 76 && nearbyMinions() < 8) {
            summonCooldown = 300; setAction(Action.SUMMON);
        } else if (fireballCooldown == 0 && roll < 92) {
            fireballCooldown = 100; setAction(Action.FIREBALL);
        }
    }

    private int nearbyMinions() {
        return level().getEntitiesOfClass(EmberSlimeEntity.class, getBoundingBox().inflate(24.0), Entity::isAlive).size();
    }

    private void tickAction() {
        Action action = getAction();
        int tick = getActionTick() + 1;
        entityData.set(ACTION_TICK, tick);
        setDeltaMovement(0.0, getDeltaMovement().y, 0.0);
        lookAtTarget();

        switch (action) {
            case AWAKEN -> tickAwaken(tick);
            case MELEE -> { if (tick == 9) meleeImpact(12.0f, 4.4); }
            case TOSS -> { if (tick == 16) tossImpact(); }
            case FIREBALL -> { if (tick == 20) launchFireball(); }
            case AURA -> { if (tick == 24 || tick == 30 || tick == 36) launchAura(); }
            case TRIDENT -> { if (tick == 17) launchTrident(); }
            case SUMMON -> { if (tick == 24) summonSlimes(); }
            default -> { }
        }
        if (action != Action.AWAKEN && tick >= action.duration) setAction(Action.NONE);
    }

    private void lookAtTarget() {
        LivingEntity target = getTarget();
        if (target != null) getLookControl().setLookAt(target, 30.0f, 30.0f);
    }

    private void tickAwaken(int tick) {
        if (!(level() instanceof ServerLevel server)) return;
        BlockParticleOption cryoShard = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.CRYING_OBSIDIAN.defaultBlockState());
        if (tick == 12) {
            server.sendParticles(EmberParticles.EMBER_ASH, getX(), getY() + 1.8, getZ(), 30, 1.1, 1.7, 1.1, 0.16);
            server.sendParticles(cryoShard, getX(), getY() + 1.8, getZ(), 18, 0.8, 1.4, 0.8, 0.08);
        }
        if (tick == 24) {
            server.sendParticles(EmberParticles.EMBER_SPARK, getX(), getY() + 1.8, getZ(), 40, 1.4, 1.8, 1.4, 0.22);
            server.sendParticles(cryoShard, getX(), getY() + 1.8, getZ(), 28, 1.0, 1.7, 1.0, 0.16);
        }
        if (tick >= Action.AWAKEN.duration) {
            server.sendParticles(cryoShard, getX(), getY() + 2.0, getZ(), 54, 1.6, 2.0, 1.6, 0.32);
            server.playSound(null, this, EmberSounds.FLAME_DEMON_AURA, SoundSource.HOSTILE, 2.0f, 0.68f);
            entityData.set(AWAKENED, true);
            setAction(Action.NONE);
            demonBar.setProgress(1.0f);
            demonBar.setVisible(true);
        }
    }

    private void meleeImpact(float damage, double reach) {
        LivingEntity target = getTarget();
        if (target == null || distanceToSqr(target) > reach * reach) return;
        target.hurt(damageSources().mobAttack(this), damage);
        target.setSecondsOnFire(4);
        level().playSound(null, this, EmberSounds.FLAME_DEMON_HIT, SoundSource.HOSTILE, 1.4f, 0.8f);
    }

    private void tossImpact() {
        LivingEntity target = getTarget();
        if (target == null || distanceToSqr(target) > 36.0) return;
        target.hurt(damageSources().mobAttack(this), 8.0f);
        Vec3 away = target.position().subtract(position()).normalize();
        target.setDeltaMovement(away.x * 1.45, 1.05, away.z * 1.45);
        target.hurtMarked = true;
        target.setSecondsOnFire(5);
    }

    private void launchFireball() {
        LivingEntity target = getTarget();
        if (target == null) return;
        Vec3 origin = position().add(0.75, 2.55, 0.0);
        Vec3 aim = target.getEyePosition().subtract(origin).normalize();
        EmberFireballProjectile projectile = new EmberFireballProjectile(level(), this, aim.x, aim.y, aim.z);
        projectile.setPos(origin);
        level().addFreshEntity(projectile);
        level().playSound(null, this, EmberSounds.EMBER_FIREBALL_LAUNCH, SoundSource.HOSTILE, 1.5f, 0.75f);
    }

    private void launchAura() {
        EmberAuraWaveEntity wave = new EmberAuraWaveEntity(level(), this);
        wave.setPos(getX(), getY() + 0.15, getZ());
        level().addFreshEntity(wave);
    }

    private void launchTrident() {
        LivingEntity target = getTarget();
        if (target == null) return;
        BlazingTridentEntity trident = new BlazingTridentEntity(EmberEntities.BLAZING_TRIDENT, level(), this);
        Vec3 origin = position().add(0.75, 2.45, 0.0);
        Vec3 aim = target.getEyePosition().subtract(origin).normalize();
        trident.setPos(origin);
        trident.shoot(aim.x, aim.y + 0.08, aim.z, 2.05f, 1.0f);
        level().addFreshEntity(trident);
    }

    private void summonSlimes() {
        for (int i = 0; i < 5; i++) {
            double angle = i * Math.PI * 2.0 / 5.0;
            EmberSlimeEntity slime = new EmberSlimeEntity(EmberEntities.EMBER_SLIME, level());
            slime.setSize(1);
            slime.setPos(getX() + Math.cos(angle) * 3.5, getY() + 0.2, getZ() + Math.sin(angle) * 3.5);
            level().addFreshEntity(slime);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            demonBar.setProgress(getHealth() / getMaxHealth());
            iceBar.setProgress(getIceProgress());
            iceBar.setVisible(!isAwakened() && getAction() != Action.AWAKEN);
            demonBar.setVisible(isAwakened());
            if (isAwakened() && isInWaterOrRain() && tickCount % 20 == 0) hurt(damageSources().drown(), 4.0f);
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        iceBar.addPlayer(player);
        demonBar.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        iceBar.removePlayer(player);
        demonBar.removePlayer(player);
    }

    @Override public boolean fireImmune() { return true; }
    @Override public boolean isAlliedTo(Entity other) { return other instanceof EmberSlimeEntity || other instanceof ObsidianGolemEntity || super.isAlliedTo(other); }
    @Override public boolean canAttack(LivingEntity target) { return !(target instanceof EmberSlimeEntity) && !(target instanceof ObsidianGolemEntity) && super.canAttack(target); }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "body", 2, this::selectAnimation));
    }

    private PlayState selectAnimation(AnimationState<FlameDemonEntity> state) {
        Action action = getAction();
        if (!isAwakened() && action == Action.NONE) state.getController().setAnimation(SEALED);
        else if (action != Action.NONE) state.getController().setAnimation(RawAnimation.begin().thenPlay(action.name().toLowerCase()));
        else state.getController().setAnimation(state.isMoving() ? WALK : IDLE);
        return PlayState.CONTINUE;
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
    @Override public double getTick(Object ignored) { return tickCount; }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Awakened", isAwakened());
        tag.putFloat("IceHealth", getIceHealth());
        tag.putInt("Action", getAction().id);
        tag.putInt("ActionTick", getActionTick());
        tag.putIntArray("Cooldowns", new int[]{tossCooldown, fireballCooldown, auraCooldown, tridentCooldown, summonCooldown});
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(AWAKENED, tag.getBoolean("Awakened"));
        entityData.set(ICE_HEALTH, tag.contains("IceHealth") ? tag.getFloat("IceHealth") : MAX_ICE_HEALTH);
        entityData.set(ACTION, tag.getInt("Action"));
        entityData.set(ACTION_TICK, tag.getInt("ActionTick"));
        int[] cooldowns = tag.getIntArray("Cooldowns");
        if (cooldowns.length == 5) {
            tossCooldown = cooldowns[0]; fireballCooldown = cooldowns[1]; auraCooldown = cooldowns[2];
            tridentCooldown = cooldowns[3]; summonCooldown = cooldowns[4];
        }
        setNoAi(false);
    }
}

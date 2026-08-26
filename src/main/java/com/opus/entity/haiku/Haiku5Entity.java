package com.opus.entity.haiku;

import com.opus.item.CombatEffects;
import com.opus.sound.ModSounds;
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
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Haiku-5 "Titan Frame" - рейд-босс биома
 * ~8-10 блоков высотой, дропает Core Opus (необходим для EXO-5)
 * Появляется как самостоятельный мини-босс и как подмога в финальном бою
 */
public class Haiku5Entity extends HaikuMob {

    private static final byte ACTION_NONE = 0;
    private static final byte ACTION_QUAKE = 1;
    private static final byte ACTION_RUSH = 2;
    private static final EntityDataAccessor<Byte> ACTION =
            SynchedEntityData.defineId(Haiku5Entity.class, EntityDataSerializers.BYTE);

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.opusvsexe.haiku_5"),
            ServerBossEvent.BossBarColor.YELLOW, ServerBossEvent.BossBarOverlay.NOTCHED_10);
    private final Set<UUID> rushHits = new HashSet<>();
    private int actionTicks;
    private long nextAbilityTick;
    private Vec3 rushDirection = Vec3.ZERO;

    public Haiku5Entity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 80;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ACTION, ACTION_NONE);
    }

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    private static final RawAnimation SPECIAL_ANIM = RawAnimation.begin().thenPlay("special");
    private static final RawAnimation HURT_ANIM = RawAnimation.begin().thenPlay("hurt");
    private static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlay("death");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "titan_controller", 4, this::titanAnimationPredicate));
    }

    private PlayState titanAnimationPredicate(AnimationState<Haiku5Entity> state) {
        Haiku5Entity self = state.getAnimatable();
        if (self.isDeadOrDying()) {
            playOnce(state, DEATH_ANIM, false);
            return PlayState.CONTINUE;
        }
        byte action = self.entityData.get(ACTION);
        if (action == ACTION_QUAKE) {
            playOnce(state, SPECIAL_ANIM, true);
            return PlayState.CONTINUE;
        }
        if (action == ACTION_RUSH) {
            playOnce(state, ATTACK_ANIM, true);
            return PlayState.CONTINUE;
        }
        if (self.hurtTime > 0) {
            playOnce(state, HURT_ANIM, true);
            return PlayState.CONTINUE;
        }
        if (self.swinging) {
            playOnce(state, ATTACK_ANIM, true);
            return PlayState.CONTINUE;
        }
        if (state.isMoving()) {
            state.getController().setAnimation(WALK_ANIM);
            return PlayState.CONTINUE;
        }
        state.getController().setAnimation(IDLE_ANIM);
        return PlayState.CONTINUE;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.7, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.4));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 200.0)
            .add(Attributes.MOVEMENT_SPEED, 0.29)
            .add(Attributes.ATTACK_DAMAGE, 15.0)
            .add(Attributes.FOLLOW_RANGE, 40.0)
            .add(Attributes.ARMOR, 14.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.7);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 8.5f;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        if (!this.isAlive()) {
            return;
        }

        byte action = this.entityData.get(ACTION);
        if (action != ACTION_NONE) {
            runAction(action);
            return;
        }

        LivingEntity target = this.getTarget();
        long now = this.level().getGameTime();
        if (target != null && target.isAlive() && now >= this.nextAbilityTick) {
            double distance = this.distanceTo(target);
            if (distance <= 11.0D) {
                beginAction(ACTION_QUAKE, target, 140L);
            } else if (distance <= 30.0D) {
                beginAction(ACTION_RUSH, target, 110L);
            }
        }
    }

    private void beginAction(byte action, LivingEntity target, long cooldown) {
        this.entityData.set(ACTION, action);
        this.actionTicks = 0;
        this.nextAbilityTick = this.level().getGameTime() + cooldown;
        this.rushHits.clear();
        this.getNavigation().stop();

        Vec3 direction = target.position().subtract(this.position()).multiply(1.0D, 0.0D, 1.0D);
        this.rushDirection = direction.lengthSqr() < 0.01D
                ? this.getLookAngle().multiply(1.0D, 0.0D, 1.0D).normalize()
                : direction.normalize();
        this.getLookControl().setLookAt(target, 40.0F, 25.0F);
    }

    private void runAction(byte action) {
        this.actionTicks++;
        this.getNavigation().stop();
        if (action == ACTION_QUAKE) {
            runTitanQuake();
        } else if (action == ACTION_RUSH) {
            runAmberRush();
        }
    }

    private void runTitanQuake() {
        if (this.actionTicks == 1 || this.actionTicks == 7 || this.actionTicks == 13) {
            emitTelegraphRing(3.0D + this.actionTicks * 0.42D, 0.25D, 28, ParticleTypes.END_ROD);
        }
        if (this.actionTicks == 18) {
            emitTelegraphRing(9.0D, 0.3D, 44, ParticleTypes.ELECTRIC_SPARK);
            CombatEffects.shockwave(this, 9.0D, 7.0D, 14.0F, 3.0D, true);
            this.level().playSound(null, this.blockPosition(), ModSounds.BOSS_SLAM,
                    SoundSource.HOSTILE, 1.8F, 0.65F);
        }
        if (this.actionTicks >= 34) {
            finishAction();
        }
    }

    private void runAmberRush() {
        if (this.actionTicks == 1) {
            emitRushTelegraph();
            this.level().playSound(null, this.blockPosition(), ModSounds.BOSS_ROAR,
                    SoundSource.HOSTILE, 1.4F, 1.15F);
        }
        if (this.actionTicks >= 11 && this.actionTicks <= 20) {
            this.setDeltaMovement(this.rushDirection.x * 1.15D,
                    Math.max(this.getDeltaMovement().y, 0.0D), this.rushDirection.z * 1.15D);
            this.hurtMarked = true;
            for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(1.2D, 0.8D, 1.2D),
                    living -> living.isAlive() && living != this && !this.isAlliedTo(living))) {
                if (this.rushHits.add(target.getUUID())) {
                    target.hurt(this.level().damageSources().mobAttack(this), 13.0F);
                    target.push(this.rushDirection.x * 2.8D, 0.55D, this.rushDirection.z * 2.8D);
                }
            }
            if (this.actionTicks % 2 == 0 && this.level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        this.getX(), this.getY() + 1.0D, this.getZ(),
                        12, this.getBbWidth() * 0.35D, 0.3D, this.getBbWidth() * 0.35D, 0.08D);
            }
        }
        if (this.actionTicks >= 30) {
            finishAction();
        }
    }

    private void emitRushTelegraph() {
        if (!(this.level() instanceof ServerLevel server)) {
            return;
        }
        for (int i = 2; i <= 28; i += 2) {
            server.sendParticles(ParticleTypes.END_ROD,
                    this.getX() + this.rushDirection.x * i, this.getY() + 0.25D,
                    this.getZ() + this.rushDirection.z * i,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private void finishAction() {
        this.entityData.set(ACTION, ACTION_NONE);
        this.actionTicks = 0;
        this.rushHits.clear();
        this.setDeltaMovement(this.getDeltaMovement().multiply(0.2D, 1.0D, 0.2D));
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("TitanAction", this.entityData.get(ACTION));
        tag.putInt("TitanActionTicks", this.actionTicks);
        tag.putLong("TitanNextAbility", this.nextAbilityTick);
        tag.putDouble("TitanRushX", this.rushDirection.x);
        tag.putDouble("TitanRushZ", this.rushDirection.z);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(ACTION, tag.getByte("TitanAction"));
        this.actionTicks = tag.getInt("TitanActionTicks");
        this.nextAbilityTick = tag.getLong("TitanNextAbility");
        Vec3 loadedDirection = new Vec3(tag.getDouble("TitanRushX"), 0.0D, tag.getDouble("TitanRushZ"));
        this.rushDirection = loadedDirection.lengthSqr() > 0.01D ? loadedDirection.normalize() : Vec3.ZERO;
    }
}

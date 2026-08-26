package com.opus.entity.haiku;

import com.opus.item.CombatEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import com.opus.registry.ModItems;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

/**
 * Haiku-4 "Elite Warden" - мини-босс, колосс-страж (щитоносец)
 * Монолит ~4.2 блока: корпус-наковальня, голова-штурвал, плиты-крылья.
 * Дроп необходим для EXO-4 и Resonant Opus.
 */
public class Haiku4Entity extends HaikuMob {

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    private static final RawAnimation SPECIAL_ANIM = RawAnimation.begin().thenPlay("special");
    private static final RawAnimation HURT_ANIM = RawAnimation.begin().thenPlay("hurt");
    private static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlay("death");
    private static final EntityDataAccessor<Boolean> RESONANCE_GUARD =
            SynchedEntityData.defineId(Haiku4Entity.class, EntityDataSerializers.BOOLEAN);

    private int abilityTicks;
    private long nextAbilityTick;
    private long guardUntil;

    public Haiku4Entity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RESONANCE_GUARD, false);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "warden_controller", 4, this::wardenAnimationPredicate));
    }

    private PlayState wardenAnimationPredicate(AnimationState<Haiku4Entity> state) {
        Haiku4Entity self = state.getAnimatable();
        if (self.isDeadOrDying()) {
            playOnce(state, DEATH_ANIM, false);
            return PlayState.CONTINUE;
        }
        if (self.hurtTime > 0) {
            playOnce(state, HURT_ANIM, true);
            return PlayState.CONTINUE;
        }
        if (self.entityData.get(RESONANCE_GUARD)) {
            playOnce(state, SPECIAL_ANIM, true);
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
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.8, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 100.0)
            .add(Attributes.MOVEMENT_SPEED, 0.28)
            .add(Attributes.ATTACK_DAMAGE, 12.0)
            .add(Attributes.FOLLOW_RANGE, 35.0)
            .add(Attributes.ARMOR, 10.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 4.0f;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide || !this.isAlive()) {
            return;
        }

        long now = this.level().getGameTime();
        if (this.entityData.get(RESONANCE_GUARD)) {
            runResonanceGuard(now);
            return;
        }

        if (this.getTarget() != null && this.getTarget().isAlive()
                && this.distanceToSqr(this.getTarget()) <= 144.0D
                && now >= this.nextAbilityTick) {
            this.abilityTicks = 0;
            this.guardUntil = now + 50L;
            this.nextAbilityTick = now + 180L;
            this.entityData.set(RESONANCE_GUARD, true);
            this.getNavigation().stop();
        }
    }

    private void runResonanceGuard(long now) {
        this.abilityTicks++;
        this.getNavigation().stop();
        if (this.getTarget() != null) {
            this.getLookControl().setLookAt(this.getTarget(), 30.0F, 20.0F);
        }

        if (this.abilityTicks == 1 || this.abilityTicks == 7 || this.abilityTicks == 13) {
            emitTelegraphRing(1.8D + this.abilityTicks * 0.18D, 0.2D, 20, ParticleTypes.END_ROD);
        }
        if (this.abilityTicks == 18) {
            emitTelegraphRing(5.5D, 0.25D, 32, ParticleTypes.ELECTRIC_SPARK);
            CombatEffects.shockwave(this, 5.5D, 8.0F, 2.2D, true);
        }
        if (now >= this.guardUntil) {
            this.entityData.set(RESONANCE_GUARD, false);
            this.abilityTicks = 0;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide && this.entityData.get(RESONANCE_GUARD)
                && this.level().getGameTime() >= this.guardUntil - 32L) {
            amount *= 0.45F;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putLong("NextResonanceGuard", this.nextAbilityTick);
        tag.putLong("ResonanceGuardUntil", this.guardUntil);
        tag.putInt("ResonanceGuardTicks", this.abilityTicks);
        tag.putBoolean("ResonanceGuardActive", this.entityData.get(RESONANCE_GUARD));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.nextAbilityTick = tag.getLong("NextResonanceGuard");
        this.guardUntil = tag.getLong("ResonanceGuardUntil");
        this.abilityTicks = tag.getInt("ResonanceGuardTicks");
        this.entityData.set(RESONANCE_GUARD, tag.getBoolean("ResonanceGuardActive"));
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.spawnAtLocation(ModItems.RESONANT_OPUS.getDefaultInstance(), 1.0f);
    }
}

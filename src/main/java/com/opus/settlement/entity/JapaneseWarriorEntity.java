package com.opus.settlement.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class JapaneseWarriorEntity extends Monster {
    public static final int ACTION_NONE = 0;
    public static final int ACTION_SMOKE_STEP = 1;
    public static final int ACTION_LONG_LUNGE = 2;
    private static final EntityDataAccessor<Integer> ACTION = SynchedEntityData.defineId(JapaneseWarriorEntity.class, EntityDataSerializers.INT);
    private int actionTicks;
    private int techniqueCooldown;

    protected JapaneseWarriorEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        setPersistenceRequired();
    }

    protected abstract ItemStack defaultWeapon();
    protected abstract int specialAction();
    protected abstract int actionWindup();
    protected abstract float techniqueDamage();

    @Override protected void defineSynchedData() { super.defineSynchedData(); entityData.define(ACTION, ACTION_NONE); }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15D, true) {
            @Override public boolean canUse() { return getActionState() == ACTION_NONE && super.canUse(); }
            @Override public boolean canContinueToUse() { return getActionState() == ACTION_NONE && super.canContinueToUse(); }
        });
        goalSelector.addGoal(6, new RandomStrollGoal(this, .8D));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 12F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void populateDefaultEquipmentSlots(net.minecraft.util.RandomSource random, net.minecraft.world.DifficultyInstance difficulty) {
        setItemSlot(EquipmentSlot.MAINHAND, defaultWeapon());
        setDropChance(EquipmentSlot.MAINHAND, .08F);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (getMainHandItem().isEmpty()) setItemSlot(EquipmentSlot.MAINHAND, defaultWeapon());
        if (techniqueCooldown > 0) techniqueCooldown--;
        LivingEntity target = getTarget();
        if (getActionState() == ACTION_NONE && techniqueCooldown == 0 && target != null && target.isAlive()
            && distanceToSqr(target) >= 9D && distanceToSqr(target) <= 100D) beginTechnique();
        tickTechnique(target);
    }

    public void beginTechnique() {
        entityData.set(ACTION, specialAction());
        actionTicks = actionWindup() + 8;
        techniqueCooldown = specialAction() == ACTION_SMOKE_STEP ? 90 : 120;
        getNavigation().stop();
        if (level() instanceof ServerLevel server) server.sendParticles(
            specialAction() == ACTION_SMOKE_STEP ? ParticleTypes.LARGE_SMOKE : ParticleTypes.CRIT,
            getX(), getY() + 1D, getZ(), specialAction() == ACTION_SMOKE_STEP ? 18 : 12, .45D, .8D, .45D, .04D);
    }

    private void tickTechnique(LivingEntity target) {
        if (getActionState() == ACTION_NONE) return;
        actionTicks--;
        if (target != null) getLookControl().setLookAt(target, 40F, 40F);
        if (actionTicks == 8 && target != null) {
            Vec3 direction = target.position().subtract(position());
            if (direction.lengthSqr() > .01D) {
                direction = new Vec3(direction.x, 0, direction.z).normalize();
                setDeltaMovement(direction.scale(getActionState() == ACTION_SMOKE_STEP ? 1.45D : 1.10D).add(0, .12D, 0));
            }
        }
        if (actionTicks == 5 && target != null && distanceToSqr(target) <= (getActionState() == ACTION_SMOKE_STEP ? 12.25D : 20.25D)) {
            applyTechniqueHit(target);
            swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        }
        if (actionTicks <= 0) entityData.set(ACTION, ACTION_NONE);
    }

    public boolean applyTechniqueHit(LivingEntity target) {
        boolean hit = target.hurt(level().damageSources().mobAttack(this), techniqueDamage());
        if (hit) {
            Vec3 away = target.position().subtract(position()).normalize();
            target.push(away.x * (specialAction() == ACTION_LONG_LUNGE ? 1.2D : .65D), .25D, away.z * (specialAction() == ACTION_LONG_LUNGE ? 1.2D : .65D));
            playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1F, specialAction() == ACTION_LONG_LUNGE ? .75F : 1.25F);
        }
        return hit;
    }

    public int getActionState() { return entityData.get(ACTION); }
    public void setActionForQa(int action) { entityData.set(ACTION, action); }

    @Override public boolean removeWhenFarAway(double distance) { return false; }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag); tag.putInt("WarriorAction", getActionState()); tag.putInt("TechniqueCooldown", techniqueCooldown); tag.putInt("ActionTicks", actionTicks);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag); entityData.set(ACTION, tag.getInt("WarriorAction")); techniqueCooldown=tag.getInt("TechniqueCooldown"); actionTicks=tag.getInt("ActionTicks");
    }
}

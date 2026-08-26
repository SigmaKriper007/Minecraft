package com.opus.darkforest.entity;

import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.registry.DarkForestEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class MoonwingBatEntity extends FlyingMob implements Enemy, GeoEntity {
    public enum Action {
        HUNT(0, 0, "fly"), TELEGRAPH(1, 12, "hover"), DIVE(2, 20, "dive"), RETREAT(3, 24, "fly"), SONAR(4, 32, "sonar");
        final int id, duration; final String animation;
        Action(int id, int duration, String animation) { this.id = id; this.duration = duration; this.animation = animation; }
        static Action byId(int id) { for (Action action : values()) if (action.id == id) return action; return HUNT; }
    }

    private static final EntityDataAccessor<Integer> ACTION = SynchedEntityData.defineId(MoonwingBatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ACTION_TICK = SynchedEntityData.defineId(MoonwingBatEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int attackCooldown = 25;
    private int attackCycle;
    private boolean diveHit;
    private Vec3 patrolTarget;

    public MoonwingBatEntity(EntityType<? extends FlyingMob> type, Level level) {
        super(type, level);
        moveControl = new MoonwingMoveControl(this);
        setNoGravity(true);
        xpReward = 10;
    }

    @Override protected void defineSynchedData() { super.defineSynchedData(); entityData.define(ACTION, 0); entityData.define(ACTION_TICK, 0); }
    @Override protected void registerGoals() {
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 20));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
    @Override protected PathNavigation createNavigation(Level level) { FlyingPathNavigation navigation = new FlyingPathNavigation(this, level); navigation.setCanFloat(true); navigation.setCanOpenDoors(false); return navigation; }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 24).add(Attributes.ATTACK_DAMAGE, 6)
            .add(Attributes.MOVEMENT_SPEED, .28).add(Attributes.FLYING_SPEED, .42).add(Attributes.FOLLOW_RANGE, 28);
    }

    public static boolean canSpawn(EntityType<MoonwingBatEntity> type, ServerLevelAccessor level, MobSpawnType reason,
                                   BlockPos pos, RandomSource random) {
        return level.getBiome(pos).is(DarkForestLine.DARK_FOREST) && ShadeSpiderlingEntity.nativeFloor(level, pos.below())
            && level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
    }

    public Action getAction() { return Action.byId(entityData.get(ACTION)); }
    public int getActionTick() { return entityData.get(ACTION_TICK); }
    private void setAction(Action action) { entityData.set(ACTION, action.id); entityData.set(ACTION_TICK, 0); diveHit = false; navigation.stop(); }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        setNoGravity(true);
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) { setTarget(null); tickPatrol(); return; }
        Action action = getAction();
        if (action == Action.HUNT) tickHunt(target); else tickAction(action, target);
    }

    private void tickHunt(LivingEntity target) {
        if (attackCooldown > 0) attackCooldown--;
        double angle = (tickCount * .055 + getId()) % (Math.PI * 2);
        Vec3 orbit = target.position().add(Math.cos(angle) * 5.5, 3.5 + Math.sin(angle * 2), Math.sin(angle) * 5.5);
        moveControl.setWantedPosition(orbit.x, orbit.y, orbit.z, 1.1);
        if (attackCooldown <= 0 && distanceToSqr(target) < 18 * 18) {
            attackCycle++;
            setAction(attackCycle % 3 == 0 ? Action.SONAR : Action.TELEGRAPH);
        }
    }

    private void tickAction(Action action, LivingEntity target) {
        int tick = getActionTick();
        lookAt(target, 30, 30);
        switch (action) {
            case TELEGRAPH -> {
                setDeltaMovement(getDeltaMovement().scale(.72));
                if (tick == action.duration - 1) {
                    Vec3 aim = target.getEyePosition().add(target.getDeltaMovement().scale(5)).subtract(position());
                    if (aim.lengthSqr() < .01) aim = new Vec3(0, 0, 1);
                    setDeltaMovement(aim.normalize().scale(.86));
                    setAction(Action.DIVE);
                    return;
                }
            }
            case DIVE -> {
                Vec3 motion = getDeltaMovement(); setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
                if (!diveHit && distanceToSqr(target) <= 2.2 * 2.2) {
                    diveHit = true; target.hurt(damageSources().mobAttack(this), 6F);
                    Vec3 push = motion.normalize(); target.push(push.x * .45, .18, push.z * .45);
                }
                if (diveHit || tick >= action.duration - 1) { setAction(Action.RETREAT); return; }
            }
            case RETREAT -> {
                Vec3 away = position().subtract(target.position()); away = new Vec3(away.x, .8, away.z);
                if (away.lengthSqr() < .01) away = new Vec3(0, 1, 1);
                Vec3 retreat = position().add(away.normalize().scale(8));
                moveControl.setWantedPosition(retreat.x, retreat.y + 3, retreat.z, 1.25);
            }
            case SONAR -> {
                setDeltaMovement(getDeltaMovement().scale(.7));
                if (tick == 10) {
                    MoonwingPulseEntity pulse = DarkForestEntities.MOONWING_PULSE.create(level());
                    if (pulse == null) throw new IllegalStateException("Unable to create Moonwing Pulse");
                    pulse.setPos(getX(), getY(), getZ()); pulse.configure(this); level().addFreshEntity(pulse);
                    level().playSound(null, this, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.HOSTILE, .9F, 1.45F);
                }
            }
            default -> { }
        }
        tick++;
        entityData.set(ACTION_TICK, tick);
        if (tick >= action.duration) {
            if (action == Action.SONAR) setAction(Action.RETREAT);
            else { setAction(Action.HUNT); attackCooldown = 25; }
        }
    }

    private void tickPatrol() {
        if (patrolTarget == null || patrolTarget.distanceToSqr(position()) < 4 || tickCount % 80 == 0) {
            patrolTarget = position().add(random.nextInt(17) - 8, random.nextInt(7) - 2, random.nextInt(17) - 8);
            patrolTarget = new Vec3(patrolTarget.x, Mth.clamp(patrolTarget.y, level().getMinBuildHeight() + 3, level().getMaxBuildHeight() - 4), patrolTarget.z);
        }
        moveControl.setWantedPosition(patrolTarget.x, patrolTarget.y, patrolTarget.z, .82);
    }

    @Override public void tick() { super.tick(); setNoGravity(true); }
    @Override public boolean causeFallDamage(float distance, float multiplier, DamageSource source) { return false; }
    @Override protected void checkFallDamage(double y, boolean onGround, net.minecraft.world.level.block.state.BlockState state, BlockPos pos) { }
    @Override protected ResourceLocation getDefaultLootTable() { return DarkForestLine.id("entities/moonwing_bat"); }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putInt("Action", getAction().id); tag.putInt("ActionTick", getActionTick()); tag.putInt("AttackCooldown", attackCooldown); tag.putInt("AttackCycle", attackCycle); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); entityData.set(ACTION, tag.getInt("Action")); entityData.set(ACTION_TICK, tag.getInt("ActionTick")); attackCooldown = tag.getInt("AttackCooldown"); attackCycle = tag.getInt("AttackCycle"); }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "moonwing", 1, state -> {
            Action action = getAction(); String name = action == Action.HUNT && getDeltaMovement().lengthSqr() < .005 ? "hover" : action.animation;
            state.getController().setAnimation(RawAnimation.begin().thenLoop(name)); return PlayState.CONTINUE;
        }));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    private static final class MoonwingMoveControl extends MoveControl {
        private MoonwingMoveControl(Mob mob) { super(mob); }
        @Override public void tick() {
            if (operation != Operation.MOVE_TO) { mob.setDeltaMovement(mob.getDeltaMovement().scale(.92)); return; }
            Vec3 delta = new Vec3(wantedX - mob.getX(), wantedY - mob.getY(), wantedZ - mob.getZ());
            double distance = delta.length();
            if (distance < .4) { operation = Operation.WAIT; mob.setDeltaMovement(mob.getDeltaMovement().scale(.6)); return; }
            double speed = Math.min(.46, speedModifier * .15);
            Vec3 desired = delta.normalize().scale(speed);
            mob.setDeltaMovement(mob.getDeltaMovement().scale(.74).add(desired.scale(.26)));
            float yaw = (float)Math.toDegrees(Math.atan2(delta.x, delta.z)); mob.setYRot(yaw); mob.yBodyRot = yaw;
        }
    }
}

package com.opus.darkforest.entity;

import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.registry.DarkForestEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public final class GloomBroodmotherEntity extends Spider implements GeoEntity {
    public enum Action {
        NONE(0, 0, "idle"), BITE(1, 12, "bite"), WEB(2, 40, "web_cast"), SLAM(3, 42, "slam");
        final int id, duration; final String animation;
        Action(int id, int duration, String animation) { this.id = id; this.duration = duration; this.animation = animation; }
        static Action byId(int id) { for (Action action : values()) if (action.id == id) return action; return NONE; }
    }

    private static final EntityDataAccessor<Integer> ACTION = SynchedEntityData.defineId(GloomBroodmotherEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ACTION_TICK = SynchedEntityData.defineId(GloomBroodmotherEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int specialCooldown = 45;
    private int meleeCooldown;
    private boolean slamImpacted;
    private boolean broodReleased;
    private int releasedChildCount;
    private final List<ShadeSpiderlingEntity> releasedChildren = new ArrayList<>();

    public GloomBroodmotherEntity(EntityType<? extends Spider> type, Level level) { super(type, level); xpReward = 24; }
    @Override protected void defineSynchedData() { super.defineSynchedData(); entityData.define(ACTION, 0); entityData.define(ACTION_TICK, 0); }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, .65));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 16));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 56).add(Attributes.ARMOR, 4)
            .add(Attributes.ATTACK_DAMAGE, 8).add(Attributes.MOVEMENT_SPEED, .27).add(Attributes.FOLLOW_RANGE, 28)
            .add(Attributes.KNOCKBACK_RESISTANCE, .5);
    }

    public static boolean canSpawn(EntityType<GloomBroodmotherEntity> type, ServerLevelAccessor level, MobSpawnType reason,
                                   BlockPos pos, RandomSource random) {
        return level.getBiome(pos).is(DarkForestLine.DARK_FOREST) && ShadeSpiderlingEntity.nativeFloor(level, pos.below())
            && level.noCollision(type.getAABB(pos.getX() + .5, pos.getY(), pos.getZ() + .5));
    }

    public Action getAction() { return Action.byId(entityData.get(ACTION)); }
    public int getActionTick() { return entityData.get(ACTION_TICK); }
    private void setAction(Action action) { entityData.set(ACTION, action.id); entityData.set(ACTION_TICK, 0); slamImpacted = false; navigation.stop(); }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) return;
        if (specialCooldown > 0) specialCooldown--;
        if (meleeCooldown > 0) meleeCooldown--;
        Action action = getAction();
        if (action != Action.NONE) { tickAction(action, target); return; }
        double distance = distanceTo(target);
        if (specialCooldown <= 0 && distance <= 13) {
            setAction(distance > 4.5 ? Action.WEB : Action.SLAM);
            return;
        }
        if (distance <= 2.8 && meleeCooldown <= 0) setAction(Action.BITE);
        else navigation.moveTo(target, .95);
    }

    private void tickAction(Action action, LivingEntity target) {
        int tick = getActionTick();
        lookAt(target, 20, 20);
        switch (action) {
            case BITE -> { if (tick == 5 && distanceTo(target) <= 3.2) doHurtTarget(target); }
            case WEB -> { if (tick == 20) spawnWebLane(target.position()); }
            case SLAM -> {
                if (tick == 16) {
                    Vec3 delta = target.position().subtract(position());
                    Vec3 horizontal = new Vec3(delta.x, 0, delta.z);
                    if (horizontal.lengthSqr() > .01) horizontal = horizontal.normalize();
                    setDeltaMovement(horizontal.scale(.78).add(0, .52, 0));
                }
                if (!slamImpacted && tick >= 22 && (onGround() || tick >= 34)) impactSlam();
            }
            default -> { }
        }
        tick++;
        entityData.set(ACTION_TICK, tick);
        if (tick >= action.duration) {
            entityData.set(ACTION, 0); entityData.set(ACTION_TICK, 0);
            if (action == Action.BITE) meleeCooldown = 18; else specialCooldown = 90;
        }
    }

    private void spawnWebLane(Vec3 targetPos) {
        Vec3 start = position(); Vec3 delta = targetPos.subtract(start); Vec3 flat = new Vec3(delta.x, 0, delta.z);
        if (flat.lengthSqr() < .01) flat = new Vec3(0, 0, 1); else flat = flat.normalize();
        for (int i = 1; i <= 7; i++) {
            Vec3 sample = start.add(flat.scale(i * 1.45));
            int y = level().getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mth.floor(sample.x), Mth.floor(sample.z));
            GloomWebEntity web = DarkForestEntities.GLOOM_WEB.create(level());
            if (web == null) throw new IllegalStateException("Unable to create Gloom Web");
            web.setPos(sample.x, y + .08, sample.z); web.configure(this, 48); level().addFreshEntity(web);
        }
        level().playSound(null, this, SoundEvents.SPIDER_AMBIENT, SoundSource.HOSTILE, 1.3F, .62F);
    }

    private void impactSlam() {
        slamImpacted = true;
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(3.6, 1.2, 3.6),
            entity -> entity.isAlive() && entity != this && !(entity instanceof ShadeSpiderlingEntity)
                && !(entity instanceof GloomBroodmotherEntity) && !(entity instanceof MoonwingBatEntity))) {
            target.hurt(damageSources().mobAttack(this), 9F);
            Vec3 away = target.position().subtract(position()); away = new Vec3(away.x, 0, away.z);
            if (away.lengthSqr() > .01) away = away.normalize();
            target.push(away.x * .85, .38, away.z * .85);
        }
        level().playSound(null, this, SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.1F, .55F);
    }

    @Override
    public void die(DamageSource source) {
        if (!broodReleased && level() instanceof ServerLevel server) {
            broodReleased = true;
            for (int i = 0; i < 6; i++) {
                ShadeSpiderlingEntity child = DarkForestEntities.SHADE_SPIDERLING.create(server);
                if (child == null) throw new IllegalStateException("Unable to create Broodmother child");
                double angle = Math.PI * 2 * i / 6D;
                child.setPos(getX() + Math.cos(angle) * 1.15, getY() + .15, getZ() + Math.sin(angle) * 1.15);
                child.setDeltaMovement(Math.cos(angle) * .18, .22, Math.sin(angle) * .18);
                child.setTarget(getLastHurtByMob());
                if (!server.addFreshEntity(child)) throw new IllegalStateException("Unable to add one of six Broodmother children");
                releasedChildCount++;
                releasedChildren.add(child);
            }
        }
        super.die(source);
    }

    int releasedChildCount() { return releasedChildCount; }
    List<ShadeSpiderlingEntity> releasedChildren() { return List.copyOf(releasedChildren); }

    @Override protected ResourceLocation getDefaultLootTable() { return DarkForestLine.id("entities/gloom_broodmother"); }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putBoolean("BroodReleased", broodReleased); tag.putInt("SpecialCooldown", specialCooldown); tag.putInt("MeleeCooldown", meleeCooldown); tag.putInt("Action", getAction().id); tag.putInt("ActionTick", getActionTick()); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); broodReleased = tag.getBoolean("BroodReleased"); specialCooldown = tag.getInt("SpecialCooldown"); meleeCooldown = tag.getInt("MeleeCooldown"); entityData.set(ACTION, tag.getInt("Action")); entityData.set(ACTION_TICK, tag.getInt("ActionTick")); }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "broodmother", 2, state -> {
            Action action = getAction();
            RawAnimation animation = action == Action.NONE ? RawAnimation.begin().thenLoop(state.isMoving() ? "walk" : "idle")
                : RawAnimation.begin().thenPlay(action.animation);
            state.getController().setAnimation(animation); return PlayState.CONTINUE;
        }));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}

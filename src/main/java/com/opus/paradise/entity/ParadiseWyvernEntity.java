package com.opus.paradise.entity;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.registry.ParadiseBlocks;
import com.opus.paradise.registry.ParadiseEntities;
import com.opus.paradise.registry.ParadiseItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class ParadiseWyvernEntity extends TamableAnimal implements GeoEntity {
    public static final int WIND_COOLDOWN_TICKS = 300;
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(ParadiseWyvernEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> WIND_COOLDOWN = SynchedEntityData.defineId(ParadiseWyvernEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CAST_TICKS = SynchedEntityData.defineId(ParadiseWyvernEntity.class, EntityDataSerializers.INT);
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle_flight");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
    private static final RawAnimation SIT = RawAnimation.begin().thenLoop("sit");
    private static final RawAnimation CAST = RawAnimation.begin().thenPlay("wind_cast");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private float pilotStrafe;
    private float pilotForward;
    private float pilotYaw;
    private float pilotPitch;
    private boolean pilotAscend;
    private boolean pilotDescend;
    private int lastPilotInputTick = Integer.MIN_VALUE;

    public ParadiseWyvernEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        moveControl = new WyvernMoveControl(this);
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SADDLED, false);
        entityData.define(WIND_COOLDOWN, 0);
        entityData.define(CAST_TICKS, 0);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanFloat(true);
        navigation.setCanOpenDoors(false);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(1, new FollowOwnerGoal(this, 1.05, 7.0F, 2.5F, true));
        goalSelector.addGoal(2, new BreedGoal(this, 0.9));
        goalSelector.addGoal(3, new TemptGoal(this, 1.0, Ingredient.of(ParadiseItems.PARADISE_FRUIT), false));
        goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 0.78));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 12.0F));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 42.0).add(Attributes.ARMOR, 6.0)
            .add(Attributes.MOVEMENT_SPEED, 0.28).add(Attributes.FLYING_SPEED, 0.48)
            .add(Attributes.FOLLOW_RANGE, 32.0).add(Attributes.KNOCKBACK_RESISTANCE, 0.25);
    }

    public static boolean canSpawn(EntityType<ParadiseWyvernEntity> type, ServerLevelAccessor level, MobSpawnType reason,
                                   BlockPos pos, RandomSource random) {
        return pos.getY() >= ParadiseLine.SKY_ISLAND_MIN_Y
            && level.getBlockState(pos.below()).is(ParadiseBlocks.PARADISE_GRASS)
            && level.getRawBrightness(pos, 0) > 8 && random.nextInt(3) == 0;
    }

    public boolean isSaddled() { return entityData.get(SADDLED); }
    public int getWindCooldown() { return entityData.get(WIND_COOLDOWN); }
    public boolean isCastingWind() { return entityData.get(CAST_TICKS) > 0; }

    public void acceptPilotInput(ServerPlayer player, float strafe, float forward, float yaw, float pitch,
                                 boolean ascend, boolean descend) {
        acceptPilotInput((Player) player, strafe, forward, yaw, pitch, ascend, descend);
    }

    public void acceptPilotInput(Player player, float strafe, float forward, float yaw, float pitch,
                                 boolean ascend, boolean descend) {
        if (player != getControllingPassenger() || !isOwnedBy(player) || !isSaddled()) return;
        pilotStrafe = Mth.clamp(strafe, -1.0F, 1.0F);
        pilotForward = Mth.clamp(forward, -1.0F, 1.0F);
        pilotYaw = yaw;
        pilotPitch = Mth.clamp(pitch, -80.0F, 80.0F);
        pilotAscend = ascend;
        pilotDescend = descend;
        lastPilotInputTick = tickCount;
    }

    public boolean tryFireWindCore(ServerPlayer player) {
        if (level().isClientSide || player != getControllingPassenger() || !isOwnedBy(player)
            || !isSaddled() || getWindCooldown() > 0 || !isAlive()) return false;
        Vec3 look = player.getViewVector(1.0F).normalize();
        Vec3 origin = position().add(0.0, 1.15, 0.0).add(look.scale(1.65));
        WindCoreEntity core = new WindCoreEntity(level(), player, this, look);
        core.setPos(origin);
        level().addFreshEntity(core);
        entityData.set(WIND_COOLDOWN, WIND_COOLDOWN_TICKS);
        entityData.set(CAST_TICKS, 16);
        return true;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (!isTame() && !isBaby() && held.is(ParadiseItems.PARADISE_FRUIT)) {
            if (!level().isClientSide) {
                if (!player.getAbilities().instabuild) held.shrink(1);
                if (random.nextInt(3) == 0) {
                    tame(player);
                    setOrderedToSit(false);
                    setPersistenceRequired();
                    level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    level().broadcastEntityEvent(this, (byte) 6);
                }
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (isTame() && isOwnedBy(player)) {
            if (held.is(ParadiseItems.PARADISE_FRUIT) && getHealth() < getMaxHealth()) {
                if (!level().isClientSide) {
                    if (!player.getAbilities().instabuild) held.shrink(1);
                    heal(8.0F);
                }
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
            if (!isSaddled() && !isBaby() && held.is(Items.SADDLE)) {
                if (!level().isClientSide) {
                    entityData.set(SADDLED, true);
                    if (!player.getAbilities().instabuild) held.shrink(1);
                }
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
            if (player.isSecondaryUseActive()) {
                if (!level().isClientSide) {
                    setOrderedToSit(!isOrderedToSit());
                    navigation.stop();
                }
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
            if (isSaddled() && held.isEmpty() && !isVehicle() && !isBaby()) {
                if (!level().isClientSide) {
                    setOrderedToSit(false);
                    player.startRiding(this);
                }
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
        }
        return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    public Player getControllingPassenger() {
        Entity passenger = getFirstPassenger();
        return passenger instanceof Player player && isOwnedBy(player) ? player : null;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return passenger instanceof Player player && isSaddled() && isOwnedBy(player) && getPassengers().isEmpty();
    }

    @Override public double getPassengersRidingOffset() { return 1.18D; }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (isSaddled()) spawnAtLocation(Items.SADDLE);
    }

    @Override
    public void travel(Vec3 travelVector) {
        Player rider = getControllingPassenger();
        if (rider == null || !isSaddled()) {
            super.travel(travelVector);
            return;
        }
        if (!level().isClientSide && tickCount - lastPilotInputTick > 10) {
            pilotStrafe = 0.0F;
            pilotForward = 0.0F;
            pilotAscend = false;
            pilotDescend = false;
        }
        setYRot(pilotYaw);
        yRotO = getYRot();
        setXRot(pilotPitch * 0.35F);
        yBodyRot = getYRot();
        yHeadRot = getYRot();

        double radians = Math.toRadians(pilotYaw);
        Vec3 forward = new Vec3(-Math.sin(radians), 0.0, Math.cos(radians));
        Vec3 right = new Vec3(Math.cos(radians), 0.0, Math.sin(radians));
        Vec3 horizontal = forward.scale(pilotForward).add(right.scale(pilotStrafe * 0.72));
        if (horizontal.lengthSqr() > 1.0) horizontal = horizontal.normalize();
        double cruise = pilotForward < 0.0F ? 0.22 : 0.44;
        horizontal = horizontal.scale(cruise);

        double vertical = Math.abs(pilotForward) + Math.abs(pilotStrafe) < 0.05F ? -0.035 : -pilotPitch / 80.0 * 0.24;
        if (pilotAscend != pilotDescend) vertical = pilotAscend ? 0.34 : -0.30;
        vertical = Mth.clamp(vertical, -0.34, 0.38);
        Vec3 desired = new Vec3(horizontal.x, vertical, horizontal.z);
        Vec3 motion = getDeltaMovement().scale(0.58).add(desired.scale(0.42));
        setDeltaMovement(motion);
        move(MoverType.SELF, motion);
        hasImpulse = true;
        calculateEntityAnimation(false);
        tryCheckInsideBlocks();
    }

    @Override
    public void tick() {
        super.tick();
        setNoGravity(true);
        if (!level().isClientSide) {
            if (getWindCooldown() > 0) entityData.set(WIND_COOLDOWN, getWindCooldown() - 1);
            if (entityData.get(CAST_TICKS) > 0) entityData.set(CAST_TICKS, entityData.get(CAST_TICKS) - 1);
        }
    }

    @Override public boolean isFood(ItemStack stack) { return stack.is(ParadiseItems.PARADISE_FRUIT); }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ParadiseEntities.PARADISE_WYVERN.create(level);
    }

    @Override public boolean causeFallDamage(float distance, float multiplier, net.minecraft.world.damagesource.DamageSource source) { return false; }
    @Override protected void checkFallDamage(double y, boolean onGround, net.minecraft.world.level.block.state.BlockState state, BlockPos pos) { }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Saddle", isSaddled());
        tag.putInt("WindCooldown", getWindCooldown());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(SADDLED, tag.getBoolean("Saddle"));
        entityData.set(WIND_COOLDOWN, Math.max(0, tag.getInt("WindCooldown")));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "body", 2, this::animation));
    }

    private PlayState animation(AnimationState<ParadiseWyvernEntity> state) {
        RawAnimation animation = isCastingWind() ? CAST : isOrderedToSit() ? SIT : state.isMoving() ? FLY : IDLE;
        state.getController().setAnimation(animation);
        return PlayState.CONTINUE;
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    private static final class WyvernMoveControl extends MoveControl {
        private WyvernMoveControl(Mob mob) { super(mob); }

        @Override
        public void tick() {
            if (mob.isVehicle()) return;
            if (operation != Operation.MOVE_TO) {
                mob.setDeltaMovement(mob.getDeltaMovement().scale(0.91));
                return;
            }
            Vec3 delta = new Vec3(wantedX - mob.getX(), wantedY - mob.getY(), wantedZ - mob.getZ());
            if (delta.lengthSqr() < 0.25) {
                operation = Operation.WAIT;
                return;
            }
            Vec3 desired = delta.normalize().scale(Math.min(0.30, speedModifier * 0.11));
            mob.setDeltaMovement(mob.getDeltaMovement().scale(0.76).add(desired.scale(0.24)));
            float yaw = (float) Math.toDegrees(Math.atan2(delta.x, delta.z));
            mob.setYRot(yaw);
            mob.yBodyRot = yaw;
        }
    }
}

package com.opus.paradise.entity;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.registry.ParadiseBlocks;
import com.opus.paradise.registry.ParadiseEntities;
import com.opus.paradise.registry.ParadiseItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

public final class SunfinchEntity extends Animal implements GeoEntity {
    private static final RawAnimation PERCH = RawAnimation.begin().thenLoop("perch");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SunfinchEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.moveControl = new FinchMoveControl(this);
        setNoGravity(true);
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
        goalSelector.addGoal(0, new PanicGoal(this, 1.25));
        goalSelector.addGoal(1, new BreedGoal(this, 1.0));
        goalSelector.addGoal(2, new TemptGoal(this, 1.05, Ingredient.of(ParadiseItems.PARADISE_FRUIT), false));
        goalSelector.addGoal(3, new WaterAvoidingRandomFlyingGoal(this, 0.85));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0).add(Attributes.MOVEMENT_SPEED, 0.24)
            .add(Attributes.FLYING_SPEED, 0.62).add(Attributes.FOLLOW_RANGE, 16.0);
    }

    public static boolean canSpawn(EntityType<SunfinchEntity> type, ServerLevelAccessor level, MobSpawnType reason,
                                   BlockPos pos, RandomSource random) {
        return pos.getY() >= ParadiseLine.SKY_ISLAND_MIN_Y
            && level.getBlockState(pos.below()).is(ParadiseBlocks.PARADISE_GRASS)
            && level.getRawBrightness(pos, 0) > 8;
    }

    @Override public boolean isFood(ItemStack stack) { return stack.is(ParadiseItems.PARADISE_FRUIT); }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ParadiseEntities.SUNFINCH.create(level);
    }

    @Override public boolean causeFallDamage(float distance, float multiplier, net.minecraft.world.damagesource.DamageSource source) { return false; }
    @Override protected void checkFallDamage(double y, boolean onGround, net.minecraft.world.level.block.state.BlockState state, BlockPos pos) { }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return new ResourceLocation("opusvsexe", "entities/sunfinch");
    }

    @Override
    public void tick() {
        super.tick();
        setNoGravity(true);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "body", 2, this::animation));
    }

    private PlayState animation(AnimationState<SunfinchEntity> state) {
        state.getController().setAnimation(state.isMoving() ? FLY : PERCH);
        return PlayState.CONTINUE;
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    private static final class FinchMoveControl extends MoveControl {
        private FinchMoveControl(Mob mob) { super(mob); }

        @Override
        public void tick() {
            if (operation != Operation.MOVE_TO) {
                mob.setDeltaMovement(mob.getDeltaMovement().scale(0.91));
                return;
            }
            Vec3 delta = new Vec3(wantedX - mob.getX(), wantedY - mob.getY(), wantedZ - mob.getZ());
            double distance = delta.length();
            if (distance < 0.35) {
                operation = Operation.WAIT;
                mob.setDeltaMovement(mob.getDeltaMovement().scale(0.5));
                return;
            }
            double speed = Math.min(0.34, speedModifier * 0.12);
            Vec3 desired = delta.normalize().scale(speed);
            mob.setDeltaMovement(mob.getDeltaMovement().scale(0.72).add(desired.scale(0.28)));
            float yaw = (float) Math.toDegrees(Math.atan2(delta.x, delta.z));
            mob.setYRot(yaw); mob.yBodyRot = yaw;
        }
    }
}

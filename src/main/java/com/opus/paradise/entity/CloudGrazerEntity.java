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
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class CloudGrazerEntity extends Animal implements GeoEntity {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CloudGrazerEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        goalSelector.addGoal(3, new TemptGoal(this, 1.05, Ingredient.of(ParadiseItems.PARADISE_FRUIT), false));
        goalSelector.addGoal(4, new FollowParentGoal(this, 1.0));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.78));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes().add(Attributes.MAX_HEALTH, 18.0).add(Attributes.MOVEMENT_SPEED, 0.22)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.18).add(Attributes.FOLLOW_RANGE, 18.0);
    }

    public static boolean canSpawn(EntityType<CloudGrazerEntity> type, ServerLevelAccessor level, MobSpawnType reason,
                                   BlockPos pos, RandomSource random) {
        return pos.getY() >= ParadiseLine.SKY_ISLAND_MIN_Y
            && level.getBlockState(pos.below()).is(ParadiseBlocks.PARADISE_GRASS)
            && level.getRawBrightness(pos, 0) > 8;
    }

    @Override public boolean isFood(ItemStack stack) { return stack.is(ParadiseItems.PARADISE_FRUIT); }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ParadiseEntities.CLOUD_GRAZER.create(level);
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return new ResourceLocation("opusvsexe", "entities/cloud_grazer");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "body", 3, this::animation));
    }

    private PlayState animation(AnimationState<CloudGrazerEntity> state) {
        state.getController().setAnimation(state.isMoving() ? WALK : IDLE);
        return PlayState.CONTINUE;
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}

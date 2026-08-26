package com.opus.entity.haiku;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

/**
 * Haiku-3 "Enforcer" - тяжёлый двуногий «исполнитель»
 * ~2.8 блока: пушка справа, клешня-щит слева, обратные колени, серво-горб.
 */
public class Haiku3Entity extends HaikuMob {

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    private static final RawAnimation HURT_ANIM = RawAnimation.begin().thenPlay("hurt");
    private static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlay("death");

    public Haiku3Entity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "enforcer_controller", 4, this::enforcerAnimationPredicate));
    }

    private PlayState enforcerAnimationPredicate(AnimationState<Haiku3Entity> state) {
        Haiku3Entity self = state.getAnimatable();
        if (self.isDeadOrDying()) {
            playOnce(state, DEATH_ANIM, false);
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
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.9, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 40.0)
            .add(Attributes.MOVEMENT_SPEED, 0.27)
            .add(Attributes.ATTACK_DAMAGE, 8.0)
            .add(Attributes.ARMOR, 6.0)
            .add(Attributes.FOLLOW_RANGE, 30.0);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 2.55f;
    }
}

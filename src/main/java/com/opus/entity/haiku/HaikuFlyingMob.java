package com.opus.entity.haiku;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

/**
 * Летающая база машин Haiku (воздушная ветвь): дроны.
 * Наследует GeckoLib-каркас HaikuMob, но использует воздушную навигацию
 * (FlyingPathNavigation) и собственный 3D-MoveControl (по образцу Vex/Allay),
 * чтобы передвигаться в воздухе и таранить игрока.
 *
 * Анимации: idle/drift из haiku.animation.json; attack — лёгкий рывок корпуса.
 */
public abstract class HaikuFlyingMob extends HaikuMob {

    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");

    protected HaikuFlyingMob(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new HaikuFlyMoveControl(this, 0.9F);
        this.setNoGravity(true);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    protected PlayState animationPredicate(AnimationState<HaikuMob> state) {
        HaikuMob self = state.getAnimatable();
        if (self.isDeadOrDying()) {
            // у дронов нет death-анимации — просто висим в idle
            state.getController().setAnimation(IDLE_ANIM);
            return PlayState.CONTINUE;
        }
        if (self.swinging) {
            playOnce(state, ATTACK_ANIM, true);
            return PlayState.CONTINUE;
        }
        state.getController().setAnimation(state.isMoving() ? DRIFT_ANIM : IDLE_ANIM);
        return PlayState.CONTINUE;
    }

    /**
     * 3D-контроль движения для летающих мобов: движемся к wanted point
     * по прямой в воздухе (без наземной физики), как Vex/Allay.
     */
    public static class HaikuFlyMoveControl extends MoveControl {
        private final float speedModifier;

        public HaikuFlyMoveControl(Mob mob, float speedModifier) {
            super(mob);
            this.speedModifier = speedModifier;
        }

        @Override
        public void tick() {
            if (this.operation != MoveControl.Operation.MOVE_TO) {
                return;
            }
            Vec3 delta = new Vec3(this.wantedX - this.mob.getX(),
                    this.wantedY - this.mob.getY(),
                    this.wantedZ - this.mob.getZ());
            double dist = delta.length();
            if (dist < this.mob.getBoundingBox().getSize() * 0.5) {
                // долетели — гасим скорость
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0.1));
                this.operation = MoveControl.Operation.WAIT;
                return;
            }
            // летим прямо к цели со скоростью
            Vec3 vel = delta.normalize().scale(this.speedModifier);
            this.mob.setDeltaMovement(vel.x(), vel.y(), vel.z());
            // смотрим в направлении полёта
            double yaw = Math.toDegrees(Math.atan2(delta.x(), delta.z()));
            this.mob.setYRot((float) yaw);
            this.mob.yBodyRot = (float) yaw;
            this.mob.yHeadRot = (float) yaw;
        }
    }
}

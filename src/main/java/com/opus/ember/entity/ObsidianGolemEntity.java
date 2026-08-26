package com.opus.ember.entity;

import com.opus.ember.registry.EmberParticles;
import com.opus.ember.sound.EmberSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class ObsidianGolemEntity extends Monster implements GeoAnimatable {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    private static final RawAnimation HURT = RawAnimation.begin().thenPlay("hurt");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlay("death");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ObsidianGolemEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        xpReward = 18;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.95, false));
        goalSelector.addGoal(2, new RandomStrollGoal(this, 0.55));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 14.0f));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 72.0)
            .add(Attributes.MOVEMENT_SPEED, 0.22)
            .add(Attributes.ATTACK_DAMAGE, 10.0)
            .add(Attributes.ARMOR, 8.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.85)
            .add(Attributes.FOLLOW_RANGE, 36.0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && tickCount % 20 == 0) {
            if (isInLava()) heal(1.0f);
            if (isInWaterOrRain()) hurt(damageSources().drown(), 2.0f);
        }
        if (level() instanceof ServerLevel server && tickCount % 8 == 0) {
            server.sendParticles(EmberParticles.EMBER_SPARK, getX(), getY() + 1.4, getZ(), 2, 0.45, 0.8, 0.45, 0.01);
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            living.setSecondsOnFire(5);
            level().playSound(null, this, EmberSounds.OBSIDIAN_GOLEM_ATTACK, SoundSource.HOSTILE, 1.3f, 0.82f + random.nextFloat() * 0.12f);
        }
        return hit;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hit = super.hurt(source, amount);
        if (hit && !level().isClientSide) {
            level().playSound(null, this, EmberSounds.OBSIDIAN_GOLEM_HURT, SoundSource.HOSTILE, 1.0f, 0.78f + random.nextFloat() * 0.16f);
        }
        return hit;
    }

    @Override public boolean fireImmune() { return true; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "body", 3, this::selectAnimation));
    }

    private PlayState selectAnimation(AnimationState<ObsidianGolemEntity> state) {
        var controller = state.getController();
        if (isDeadOrDying()) controller.setAnimation(DEATH);
        else if (hurtTime > 0) controller.setAnimation(HURT);
        else if (swinging) controller.setAnimation(ATTACK);
        else controller.setAnimation(state.isMoving() ? WALK : IDLE);
        return PlayState.CONTINUE;
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
    @Override public double getTick(Object ignored) { return tickCount; }
}

package com.opusvsexe.entity.custom;

import com.opusvsexe.inventory.ExoInventoryMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class ExosuitEntity extends Mob implements GeoAnimatable {
    protected static final EntityDataAccessor<Boolean> DATA_ATTACKING =
        SynchedEntityData.defineId(ExosuitEntity.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");

    protected int energy;
    protected int maxEnergy;
    protected String exoTier;
    protected int attackTimer;

    private final ExoContainer inventory = new ExoContainer(2);
    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);

    protected ExosuitEntity(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
        this.maxEnergy = 1000;
        this.energy = this.maxEnergy;
        this.exoTier = "unknown";
        if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.getAttackDamage());
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.3)
            .add(Attributes.ATTACK_DAMAGE, 8.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    public float getAttackDamage() {
        return switch (this.exoTier) {
            case "EXO-1" -> 10.0f;
            case "EXO-2" -> 12.0f;
            case "EXO-3" -> 15.0f;
            case "EXO-4" -> 18.0f;
            case "EXO-5" -> 20.0f;
            default -> 8.0f;
        };
    }

    public int getEnergy() {
        return this.energy;
    }

    public int getMaxEnergy() {
        return this.maxEnergy;
    }

    public void addEnergy(int amount) {
        this.energy = Math.min(this.energy + amount, this.maxEnergy);
    }

    public void consumeEnergy(int amount) {
        this.energy = Math.max(this.energy - amount, 0);
    }

    public boolean hasEnoughEnergy(int amount) {
        return this.energy >= amount;
    }

    public abstract void performAbility();

    public void performAbility(int action) {
        if (action == 0) { performAbility(); return; }
        if (action == 1 && hasEnoughEnergy(80)) {
            consumeEnergy(80);
            com.opus.item.CombatEffects.shockwave(this, 3.5, getAttackDamage() * .65f, 1.8, true);
        } else if (action == 2 && hasEnoughEnergy(120)) {
            consumeEnergy(120);
            Vec3 direction = getViewVector(1.0f);
            setDeltaMovement(direction.x * 1.8, Math.max(0.35, direction.y + .35), direction.z * 1.8);
            level().playSound(null, blockPosition(), com.opus.sound.ModSounds.EXO_THRUST, net.minecraft.sounds.SoundSource.PLAYERS, 1.5f, 1.2f);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ATTACKING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.Goal() {
            @Override
            public boolean canUse() {
                return ExosuitEntity.this.getTarget() != null && ExosuitEntity.this.getTarget().isAlive();
            }

            @Override
            public boolean canContinueToUse() {
                return this.canUse();
            }

            @Override
            public void tick() {
                LivingEntity target = ExosuitEntity.this.getTarget();
                if (target == null || !target.isAlive()) {
                    return;
                }
                double reach = ExosuitEntity.this.getBbWidth() * 2.0 * (ExosuitEntity.this.getBbWidth() * 2.0 + target.getBbWidth());
                if (ExosuitEntity.this.distanceToSqr(target) <= reach) {
                    ExosuitEntity.this.attackTimer = 30;
                    ExosuitEntity.this.entityData.set(DATA_ATTACKING, true);
                    ExosuitEntity.this.performAttack();
                }
            }
        });
    }

    @Override
    public void tick() {
        super.tick();
        if (this.attackTimer > 0) {
            this.attackTimer--;
            if (this.attackTimer == 0) {
                this.entityData.set(DATA_ATTACKING, false);
            }
        } else if (this.level().isClientSide && this.entityData.get(DATA_ATTACKING)) {
            this.attackTimer = 30;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "exo_controller", 0, this::animationPredicate));
    }

    protected PlayState animationPredicate(AnimationState<ExosuitEntity> state) {
        if (this.attackTimer > 0) {
            state.getController().setAnimation(ATTACK_ANIM);
        } else if (state.isMoving()) {
            state.getController().setAnimation(WALK_ANIM);
        } else {
            state.getController().setAnimation(IDLE_ANIM);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animatableCache;
    }

    @Override
    public double getTick(Object object) {
        return this.tickCount;
    }

    protected boolean isValidCombatTarget(Entity entity) { return entity instanceof LivingEntity living && living != this && living != getControllingPassenger() && !isAlliedTo(entity); }

    public abstract void performAttack();

    public void attackWithRider(LivingEntity target) {
        this.setTarget(target);
        this.attackTimer = 30;
        this.entityData.set(DATA_ATTACKING, true);
        this.performAttack();
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();
        if (passenger instanceof LivingEntity living) {
            return living;
        }
        return null;
    }

    public boolean isRideableBy(Player player) {
        return this.getFirstPassenger() == null || this.getFirstPassenger() == player;
    }

    public void rideJump() {
        if (this.onGround()) {
            this.jumpFromGround();
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isAlive() && this.isVehicle()) {
            LivingEntity rider = this.getControllingPassenger();
            if (rider != null) {
                this.setYRot(rider.getYRot());
                this.yHeadRot = rider.getYRot();
                this.yBodyRot = this.getYRot();
                this.setRot(this.getYRot(), this.getXRot());
                if (rider instanceof Player player && this.isControlledByLocalInstance()) {
                    float forward = player.zza;
                    float strafe = player.xxa;
                    if (forward <= 0.0F) {
                        forward *= 0.25F;
                    }
                    this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    super.travel(new Vec3(strafe, travelVector.y, forward));
                } else {
                    this.lerpSteps = 0;
                }
                return;
            }
        }
        super.travel(travelVector);
    }

    public ExoContainer getInventory() {
        return this.inventory;
    }

    @Override
    public ItemStack getItemInHand(InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            return this.inventory.getItem(1);
        }
        return this.inventory.getItem(0);
    }

    public SimpleMenuProvider getMenuProvider() {
        return new SimpleMenuProvider(
            (id, inv, p) -> new ExoInventoryMenu(id, inv, this),
            Component.translatable("container.opusvsexe.exo_inventory"));
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 pos, InteractionHand hand) {
        if (!this.level().isClientSide) {
            player.startRiding(this);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }
}
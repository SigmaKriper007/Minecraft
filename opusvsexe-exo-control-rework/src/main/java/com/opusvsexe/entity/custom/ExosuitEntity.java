package com.opusvsexe.entity.custom;

import com.opus.network.ModNetwork;
import com.opus.sound.ModSounds;
import com.opusvsexe.inventory.ExoInventoryMenu;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

/**
 * Base class for every pilotable EXO frame.
 *
 * Control model (rewritten from scratch):
 *
 *  - The pilot is the only source of truth for steering. Mob AI is switched off
 *    entirely while somebody is inside, so the look/move controls can no longer
 *    fight the rider's rotation.
 *  - Movement stays client authoritative (same contract vanilla uses for boats
 *    and horses), which is why every impulse-style effect goes through
 *    {@link #applyImpulse(Vec3)}: the server decides, the piloting client
 *    applies. Setting velocity server-side on a client controlled vehicle is a
 *    no-op, which is why dash/thrusters did nothing in the old build.
 *  - Energy, cooldowns and state flags are synced entity data, so the HUD and
 *    other players see the real values, and they survive save/load.
 *  - Everything a pilot can trigger (attack, jump, ability, inventory) is
 *    validated server-side: energy, cooldown, reach and "is this player really
 *    the pilot".
 */
public abstract class ExosuitEntity extends Mob implements GeoAnimatable {

    public static final int ABILITY_SLOTS = 3;

    private static final EntityDataAccessor<Integer> DATA_ENERGY =
            SynchedEntityData.defineId(ExosuitEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> DATA_FLAGS =
            SynchedEntityData.defineId(ExosuitEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> DATA_COOLDOWNS =
            SynchedEntityData.defineId(ExosuitEntity.class, EntityDataSerializers.INT);

    private static final int FLAG_ATTACKING = 1;
    private static final int FLAG_SPRINTING = 2;
    private static final int FLAG_SHIELD = 4;
    private static final int FLAG_OVERLOAD = 8;

    private static final int COOLDOWN_BITS = 10;
    private static final int COOLDOWN_MASK = (1 << COOLDOWN_BITS) - 1;

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");

    protected final ExoTier tier;

    private final ExoContainer inventory = new ExoContainer();
    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);
    private final int[] cooldowns = new int[ABILITY_SLOTS];

    private int attackTicks;
    private int attackCooldown;
    private int regenTimer;
    private int drainTimer;
    private int shieldTicks;
    private int overloadTicks;
    private int airThrusts;
    private boolean sprintInput;

    protected ExosuitEntity(EntityType<? extends Mob> entityType, Level level, ExoTier tier) {
        super(entityType, level);
        this.tier = tier;
        this.setMaxUpStep(tier.stepHeight());
        this.setPersistenceRequired();
        this.setEnergy(tier.maxEnergy());
        this.setHealth(this.getMaxHealth());
        this.noCulling = true;
    }

    public static AttributeSupplier.Builder createAttributes(ExoTier tier) {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, tier.maxHealth())
                .add(Attributes.MOVEMENT_SPEED, tier.moveSpeed())
                .add(Attributes.ATTACK_DAMAGE, tier.attackDamage())
                .add(Attributes.ARMOR, tier.armor())
                .add(Attributes.KNOCKBACK_RESISTANCE, tier.knockbackResistance())
                .add(Attributes.FOLLOW_RANGE, 0.0D);
    }

    // ------------------------------------------------------------------
    // synced state
    // ------------------------------------------------------------------

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ENERGY, 0);
        this.entityData.define(DATA_FLAGS, (byte) 0);
        this.entityData.define(DATA_COOLDOWNS, 0);
    }

    public ExoTier getTier() {
        return this.tier;
    }

    public int getEnergy() {
        return this.entityData.get(DATA_ENERGY);
    }

    public int getMaxEnergy() {
        return this.tier == null ? 0 : this.tier.maxEnergy();
    }

    public void setEnergy(int amount) {
        int max = this.tier == null ? amount : this.tier.maxEnergy();
        this.entityData.set(DATA_ENERGY, Mth.clamp(amount, 0, max));
    }

    public void addEnergy(int amount) {
        this.setEnergy(this.getEnergy() + amount);
    }

    public void consumeEnergy(int amount) {
        this.setEnergy(this.getEnergy() - amount);
    }

    public boolean hasEnergy(int amount) {
        return this.getEnergy() >= amount;
    }

    /** Kept for source compatibility with the old API. */
    public boolean hasEnoughEnergy(int amount) {
        return this.hasEnergy(amount);
    }

    public float getAttackDamage() {
        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        return this.isOverloadActive() ? damage * 1.5F : damage;
    }

    private void setFlag(int flag, boolean value) {
        byte flags = this.entityData.get(DATA_FLAGS);
        this.entityData.set(DATA_FLAGS, (byte) (value ? flags | flag : flags & ~flag));
    }

    private boolean getFlag(int flag) {
        return (this.entityData.get(DATA_FLAGS) & flag) != 0;
    }

    public boolean isAttacking() {
        return this.getFlag(FLAG_ATTACKING);
    }

    public boolean isExoSprinting() {
        return this.sprintInput || this.getFlag(FLAG_SPRINTING);
    }

    public boolean isShieldActive() {
        return this.getFlag(FLAG_SHIELD);
    }

    public boolean isOverloadActive() {
        return this.getFlag(FLAG_OVERLOAD);
    }

    protected void activateShield(int ticks) {
        this.shieldTicks = ticks;
        this.setFlag(FLAG_SHIELD, true);
    }

    protected void activateOverload(int ticks) {
        this.overloadTicks = ticks;
        this.setFlag(FLAG_OVERLOAD, true);
    }

    public int getCooldown(int slot) {
        if (slot < 0 || slot >= ABILITY_SLOTS) {
            return 0;
        }
        return (this.entityData.get(DATA_COOLDOWNS) >>> (slot * COOLDOWN_BITS)) & COOLDOWN_MASK;
    }

    private void syncCooldowns() {
        int packed = 0;
        for (int slot = 0; slot < ABILITY_SLOTS; slot++) {
            packed |= Math.min(this.cooldowns[slot], COOLDOWN_MASK) << (slot * COOLDOWN_BITS);
        }
        this.entityData.set(DATA_COOLDOWNS, packed);
    }

    private void tickCooldowns() {
        boolean running = false;
        boolean finished = false;
        for (int slot = 0; slot < ABILITY_SLOTS; slot++) {
            if (this.cooldowns[slot] > 0) {
                this.cooldowns[slot]--;
                running = true;
                if (this.cooldowns[slot] == 0) {
                    finished = true;
                }
            }
        }
        if (finished || (running && (this.tickCount & 3) == 0)) {
            this.syncCooldowns();
        }
    }

    // ------------------------------------------------------------------
    // AI: a parked suit is a machine, not a mob. It never fights on its own.
    // ------------------------------------------------------------------

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    @Override
    protected void serverAiStep() {
        if (this.isVehicle()) {
            // The pilot owns rotation and movement. Running goals, look control
            // or navigation here is exactly what made steering jitter before.
            this.getNavigation().stop();
            return;
        }
        super.serverAiStep();
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    // ------------------------------------------------------------------
    // tick
    // ------------------------------------------------------------------

    @Override
    public void tick() {
        super.tick();

        if (this.attackTicks > 0) {
            this.attackTicks--;
        }
        if (this.onGround()) {
            this.airThrusts = 0;
        }

        if (this.level().isClientSide) {
            if (this.isAttacking() && this.attackTicks <= 0) {
                this.attackTicks = 10;
            }
            return;
        }

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
        if (this.attackTicks <= 0 && this.isAttacking()) {
            this.setFlag(FLAG_ATTACKING, false);
        }

        this.tickCooldowns();
        this.tickEnergy();

        if (this.shieldTicks > 0 && --this.shieldTicks == 0) {
            this.setFlag(FLAG_SHIELD, false);
        }
        if (this.overloadTicks > 0 && --this.overloadTicks == 0) {
            this.setFlag(FLAG_OVERLOAD, false);
            // Overload always ends in a vulnerability window, as the lore wants.
            this.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
        }

        if (!this.isVehicle()) {
            this.sprintInput = false;
            this.setFlag(FLAG_SPRINTING, false);
            this.setTarget(null);
        }
    }

    private void tickEnergy() {
        boolean moving = this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-4D;
        if (this.isVehicle() && (moving || this.isExoSprinting())) {
            if (++this.drainTimer >= 20) {
                this.drainTimer = 0;
                this.consumeEnergy(this.tier.energyDrain() * (this.isExoSprinting() ? 3 : 1));
                if (this.getEnergy() <= 0) {
                    this.setExoSprint(false);
                }
            }
        } else if (this.getEnergy() < this.tier.maxEnergy()) {
            if (++this.regenTimer >= 20) {
                this.regenTimer = 0;
                this.addEnergy(this.tier.energyRegen());
            }
        }
    }

    // ------------------------------------------------------------------
    // steering
    // ------------------------------------------------------------------

    @Override
    public LivingEntity getControllingPassenger() {
        Entity first = this.getFirstPassenger();
        return first instanceof LivingEntity living ? living : null;
    }

    public boolean isPilot(Entity entity) {
        return entity != null && entity == this.getControllingPassenger();
    }

    public void setExoSprint(boolean sprinting) {
        this.sprintInput = sprinting;
        if (!this.level().isClientSide) {
            this.setFlag(FLAG_SPRINTING, sprinting && this.getEnergy() > 0);
        }
    }

    private float getPilotedSpeed() {
        float base = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
        if (this.getEnergy() <= 0) {
            return base * 0.45F;
        }
        return this.isExoSprinting() ? base * (float) this.tier.sprintMultiplier() : base;
    }

    @Override
    public void travel(Vec3 travelVector) {
        LivingEntity pilot = this.getControllingPassenger();
        if (!this.isAlive() || pilot == null) {
            super.travel(travelVector);
            return;
        }

        this.setYRot(pilot.getYRot());
        this.yRotO = this.getYRot();
        this.setXRot(pilot.getXRot() * 0.5F);
        this.setRot(this.getYRot(), this.getXRot());
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();

        float strafe = pilot.xxa * 0.5F;
        float forward = pilot.zza;
        if (forward <= 0.0F) {
            forward *= 0.3F;
        }

        if (this.isControlledByLocalInstance()) {
            this.setSpeed(this.getPilotedSpeed());
            super.travel(new Vec3(strafe, travelVector.y, forward));
        } else {
            this.setDeltaMovement(Vec3.ZERO);
            this.calculateEntityAnimation(false);
        }

        this.tryCheckInsideBlocks();
    }

    /**
     * Server side velocity changes are discarded on a client controlled vehicle,
     * so every impulse is mirrored to the piloting client.
     */
    public void applyImpulse(Vec3 impulse) {
        this.setDeltaMovement(impulse);
        this.hasImpulse = true;
        if (this.getControllingPassenger() instanceof ServerPlayer pilot) {
            ModNetwork.sendImpulse(pilot, impulse);
        }
    }

    // ------------------------------------------------------------------
    // jumping / thrusters
    // ------------------------------------------------------------------

    public boolean canGroundJump() {
        return this.onGround() && this.hasEnergy(ExoTier.JUMP_COST);
    }

    public boolean canAirThrust() {
        return !this.onGround() && this.tier.canAirThrust() && this.airThrusts < 1
                && this.hasEnergy(ExoTier.AIR_THRUST_COST);
    }

    /** Applied by the piloting client so the hop is instant and smooth. */
    public void doGroundJump() {
        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x, this.tier.jumpPower(), motion.z);
        this.hasImpulse = true;
    }

    /** Applied by the piloting client. */
    public void doAirThrust() {
        Vec3 look = this.getLookAngle();
        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x * 0.6D + look.x * 0.55D, 0.72D, motion.z * 0.6D + look.z * 0.55D);
        this.hasImpulse = true;
        this.airThrusts++;
    }

    /** Server side validation of a jump request. */
    public void onPilotJump(boolean airThrust) {
        if (this.level().isClientSide) {
            return;
        }
        if (airThrust) {
            if (!this.tier.canAirThrust() || this.airThrusts >= 1 || !this.hasEnergy(ExoTier.AIR_THRUST_COST)) {
                return;
            }
            this.consumeEnergy(ExoTier.AIR_THRUST_COST);
            this.airThrusts++;
            this.playSound(ModSounds.EXO_THRUST, 1.2F, 1.35F);
            this.spawnBurst(ParticleTypes.CLOUD, 14, 0.35D);
        } else {
            if (!this.hasEnergy(ExoTier.JUMP_COST)) {
                return;
            }
            this.consumeEnergy(ExoTier.JUMP_COST);
            this.playSound(ModSounds.EXO_THRUST, 0.7F, 0.9F);
        }
    }

    protected void spawnBurst(net.minecraft.core.particles.SimpleParticleType particle, int count, double spread) {
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(particle, this.getX(), this.getY() + 0.25D, this.getZ(), count, spread, 0.1D, spread, 0.05D);
        }
    }

    // ------------------------------------------------------------------
    // combat
    // ------------------------------------------------------------------

    /** Entry point for a pilot triggered melee swing. Fully server validated. */
    public void pilotAttack(ServerPlayer pilot) {
        if (this.level().isClientSide || !this.isPilot(pilot) || this.attackCooldown > 0) {
            return;
        }
        this.attackCooldown = this.tier.attackCooldown();
        this.attackTicks = 10;
        this.setFlag(FLAG_ATTACKING, true);
        this.swing(InteractionHand.MAIN_HAND, true);

        LivingEntity target = this.findPilotTarget(pilot);
        if (target == null) {
            this.playSound(ModSounds.KATANA_SWING, 0.7F, 0.7F);
            return;
        }
        this.setTarget(target);
        this.doHurtTarget(target);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        float damage = this.getAttackDamage();
        boolean hurt = target.hurt(this.damageSources().mobAttack(this), damage);
        if (hurt) {
            this.doEnchantDamageEffects(this, target);
            if (target instanceof LivingEntity living) {
                living.knockback(0.4D + this.getBbWidth() * 0.15D,
                        this.getX() - target.getX(), this.getZ() - target.getZ());
            }
            this.playSound(ModSounds.HAMMER_HIT, 0.9F, 0.8F);
        }
        return hurt;
    }

    /**
     * Ray from the suit's own eye along the pilot's view, then a forward cone as
     * a fallback so huge frames do not whiff on things standing at their feet.
     */
    protected LivingEntity findPilotTarget(Player pilot) {
        double reach = this.tier.attackReach();
        Vec3 eye = this.getEyePosition(1.0F);
        Vec3 view = pilot.getViewVector(1.0F);
        Vec3 end = eye.add(view.scale(reach));
        AABB search = this.getBoundingBox().expandTowards(view.scale(reach)).inflate(1.0D);

        EntityHitResult hit = ProjectileUtil.getEntityHitResult(this, eye, end, search,
                entity -> this.isValidCombatTarget(entity, pilot), reach * reach);
        if (hit != null && hit.getEntity() instanceof LivingEntity living) {
            return living;
        }

        LivingEntity best = null;
        double bestDistance = reach * reach;
        for (LivingEntity candidate : this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(reach), entity -> this.isValidCombatTarget(entity, pilot))) {
            Vec3 toTarget = candidate.getEyePosition().subtract(eye);
            if (toTarget.lengthSqr() < 1.0E-4D || toTarget.normalize().dot(view) < 0.35D) {
                continue;
            }
            double distance = candidate.distanceToSqr(this);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = candidate;
            }
        }
        return best;
    }

    protected boolean isValidCombatTarget(Entity entity, Player pilot) {
        if (!(entity instanceof LivingEntity living) || living == this || living == pilot) {
            return false;
        }
        if (living instanceof ExosuitEntity) {
            return false;
        }
        if (living instanceof Player player && (player.isSpectator() || player.isCreative())) {
            return false;
        }
        return living.isAlive() && living.isPickable() && !living.isAlliedTo(this);
    }

    /** Piercing hit-scan beam shared by the laser style abilities. */
    protected void beamAttack(ServerPlayer pilot, double range, float damage) {
        Vec3 eye = this.getEyePosition(1.0F);
        Vec3 direction = pilot != null ? pilot.getViewVector(1.0F) : this.getLookAngle();
        Vec3 end = eye.add(direction.scale(range));
        AABB box = new AABB(eye, end).inflate(1.5D);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, box,
                entity -> this.isValidCombatTarget(entity, pilot))) {
            if (target.getBoundingBox().inflate(0.4D).clip(eye, end).isPresent()) {
                target.hurt(this.damageSources().mobAttack(this), damage);
            }
        }
        if (this.level() instanceof ServerLevel server) {
            for (int step = 1; step < range; step++) {
                Vec3 point = eye.add(direction.scale(step));
                server.sendParticles(ParticleTypes.END_ROD, point.x, point.y, point.z, 2, 0.05D, 0.05D, 0.05D, 0.0D);
            }
        }
        this.playSound(ModSounds.SUPER_LASER, 1.0F, 1.5F);
    }

    /** Hits everything inside a forward cone. */
    protected int coneAttack(ServerPlayer pilot, double range, double minDot, float damage) {
        Vec3 eye = this.getEyePosition(1.0F);
        Vec3 view = pilot != null ? pilot.getViewVector(1.0F) : this.getLookAngle();
        int hits = 0;
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(range), entity -> this.isValidCombatTarget(entity, pilot))) {
            Vec3 toTarget = target.getEyePosition().subtract(eye);
            if (toTarget.lengthSqr() < 1.0E-4D || toTarget.normalize().dot(view) < minDot) {
                continue;
            }
            if (target.hurt(this.damageSources().mobAttack(this), damage)) {
                hits++;
            }
        }
        return hits;
    }

    // ------------------------------------------------------------------
    // abilities
    // ------------------------------------------------------------------

    public abstract ExoAbility getAbility(int slot);

    protected abstract void runAbility(int slot, ServerPlayer pilot);

    /**
     * Extra per-suit gate checked before any energy is spent. Override it for
     * state based abilities so a refused ability never eats the pilot's charge.
     */
    protected boolean canUseAbility(int slot, ServerPlayer pilot) {
        return true;
    }

    public void tryUseAbility(int slot, ServerPlayer pilot) {
        if (this.level().isClientSide || !this.isPilot(pilot) || slot < 0 || slot >= ABILITY_SLOTS) {
            return;
        }
        ExoAbility ability = this.getAbility(slot);
        if (ability == null || ability.isNone()) {
            this.feedback(pilot, "message.opusvsexe.exo.no_ability");
            return;
        }
        if (!this.canUseAbility(slot, pilot)) {
            return;
        }
        if (this.cooldowns[slot] > 0) {
            this.feedback(pilot, "message.opusvsexe.exo.cooldown");
            return;
        }
        if (!this.hasEnergy(ability.energyCost())) {
            this.feedback(pilot, "message.opusvsexe.exo.no_energy");
            return;
        }
        this.consumeEnergy(ability.energyCost());
        this.cooldowns[slot] = ability.cooldown();
        this.syncCooldowns();
        this.runAbility(slot, pilot);
    }

    protected void feedback(ServerPlayer pilot, String translationKey) {
        if (pilot != null) {
            pilot.displayClientMessage(Component.translatable(translationKey), true);
        }
    }

    // ------------------------------------------------------------------
    // boarding / seating / dismounting
    // ------------------------------------------------------------------

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                player.openMenu(this.getMenuProvider());
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        if (this.isVehicle()) {
            return InteractionResult.PASS;
        }
        if (!this.level().isClientSide) {
            player.startRiding(this);
            player.displayClientMessage(
                    Component.translatable("message.opusvsexe.exo.boarded", this.tier.displayName()), true);
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().isEmpty();
    }

    /** Seats the pilot at the frame's head height, so the camera and reach line up. */
    @Override
    public double getPassengersRidingOffset() {
        return Math.max(0.45D, this.getEyeHeight() - 1.62D);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        double radius = this.getBbWidth() * 0.5D + passenger.getBbWidth() * 0.5D + 0.35D;
        float[] offsets = {0.0F, 90.0F, 180.0F, 270.0F, 45.0F, 135.0F, 225.0F, 315.0F};
        for (float offset : offsets) {
            double angle = Math.toRadians(this.getYRot() + offset);
            double x = this.getX() - Math.sin(angle) * radius;
            double z = this.getZ() + Math.cos(angle) * radius;
            for (int dy = 0; dy >= -4; dy--) {
                double y = this.getY() + dy;
                AABB box = passenger.getDimensions(Pose.STANDING).makeBoundingBox(x, y, z);
                boolean freeSpace = this.level().noCollision(passenger, box);
                boolean hasFloor = !this.level().noCollision(passenger, box.move(0.0D, -0.35D, 0.0D));
                if (freeSpace && hasFloor) {
                    return new Vec3(x, y, z);
                }
            }
        }
        return super.getDismountLocationForPassenger(passenger);
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (!this.level().isClientSide) {
            this.setExoSprint(false);
            this.setTarget(null);
            this.setFlag(FLAG_ATTACKING, false);
        }
    }

    // ------------------------------------------------------------------
    // durability
    // ------------------------------------------------------------------

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (this.isPilot(source.getEntity())) {
            return false;
        }
        if (source.is(DamageTypes.FALL) || source.is(DamageTypes.CACTUS)
                || source.is(DamageTypes.SWEET_BERRY_BUSH) || source.is(DamageTypes.IN_WALL)) {
            return false;
        }
        if (this.isShieldActive()) {
            amount *= 0.35F;
            this.consumeEnergy(10);
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        if (!this.level().isClientSide && distance > 6.0F) {
            this.playSound(ModSounds.SHOCKWAVE, 0.6F, 1.4F);
            this.spawnBurst(ParticleTypes.POOF, 20, this.getBbWidth() * 0.5D);
        }
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void die(DamageSource source) {
        this.ejectPassengers();
        super.die(source);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        for (int slot = 0; slot < this.inventory.getContainerSize(); slot++) {
            ItemStack stack = this.inventory.getItem(slot);
            if (!stack.isEmpty()) {
                this.spawnAtLocation(stack);
            }
        }
        this.inventory.clearContent();
    }

    // ------------------------------------------------------------------
    // inventory
    // ------------------------------------------------------------------

    public ExoContainer getInventory() {
        return this.inventory;
    }

    public SimpleMenuProvider getMenuProvider() {
        return new SimpleMenuProvider(
                (id, playerInventory, player) -> new ExoInventoryMenu(id, playerInventory, this),
                Component.translatable("container.opusvsexe.exo_inventory"));
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.inventory.getItem(ExoContainer.SLOT_WEAPON);
        }
        if (slot == EquipmentSlot.OFFHAND) {
            return this.inventory.getItem(ExoContainer.SLOT_MODULE);
        }
        return super.getItemBySlot(slot);
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND) {
            this.inventory.setItem(ExoContainer.SLOT_WEAPON, stack);
            return;
        }
        if (slot == EquipmentSlot.OFFHAND) {
            this.inventory.setItem(ExoContainer.SLOT_MODULE, stack);
            return;
        }
        super.setItemSlot(slot, stack);
    }

    @Override
    public Iterable<ItemStack> getHandSlots() {
        return List.of(this.inventory.getItem(ExoContainer.SLOT_WEAPON),
                this.inventory.getItem(ExoContainer.SLOT_MODULE));
    }

    // ------------------------------------------------------------------
    // persistence
    // ------------------------------------------------------------------

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ExoEnergy", this.getEnergy());
        tag.putInt("ExoShield", this.shieldTicks);
        tag.putInt("ExoOverload", this.overloadTicks);
        tag.put("ExoItems", this.inventory.createTag());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("ExoEnergy")) {
            this.setEnergy(tag.getInt("ExoEnergy"));
        }
        if (tag.contains("ExoItems", Tag.TAG_LIST)) {
            this.inventory.fromTag(tag.getList("ExoItems", Tag.TAG_COMPOUND));
        }
        this.shieldTicks = tag.getInt("ExoShield");
        this.overloadTicks = tag.getInt("ExoOverload");
        this.setFlag(FLAG_SHIELD, this.shieldTicks > 0);
        this.setFlag(FLAG_OVERLOAD, this.overloadTicks > 0);
    }

    // ------------------------------------------------------------------
    // legacy API shims (kept so unrelated call sites still compile)
    // ------------------------------------------------------------------

    /** @deprecated use {@link #tryUseAbility(int, ServerPlayer)} */
    @Deprecated
    public void performAbility() {
        this.performAbility(0);
    }

    /** @deprecated use {@link #tryUseAbility(int, ServerPlayer)} */
    @Deprecated
    public void performAbility(int slot) {
        if (this.getControllingPassenger() instanceof ServerPlayer pilot) {
            this.tryUseAbility(slot, pilot);
        }
    }

    /** @deprecated use {@link #pilotAttack(ServerPlayer)} */
    @Deprecated
    public void performAttack() {
        if (this.getControllingPassenger() instanceof ServerPlayer pilot) {
            this.pilotAttack(pilot);
        }
    }

    /** @deprecated use {@link #pilotAttack(ServerPlayer)} */
    @Deprecated
    public void attackWithRider(LivingEntity target) {
        if (target != null && target.isAlive()) {
            this.setTarget(target);
            this.doHurtTarget(target);
        }
    }

    /** @deprecated jumping is driven by the input handler now */
    @Deprecated
    public void rideJump() {
        if (this.canGroundJump()) {
            this.doGroundJump();
        }
    }

    @Deprecated
    public boolean isRideableBy(Player player) {
        return !this.isVehicle() || this.isPilot(player);
    }

    // ------------------------------------------------------------------
    // GeckoLib
    // ------------------------------------------------------------------

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "exo_controller", 0, this::animationPredicate));
    }

    protected PlayState animationPredicate(AnimationState<ExosuitEntity> state) {
        if (this.attackTicks > 0) {
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
}

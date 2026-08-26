package com.opusvsexe.entity.custom;

import com.opus.entity.BlasterBeamEntity;
import com.opus.entity.PunchShockwaveEntity;
import com.opus.network.ModNetwork;
import com.opus.sound.ModSounds;
import com.opusvsexe.inventory.ExoInventoryMenu;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
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

    public static final int ABILITY_SLOTS = 4;

    private static final EntityDataAccessor<Integer> DATA_ENERGY =
            SynchedEntityData.defineId(ExosuitEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> DATA_FLAGS =
            SynchedEntityData.defineId(ExosuitEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Long> DATA_COOLDOWNS =
            SynchedEntityData.defineId(ExosuitEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Integer> DATA_ABILITY_ANIM =
            SynchedEntityData.defineId(ExosuitEntity.class, EntityDataSerializers.INT);

    private static final int FLAG_ATTACKING = 1;
    private static final int FLAG_SPRINTING = 2;
    private static final int FLAG_SHIELD = 4;
    private static final int FLAG_OVERLOAD = 8;
    private static final int FLAG_PUNCHING = 16;

    public static final int PUNCH_COOLDOWN_TICKS = 60;
    private static final int PUNCH_BASE_ENERGY = 60;

    private static final int COOLDOWN_BITS = 10;
    private static final int COOLDOWN_MASK = (1 << COOLDOWN_BITS) - 1;

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("attack");
    private static final RawAnimation HURT_ANIM = RawAnimation.begin().thenPlay("hurt");
    private static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlay("death");
    private static final RawAnimation BLOCK_ANIM = RawAnimation.begin().thenLoop("block");
    private static final RawAnimation PUNCH_ANIM = RawAnimation.begin().thenPlay("punch");

    protected final ExoTier tier;

    private final ExoContainer inventory = new ExoContainer();
    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);
    private final int[] cooldowns = new int[ABILITY_SLOTS];

    private RawAnimation cachedAbilityAnim;
    private int cachedAbilityAnimSlot = -1;

    private int attackTicks;
    private int attackCooldown;
    private int regenTimer;
    private int drainTimer;
    private int shieldTicks;
    private int overloadTicks;
    private int airThrusts;
    private boolean sprintInput;
    private int abilityAnimTicks;
    private int activeAbilitySlot = -1;
    private int punchTicks;
    private int punchCooldown;

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
        this.entityData.define(DATA_COOLDOWNS, 0L);
        this.entityData.define(DATA_ABILITY_ANIM, 0);
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

    public boolean isPunching() {
        return this.getFlag(FLAG_PUNCHING);
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
        return (int) ((this.entityData.get(DATA_COOLDOWNS) >>> (slot * COOLDOWN_BITS)) & COOLDOWN_MASK);
    }

    private void syncCooldowns() {
        long packed = 0L;
        for (int slot = 0; slot < ABILITY_SLOTS; slot++) {
            packed |= (long) Math.min(this.cooldowns[slot], COOLDOWN_MASK) << (slot * COOLDOWN_BITS);
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
    // AI: a parked suit is a machine, not a mob. It never fights on its own,
    // and while a pilot is inside every goal is inert - otherwise the look
    // goals fight the pilot for head/body rotation and steering jitters.
    // (Mob#serverAiStep is final in these mappings, so the goals themselves
    // carry the "piloted" gate.)
    // ------------------------------------------------------------------

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this) {
            @Override
            public boolean canUse() {
                return !ExosuitEntity.this.isVehicle() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !ExosuitEntity.this.isVehicle() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 12.0F) {
            @Override
            public boolean canUse() {
                return !ExosuitEntity.this.isVehicle() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !ExosuitEntity.this.isVehicle() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return !ExosuitEntity.this.isVehicle() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !ExosuitEntity.this.isVehicle() && super.canContinueToUse();
            }
        });
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
        if (this.punchTicks > 0) {
            this.punchTicks--;
        }
        if (this.onGround()) {
            this.airThrusts = 0;
        }

        if (this.level().isClientSide) {
            if (this.isAttacking() && this.attackTicks <= 0) {
                this.attackTicks = 10;
            }
            if (this.isPunching() && this.punchTicks <= 0) {
                this.punchTicks = 14;
            }
            return;
        }

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
        if (this.punchCooldown > 0) {
            this.punchCooldown--;
        }
        if (this.attackTicks <= 0 && this.isAttacking()) {
            this.setFlag(FLAG_ATTACKING, false);
        }
        if (this.punchTicks <= 0 && this.isPunching()) {
            this.setFlag(FLAG_PUNCHING, false);
        }

        if (this.isVehicle()) {
            // The pilot owns movement. Navigation has nothing to do here:
            // a half-built path is exactly what made steering jitter before.
            this.getNavigation().stop();
        }

        this.tickCooldowns();
        this.tickEnergy();

        if (this.abilityAnimTicks > 0 && --this.abilityAnimTicks == 0) {
            this.activeAbilitySlot = -1;
            this.entityData.set(DATA_ABILITY_ANIM, 0);
        }

        if (this.shieldTicks > 0 && --this.shieldTicks == 0) {
            this.setFlag(FLAG_SHIELD, false);
        } else if (this.isShieldActive()) {
            this.tickEnergyBarrier();
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
            this.playSound(ModSounds.EXO_THRUST, 1.0F, 1.35F);
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
    // shared abilities
    // ------------------------------------------------------------------

    /**
     * Energy Shield: absorption IV for the pilot plus a green energy barrier
     * around the frame. The barrier is a particle shell sized from the suit's
     * bounding box, so every model gets its own scale automatically.
     */
    protected void activateEnergyShield(ServerPlayer pilot) {
        int duration = 200;
        this.activateShield(duration);
        if (pilot != null) {
            pilot.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, duration, 3, false, true));
        }
        this.playSound(ModSounds.EXO_SHIELD, 1.0F, 1.0F);
        if (this.level() instanceof ServerLevel server) {
            double radius = this.barrierRadius();
            server.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    this.getX(), this.getY() + this.getBbHeight() * 0.5D, this.getZ(),
                    (int) (24 + radius * 10), radius * 0.9D, this.getBbHeight() * 0.45D, radius * 0.9D, 0.02D);
        }
    }

    /** Radius of the shield barrier, derived from the model footprint. */
    private double barrierRadius() {
        return Math.max(1.1D, this.getBbWidth() * 1.05D);
    }

    /**
     * Green barrier shell while the shield is up. Golden-angle spiral keeps the
     * sphere deterministic; white END_ROD appears sparingly as the core hotspot.
     */
    private void tickEnergyBarrier() {
        if ((this.tickCount & 3) != 0 || !(this.level() instanceof ServerLevel server)) {
            return;
        }
        double radius = this.barrierRadius();
        double height = this.getBbHeight();
        int points = (int) (14 + radius * 8);
        double goldenAngle = Math.PI * (3.0D - Math.sqrt(5.0D));
        for (int i = 0; i < points; i++) {
            double phi = goldenAngle * i + this.tickCount * 0.7D;
            double band = 2.0D * i / (points - 1) - 1.0D;
            double ring = Math.sqrt(Math.max(0.0D, 1.0D - band * band));
            double x = this.getX() + Math.cos(phi) * ring * radius;
            double z = this.getZ() + Math.sin(phi) * ring * radius;
            double y = this.getY() + (band * 0.5D + 0.55D) * height;
            server.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            if ((i & 7) == 0) {
                server.sendParticles(ParticleTypes.COMPOSTER, x, y, z, 1, 0.05D, 0.05D, 0.05D, 0.0D);
            }
        }
        if ((this.tickCount & 7) == 0) {
            server.sendParticles(ParticleTypes.END_ROD,
                    this.getX(), this.getY() + height * 0.62D, this.getZ(), 1, 0.1D, 0.1D, 0.1D, 0.0D);
        }
    }

    /**
     * Dash: a ~{@code distance}-block charge along the pilot's aim. Everything
     * in the corridor is blown apart, entities inside take heavy damage, and the
     * suit itself flies forward via {@link #applyImpulse(Vec3)}.
     */
    protected void performDash(ServerPlayer pilot, double distance, float damageScale) {
        Vec3 look = pilot != null ? pilot.getViewVector(1.0F) : this.getLookAngle();
        Vec3 dir = look.normalize();
        Vec3 start = this.position().add(0.0D, this.getBbHeight() * 0.5D, 0.0D);
        Vec3 end = start.add(dir.scale(distance));
        double radius = Math.max(1.2D, this.getBbWidth() * 0.9D);

        this.breakDashCorridor(start, dir, distance, radius);

        AABB corridor = new AABB(start, end).inflate(radius + 0.5D);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, corridor,
                entity -> this.isValidCombatTarget(entity, pilot))) {
            if (target.hurt(this.damageSources().mobAttack(this), this.getAttackDamage() * damageScale)) {
                target.knockback(1.2D, this.getX() - target.getX(), this.getZ() - target.getZ());
                target.addDeltaMovement(new Vec3(dir.x * 0.8D, 0.3D, dir.z * 0.8D));
            }
        }

        float speed = (float) (distance / 11.0D);
        this.applyImpulse(dir.scale(speed));

        this.playSound(ModSounds.EXO_THRUST, 1.2F, 0.7F);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE, this.getSoundSource(), 1.0F, 1.1F);
        if (this.level() instanceof ServerLevel server) {
            for (double d = 2.0D; d <= distance; d += 4.0D) {
                Vec3 point = start.add(dir.scale(d));
                server.sendParticles(ParticleTypes.EXPLOSION_EMITTER, point.x, point.y, point.z, 1, 0.2D, 0.2D, 0.2D, 0.0D);
            }
            this.spawnBurst(ParticleTypes.CLOUD, 16, 0.6D);
        }
    }

    /** Destroys every breakable block inside the dash corridor. */
    private void breakDashCorridor(Vec3 start, Vec3 dir, double distance, double radius) {
        if (!(this.level() instanceof ServerLevel server)) {
            return;
        }
        for (double d = 0.0D; d <= distance; d += 0.5D) {
            Vec3 point = start.add(dir.scale(d));
            BlockPos center = BlockPos.containing(point.x, point.y, point.z);
            for (BlockPos pos : BlockPos.betweenClosed(
                    center.offset((int) -radius, (int) -radius, (int) -radius),
                    center.offset((int) radius, (int) radius, (int) radius))) {
                BlockState state = server.getBlockState(pos);
                if (state.isAir() || state.getDestroySpeed(server, pos) < 0.0F
                        || state.liquid() || server.getBlockEntity(pos) != null) {
                    continue;
                }
                server.destroyBlock(pos.immutable(), false);
            }
        }
    }

    /**
     * Fires a directional beam entity from the chest along the pilot's aim.
     * Shared by the heavy laser style abilities (EXO-2 Cutting Laser, EXO-5).
     */
    protected void spawnDirectionalBeam(BlasterBeamEntity beam, ServerPlayer pilot,
                                        net.minecraft.sounds.SoundEvent sound, float volume, float pitch) {
        Level level = this.level();
        Vec3 eye = this.getEyePosition(1.0F);
        Vec3 look = pilot != null ? pilot.getViewVector(1.0F) : this.getLookAngle();
        double range = 75.0D;
        double spawnOffset = 1.2D;
        Vec3 spawn = eye.add(look.scale(spawnOffset));

        BlockHitResult hit = level.clip(new ClipContext(eye, eye.add(look.scale(range)),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        double beamLength;
        if (hit.getType() == HitResult.Type.MISS) {
            beamLength = range - spawnOffset;
        } else {
            beamLength = Math.max(1.0D, eye.distanceTo(hit.getLocation()) - spawnOffset);
        }

        beam.setPos(spawn);
        beam.faceTo(pilot != null ? pilot.getYRot() : this.getYRot(), pilot != null ? pilot.getXRot() : this.getXRot());
        beam.setDeltaMovement(beam.getBeamDirection());
        beam.setSyncedDirection(beam.getBeamDirection());
        beam.setShooter(pilot != null ? pilot.getUUID() : this.getUUID());
        beam.setBeamLength((float) beamLength);
        level.addFreshEntity(beam);

        this.playSound(sound, volume, pitch);
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

    /** Hidden B-key action shared by all frames; separate from the four HUD ability slots. */
    public void tryResonancePunch(ServerPlayer pilot) {
        if (this.level().isClientSide || !this.isPilot(pilot) || !this.isAlive()) {
            return;
        }
        if (this.punchCooldown > 0) {
            this.feedback(pilot, "message.opusvsexe.exo.punch_cooldown");
            return;
        }
        int energyCost = PUNCH_BASE_ENERGY + this.tier.ordinal() * 20;
        if (!this.hasEnergy(energyCost)) {
            this.feedback(pilot, "message.opusvsexe.exo.no_energy");
            return;
        }
        this.consumeEnergy(energyCost);
        this.punchCooldown = PUNCH_COOLDOWN_TICKS;
        this.punchTicks = 14;
        this.setFlag(FLAG_PUNCHING, true);
        PunchShockwaveEntity.spawn(this, pilot);
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

    /**
     * Animation name played when ability {@code slot} fires. Returns {@code null}
     * by default, so suits without ability animations keep their old behaviour.
     * Exo+ overrides this to map each slot to its dedicated ability animation.
     */
    protected String abilityAnimName(int slot) {
        return null;
    }

    /** How long (in ticks) the ability animation flag stays raised. */
    protected int abilityAnimDuration(int slot) {
        return 20;
    }

    /** Which slot is currently animating, or -1. Read by the client via synced data. */
    public int getAbilityAnimSlot() {
        int value = this.entityData.get(DATA_ABILITY_ANIM);
        return value > 0 ? value - 1 : -1;
    }

    protected void startAbilityAnim(int slot) {
        this.activeAbilitySlot = slot;
        this.abilityAnimTicks = this.abilityAnimDuration(slot);
        this.entityData.set(DATA_ABILITY_ANIM, slot + 1);
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
        this.startAbilityAnim(slot);
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
            this.setFlag(FLAG_PUNCHING, false);
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
        tag.putInt("ExoPunchCooldown", this.punchCooldown);
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
        this.punchCooldown = tag.getInt("ExoPunchCooldown");
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
        AnimationController<ExosuitEntity> controller = state.getController();

        if (this.isDeadOrDying()) {
            this.playOnce(state, DEATH_ANIM, false);
            return PlayState.CONTINUE;
        }

        if (this.isPunching() || this.punchTicks > 0) {
            this.playOnce(state, PUNCH_ANIM, true);
            return PlayState.CONTINUE;
        }

        int abilitySlot = this.getAbilityAnimSlot();
        if (abilitySlot >= 0) {
            String animName = this.abilityAnimName(abilitySlot);
            if (animName != null) {
                if (this.cachedAbilityAnim == null || this.cachedAbilityAnimSlot != abilitySlot) {
                    this.cachedAbilityAnim = RawAnimation.begin().thenPlay(animName);
                    this.cachedAbilityAnimSlot = abilitySlot;
                }
                this.playOnce(state, this.cachedAbilityAnim, true);
                return PlayState.CONTINUE;
            }
        }

        if (this.attackTicks > 0) {
            this.playOnce(state, ATTACK_ANIM, true);
        } else if (this.hurtTime > 0) {
            this.playOnce(state, HURT_ANIM, true);
        } else if (this.isShieldActive()) {
            controller.setAnimation(BLOCK_ANIM);
        } else if (state.isMoving()) {
            controller.setAnimation(WALK_ANIM);
        } else {
            controller.setAnimation(IDLE_ANIM);
        }
        return PlayState.CONTINUE;
    }

    /**
     * Безопасный запуск анимации с принудительным сбросом. GeckoLib 4.4.9 не
     * перезапускает одноразовую анимацию с тем же RawAnimation — поэтому при
     * повторном запуске (attack/hurt/ability) выполняется forceAnimationReset.
     * {@code repeat=false} для смерти: сыграть один раз и остаться на последнем кадре.
     */
    protected <T extends GeoAnimatable> void playOnce(AnimationState<T> state, RawAnimation anim, boolean repeat) {
        AnimationController<T> controller = state.getController();
        RawAnimation current = controller.getCurrentRawAnimation();
        boolean finished = controller.hasAnimationFinished();
        if (current == null || !current.equals(anim) || (repeat && finished)) {
            controller.forceAnimationReset();
        }
        controller.setAnimation(anim);
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

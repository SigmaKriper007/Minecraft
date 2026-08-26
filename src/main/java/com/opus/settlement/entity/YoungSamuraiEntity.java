package com.opus.settlement.entity;

import com.opus.settlement.SettlementLine;
import com.opus.settlement.registry.SettlementItems;
import com.opus.settlement.sound.SettlementSounds;
import com.opus.sound.BossMusicHub;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/** Two-phase sword and taijutsu boss guarding the Japanese settlement court. */
public final class YoungSamuraiEntity extends Monster {
    public enum Action {
        NONE(0, 0), PHASE(1, 34), CRIMSON_DRAW(2, 28), CRESCENT_SWEEP(3, 36),
        RISING_KNEE(4, 40), LOTUS_BARRAGE(5, 34), FLASH_STEP(6, 26);

        final int id;
        final int duration;
        Action(int id, int duration) { this.id = id; this.duration = duration; }
        static Action byId(int id) { for (Action action : values()) if (action.id == id) return action; return NONE; }
    }

    private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(YoungSamuraiEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ACTION = SynchedEntityData.defineId(YoungSamuraiEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ACTION_TICK = SynchedEntityData.defineId(YoungSamuraiEntity.class, EntityDataSerializers.INT);
    private static final DustParticleOptions AURA = new DustParticleOptions(new Vector3f(.72F, .08F, .42F), 1.35F);
    private static final DustParticleOptions EDGE = new DustParticleOptions(new Vector3f(.42F, .03F, .75F), 1F);
    private final ServerBossEvent bossBar = new ServerBossEvent(Component.translatable("entity.opusvsexe.young_samurai"), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.NOTCHED_10);
    private BlockPos arenaAnchor;
    private int abilityCooldown = 36;
    private int lastAction = -1;
    private final java.util.Set<java.util.UUID> musicAudience = new java.util.HashSet<>();

    public YoungSamuraiEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        xpReward = 700;
        setPersistenceRequired();
    }

    @Override protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(PHASE, 1);
        entityData.define(ACTION, Action.NONE.id);
        entityData.define(ACTION_TICK, 0);
    }

    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, .8D));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 36F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 360D).add(Attributes.ARMOR, 12D)
            .add(Attributes.ATTACK_DAMAGE, 14D).add(Attributes.MOVEMENT_SPEED, .38D)
            .add(Attributes.FOLLOW_RANGE, 56D).add(Attributes.KNOCKBACK_RESISTANCE, .8D);
    }

    @Override protected void populateDefaultEquipmentSlots(net.minecraft.util.RandomSource random, net.minecraft.world.DifficultyInstance difficulty) {
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(SettlementItems.LONG_KATANA));
        setDropChance(EquipmentSlot.MAINHAND, 0F);
    }

    @Override public boolean removeWhenFarAway(double distance) { return false; }
    public int getPhase() { return entityData.get(PHASE); }
    public boolean hasAura() { return getPhase() == 2; }
    public Action getAction() { return Action.byId(entityData.get(ACTION)); }
    public int getActionTick() { return entityData.get(ACTION_TICK); }
    public int abilityCooldownForPhase(int phase) { return phase == 2 ? 24 : 48; }
    public void setArenaAnchor(BlockPos anchor) { arenaAnchor = anchor.immutable(); }

    @Override public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide) return false;
        if (musicAudience.isEmpty() && source.getEntity() instanceof Player && level() instanceof ServerLevel server) {
            BossMusicHub.start(server, this, SettlementSounds.JAPANESE_FIGHT, 64.0D);
            for (ServerPlayer p : server.players()) if (p.distanceToSqr(this) <= 64 * 64) musicAudience.add(p.getUUID());
        }
        boolean projectile = source.is(DamageTypeTags.IS_PROJECTILE);
        if (projectile || random.nextFloat() < .30F) {
            evade(source.getEntity());
            return false;
        }
        return super.hurt(source, amount);
    }

    private void evade(Entity attacker) {
        Vec3 away = attacker == null ? getLookAngle().scale(-1D) : position().subtract(attacker.position());
        away = new Vec3(away.x, 0, away.z);
        if (away.lengthSqr() < .01D) away = new Vec3(1, 0, 0); else away = away.normalize();
        Vec3 side = new Vec3(-away.z, 0, away.x).scale(random.nextBoolean() ? 1D : -1D);
        teleportSafely(position().add(away.scale(4.5D)).add(side.scale(3D)));
    }

    private boolean teleportSafely(Vec3 wanted) {
        if (!(level() instanceof ServerLevel server)) return false;
        Vec3 old = position();
        for (int radius = 0; radius <= 5; radius++) {
            for (int dx = -radius; dx <= radius; dx++) for (int dz = -radius; dz <= radius; dz++) for (int dy = 4; dy >= -4; dy--) {
                BlockPos feet = BlockPos.containing(wanted).offset(dx, dy, dz);
                if (!server.getBlockState(feet.below()).isFaceSturdy(server, feet.below(), Direction.UP)) continue;
                AABB moved = getBoundingBox().move(feet.getX() + .5D - getX(), feet.getY() - getY(), feet.getZ() + .5D - getZ());
                if (!server.noCollision(this, moved)) continue;
                server.sendParticles(ParticleTypes.PORTAL, old.x, old.y + 1D, old.z, 28, .45D, .8D, .45D, .15D);
                teleportTo(feet.getX() + .5D, feet.getY(), feet.getZ() + .5D);
                server.sendParticles(EDGE, getX(), getY() + 1D, getZ(), 20, .5D, .9D, .5D, .04D);
                server.playSound(null, blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.25F, 1.35F);
                return true;
            }
        }
        return false;
    }

    @Override protected void customServerAiStep() {
        super.customServerAiStep();
        if (getMainHandItem().isEmpty()) setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(SettlementItems.LONG_KATANA));
        if (arenaAnchor == null) arenaAnchor = blockPosition();
        bossBar.setProgress(getHealth() / getMaxHealth());
        updatePhase();
        enforceArena();
        emitAura();
        tickAction();
        tickMovement();
        tickMusic();
    }

    private void tickMusic() {
        if (!(level() instanceof ServerLevel server) || tickCount % 40 != 0) return;
        boolean any = false;
        for (ServerPlayer p : server.players()) {
            if (p.distanceToSqr(this) > 64 * 64) continue;
            any = true;
            if (musicAudience.add(p.getUUID())) {
                p.connection.send(new net.minecraft.network.protocol.game.ClientboundStopSoundPacket(SettlementSounds.JAPANESE_FIGHT.getLocation(), SoundSource.RECORDS));
                p.playNotifySound(SettlementSounds.JAPANESE_FIGHT, SoundSource.RECORDS, 1.0F, 1.0F);
            }
        }
        if (!any) { BossMusicHub.stop(server, SettlementSounds.JAPANESE_FIGHT); musicAudience.clear(); }
    }

    @Override public void die(net.minecraft.world.damagesource.DamageSource source) {
        if (level() instanceof ServerLevel server) {
            BossMusicHub.stop(server, SettlementSounds.JAPANESE_FIGHT);
            for (ServerPlayer p : server.players()) {
                p.connection.send(new net.minecraft.network.protocol.game.ClientboundStopSoundPacket(SettlementSounds.YOUNG_SAMURAI_DEFEATED.getLocation(), SoundSource.RECORDS));
                if (p.distanceToSqr(this) <= 64 * 64) p.playNotifySound(SettlementSounds.YOUNG_SAMURAI_DEFEATED, SoundSource.RECORDS, 1.0F, 1.05F);
            }
        }
        super.die(source);
    }

    private void updatePhase() {
        if (getPhase() == 1 && getHealth() <= getMaxHealth() * .5F) {
            entityData.set(PHASE, 2);
            bossBar.setName(Component.translatable("entity.opusvsexe.young_samurai.phase2"));
            setAction(Action.PHASE);
            level().playSound(null, this, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.HOSTILE, 2F, .6F);
        }
    }

    private void enforceArena() {
        Vec3 center = Vec3.atCenterOf(arenaAnchor);
        if (position().distanceToSqr(center) > 22D * 22D) teleportSafely(center.add(0, 1, 0));
    }

    private void emitAura() {
        if (!hasAura() || !(level() instanceof ServerLevel server) || tickCount % 2 != 0) return;
        double angle = tickCount * .32D;
        for (int i = 0; i < 5; i++) {
            double a = angle + Math.PI * 2D * i / 5D;
            server.sendParticles(i % 2 == 0 ? AURA : EDGE, getX() + Math.cos(a) * .85D,
                getY() + .25D + (i * .42D + tickCount * .035D) % 2.2D, getZ() + Math.sin(a) * .85D,
                1, .04D, .08D, .04D, .01D);
        }
    }

    private void setAction(Action action) {
        entityData.set(ACTION, action.id);
        entityData.set(ACTION_TICK, 0);
        getNavigation().stop();
    }

    private void tickAction() {
        Action action = getAction();
        int tick = getActionTick();
        if (action != Action.NONE) {
            runAction(action, tick);
            tick++;
            entityData.set(ACTION_TICK, tick);
            if (tick >= action.duration) {
                entityData.set(ACTION, Action.NONE.id);
                entityData.set(ACTION_TICK, 0);
                abilityCooldown = abilityCooldownForPhase(getPhase());
            }
            return;
        }
        if (abilityCooldown > 0) { abilityCooldown--; return; }
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) { target = level().getNearestPlayer(this, 48D); setTarget(target); }
        if (target != null) setAction(selectAction());
    }

    private Action selectAction() {
        Action[] pool = getPhase() == 1
            ? new Action[]{Action.CRIMSON_DRAW, Action.CRESCENT_SWEEP, Action.RISING_KNEE, Action.FLASH_STEP}
            : new Action[]{Action.CRIMSON_DRAW, Action.CRESCENT_SWEEP, Action.RISING_KNEE, Action.LOTUS_BARRAGE, Action.FLASH_STEP};
        int index = random.nextInt(pool.length);
        if (pool[index].id == lastAction) index = (index + 1) % pool.length;
        lastAction = pool[index].id;
        return pool[index];
    }

    private void runAction(Action action, int tick) {
        LivingEntity target = getTarget();
        if (target != null) getLookControl().setLookAt(target, 60F, 60F);
        switch (action) {
            case PHASE -> {
                if (level() instanceof ServerLevel server && tick % 2 == 0)
                    server.sendParticles(AURA, getX(), getY() + 1D, getZ(), 22, 1.2D, 1.1D, 1.2D, .08D);
            }
            case CRIMSON_DRAW -> {
                if (tick == 7 && target != null) dashToward(target, 1.65D, .12D);
                if (tick == 11) damageArc(4.2D, 18F, 1.1D);
            }
            case CRESCENT_SWEEP -> { if (tick == 17) damageArc(5.5D, 16F, 1.5D); }
            case RISING_KNEE -> {
                if (tick == 5 && target != null) dashToward(target, .9D, .92D);
                if (tick == 15 && target != null && distanceToSqr(target) <= 12.25D) strike(target, 13F, .65D, .9D);
                if (tick == 24) damageAround(4D, 15F, 1D);
            }
            case LOTUS_BARRAGE -> { if (tick == 8 || tick == 14 || tick == 20 || tick == 26) damageArc(3.5D, 7F, .35D); }
            case FLASH_STEP -> {
                if (tick == 6 && target != null) teleportSafely(target.position().subtract(target.getLookAngle().scale(2.2D)));
                if (tick == 10 && target != null && distanceToSqr(target) <= 16D) strike(target, 17F, 1.2D, .2D);
            }
            default -> { }
        }
    }

    private void dashToward(LivingEntity target, double speed, double lift) {
        Vec3 direction = target.position().subtract(position());
        direction = new Vec3(direction.x, 0, direction.z);
        if (direction.lengthSqr() > .01D) setDeltaMovement(direction.normalize().scale(speed).add(0, lift, 0));
    }

    private void damageArc(double range, float damage, double knockback) {
        Vec3 facing = getLookAngle();
        facing = new Vec3(facing.x, 0, facing.z).normalize();
        for (Player player : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(range, 2D, range), Player::isAlive)) {
            Vec3 flat = player.position().subtract(position()); flat = new Vec3(flat.x, 0, flat.z);
            if (flat.lengthSqr() <= range * range && flat.lengthSqr() > .01D && flat.normalize().dot(facing) > -.15D)
                strike(player, damage, knockback, .25D);
        }
        swing(InteractionHand.MAIN_HAND);
        playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.4F, hasAura() ? .72F : .88F);
    }

    private void damageAround(double range, float damage, double knockback) {
        for (Player player : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(range, 2D, range), Player::isAlive))
            if (distanceToSqr(player) <= range * range) strike(player, damage, knockback, .45D);
        if (level() instanceof ServerLevel server) server.sendParticles(ParticleTypes.SWEEP_ATTACK, getX(), getY() + .2D, getZ(), 18, range * .45D, .1D, range * .45D, 0D);
        level().playSound(null, this, SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.2F, 1.4F);
    }

    private boolean strike(LivingEntity target, float damage, double knockback, double lift) {
        boolean hit = target.hurt(damageSources().mobAttack(this), damage);
        if (hit) {
            Vec3 away = target.position().subtract(position()); away = new Vec3(away.x, 0, away.z);
            if (away.lengthSqr() > .01D) { away = away.normalize(); target.push(away.x * knockback, lift, away.z * knockback); }
        }
        return hit;
    }

    private void tickMovement() {
        LivingEntity target = getTarget();
        if (getAction() == Action.NONE && target != null && target.isAlive()) {
            if (distanceToSqr(target) > 7D) getNavigation().moveTo(target, hasAura() ? 1.35D : 1.12D);
            else getNavigation().stop();
        }
    }

    public boolean shouldEvadeForQa(boolean projectile, float roll) { return projectile || roll < .30F; }
    public void evaluatePhaseForQa() { updatePhase(); }
    public void beginActionForQa(Action action) { setAction(action); }
    public boolean strikeForQa(LivingEntity target, float damage) { return strike(target, damage, 0D, 0D); }

    @Override protected net.minecraft.resources.ResourceLocation getDefaultLootTable() { return SettlementLine.id("entities/young_samurai"); }
    @Override public void startSeenByPlayer(ServerPlayer player) { super.startSeenByPlayer(player); bossBar.addPlayer(player); }
    @Override public void stopSeenByPlayer(ServerPlayer player) { super.stopSeenByPlayer(player); bossBar.removePlayer(player); }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Phase", getPhase()); tag.putInt("Action", getAction().id); tag.putInt("ActionTick", getActionTick());
        tag.putInt("AbilityCooldown", abilityCooldown); if (arenaAnchor != null) tag.put("ArenaAnchor", NbtUtils.writeBlockPos(arenaAnchor));
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(PHASE, Mth.clamp(tag.getInt("Phase"), 1, 2)); entityData.set(ACTION, tag.getInt("Action"));
        entityData.set(ACTION_TICK, tag.getInt("ActionTick")); abilityCooldown = tag.getInt("AbilityCooldown");
        if (tag.contains("ArenaAnchor")) arenaAnchor = NbtUtils.readBlockPos(tag.getCompound("ArenaAnchor"));
        bossBar.setName(Component.translatable(getPhase() == 2 ? "entity.opusvsexe.young_samurai.phase2" : "entity.opusvsexe.young_samurai"));
    }
}

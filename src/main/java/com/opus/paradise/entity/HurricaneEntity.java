package com.opus.paradise.entity;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** A living tornado: telegraphs, then sucks everything within {@link #RADIUS} into a
 *  spinning orbit, repeatedly pummels whatever reaches the eye, and finally flings
 *  its captives away with a burst. Cast by the paradise wyvern's Wind Core and by the
 *  Parthenon Regalia set bonus. */
public final class HurricaneEntity extends Entity implements GeoEntity {
    public static final int TELEGRAPH_END = 20;
    public static final int ACTIVE_END = 160;
    public static final int LIFETIME = 200;
    public static final double RADIUS = 13.0;
    public static final double INNER_RADIUS = 5.5;
    /** Ticks between storm damage pulses on entities inside the eye. */
    public static final int DAMAGE_INTERVAL = 14;
    public static final float EYE_DAMAGE = 4.0F;
    public static final float RELEASE_DAMAGE = 8.0F;
    private static final RawAnimation VORTEX = RawAnimation.begin().thenLoop("vortex");
    private static final DustParticleOptions CYAN = new DustParticleOptions(new Vector3f(0.22F, 0.95F, 1.0F), 1.05F);
    private static final DustParticleOptions GOLD = new DustParticleOptions(new Vector3f(1.0F, 0.78F, 0.24F), 0.9F);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Set<UUID> captured = new HashSet<>();
    private UUID casterId;
    private UUID wyvernId;
    private boolean released;

    public HurricaneEntity(EntityType<? extends HurricaneEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
    }

    public void setCaster(Entity caster, UUID sourceWyvernId) {
        casterId = caster == null ? null : caster.getUUID();
        wyvernId = sourceWyvernId;
    }

    @Override protected void defineSynchedData() { }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            spawnClientVfx();
            return;
        }
        if (tickCount >= TELEGRAPH_END && tickCount < ACTIVE_END) {
            applyVortex();
            if (tickCount % 40 == 0) level().playSound(null, this, SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, 0.9F, 0.62F);
        }
        if (tickCount >= ACTIVE_END && !released) releaseCaptured();
        if (tickCount >= LIFETIME) discard();
    }

    private void applyVortex() {
        AABB area = getBoundingBox().inflate(RADIUS, RADIUS * 1.2, RADIUS);
        boolean pulse = tickCount % DAMAGE_INTERVAL == 0;
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, area, this::isValidTarget)) {
            Vec3 offset = position().subtract(target.position());
            double horizontalDistance = Math.sqrt(offset.x * offset.x + offset.z * offset.z);
            if (horizontalDistance > RADIUS || Math.abs(offset.y) > RADIUS * 1.2) continue;
            captured.add(target.getUUID());
            Vec3 inward = horizontalDistance < 0.35 ? Vec3.ZERO : new Vec3(offset.x, 0.0, offset.z).normalize();
            Vec3 tangent = new Vec3(-inward.z, 0.0, inward.x);
            double proximity = 1.0 - Math.min(1.0, horizontalDistance / RADIUS);
            // stronger suction from further out + a much faster spin, so captives visibly orbit
            double lift = target.getY() < getY() + 9.0 ? 0.05 + proximity * 0.035 : -0.02;
            Vec3 impulse = inward.scale(0.075 + proximity * 0.06)
                .add(tangent.scale(0.16 + proximity * 0.1))
                .add(0.0, lift, 0.0);
            Vec3 current = target.getDeltaMovement();
            target.setDeltaMovement(current.scale(0.76).add(impulse));
            target.hurtMarked = true;
            target.fallDistance = 0.0F;
            if (pulse && horizontalDistance <= INNER_RADIUS) {
                DamageSource source = damageSources().indirectMagic(this, getOwner());
                target.hurt(source, EYE_DAMAGE);
                // a shove so the eye keeps throwing captives around instead of letting them stand
                target.push(tangent.x * 0.3, 0.12, tangent.z * 0.3);
            }
        }
    }

    private Entity getOwner() {
        return casterId == null ? null : level().getPlayerByUUID(casterId);
    }

    private void releaseCaptured() {
        released = true;
        if (!(level() instanceof ServerLevel server)) return;
        level().playSound(null, this, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.3F, 0.72F);
        for (UUID targetId : Set.copyOf(captured)) {
            Entity resolved = server.getEntity(targetId);
            if (!(resolved instanceof LivingEntity target) || !target.isAlive()) continue;
            Vec3 outward = target.position().subtract(position());
            outward = new Vec3(outward.x, 0.0, outward.z);
            if (outward.lengthSqr() < 0.01) {
                double angle = (target.getId() * 2.399963229728653) % (Math.PI * 2.0);
                outward = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
            } else outward = outward.normalize();
            target.hurt(damageSources().indirectMagic(this, null), RELEASE_DAMAGE);
            target.setDeltaMovement(target.getDeltaMovement().scale(0.35).add(outward.scale(1.55)).add(0.0, 0.62, 0.0));
            target.hurtMarked = true;
            target.fallDistance = 0.0F;
        }
    }

    private boolean isValidTarget(LivingEntity target) {
        if (!target.isAlive() || target.isSpectator()) return false;
        UUID id = target.getUUID();
        if (id.equals(casterId) || id.equals(wyvernId)) return false;
        Entity caster = casterId == null ? null : level().getPlayerByUUID(casterId);
        if (caster != null && (target.isAlliedTo(caster) || caster.isAlliedTo(target))) return false;
        if (target.getVehicle() != null && (target.getVehicle().getUUID().equals(wyvernId)
            || target.getVehicle().getUUID().equals(casterId))) return false;
        UUID ownerId = ownerUuid(target);
        return casterId == null || ownerId == null || !casterId.equals(ownerId);
    }

    private static UUID ownerUuid(Entity entity) {
        if (entity instanceof OwnableEntity ownable) return ownable.getOwnerUUID();
        if (entity instanceof TamableAnimal tamable) return tamable.getOwnerUUID();
        return null;
    }

    private void spawnClientVfx() {
        if (level().getNearestPlayer(this, 96.0) == null) return;
        boolean telegraph = tickCount < TELEGRAPH_END;
        boolean active = tickCount >= TELEGRAPH_END && tickCount < ACTIVE_END;
        int count = telegraph ? 4 : active ? 9 : 5;
        double grow = telegraph ? 0.2 + 0.8 * tickCount / TELEGRAPH_END : 1.0;
        double phase = tickCount * 0.42;
        double top = 56.0;
        for (int i = 0; i < count; i++) {
            // spiral streaks that follow the widening funnel silhouette
            double t = random.nextDouble();
            double height = t * top * grow;
            double radius = (1.6 + t * 9.4) * grow;
            double angle = phase + i * 2.399963229728653 + t * 5.0;
            double x = getX() + Math.cos(angle) * radius;
            double z = getZ() + Math.sin(angle) * radius;
            DustParticleOptions dust = (i & 3) == 0 ? GOLD : CYAN;
            level().addParticle(dust, x, getY() + height, z,
                -Math.sin(angle) * 0.28, 0.06 + random.nextDouble() * 0.05, Math.cos(angle) * 0.28);
            if ((i & 2) == 0) {
                level().addParticle(ParticleTypes.CLOUD, getX() + Math.cos(angle + 0.7) * radius,
                    getY() + height, getZ() + Math.sin(angle + 0.7) * radius,
                    -Math.sin(angle) * 0.16, 0.03, Math.cos(angle) * 0.16);
            }
        }
        // churning cloud deck at the top
        for (int i = 0; i < (active ? 3 : 1); i++) {
            double angle = phase * 0.4 + i * Math.PI * 2.0 / 3.0;
            double radius = (7.0 + random.nextDouble() * 4.5) * grow;
            level().addParticle(ParticleTypes.CLOUD, getX() + Math.cos(angle) * radius,
                getY() + top * grow - 2.0 + random.nextDouble() * 4.0, getZ() + Math.sin(angle) * radius,
                -Math.sin(angle) * 0.1, 0.02, Math.cos(angle) * 0.1);
        }
        // dust skirt whipping around the base
        for (int i = 0; i < (active ? 3 : 1); i++) {
            double angle = -phase * 1.6 + i * Math.PI / 2.0;
            double radius = (3.2 + random.nextDouble() * 2.6) * grow;
            level().addParticle(ParticleTypes.CLOUD, getX() + Math.cos(angle) * radius,
                getY() + 0.4, getZ() + Math.sin(angle) * radius,
                -Math.sin(angle) * 0.34, 0.1, Math.cos(angle) * 0.34);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        tickCount = tag.getInt("Age");
        released = tag.getBoolean("Released");
        if (tag.hasUUID("Caster")) casterId = tag.getUUID("Caster");
        if (tag.hasUUID("Wyvern")) wyvernId = tag.getUUID("Wyvern");
        captured.clear();
        ListTag list = tag.getList("Captured", Tag.TAG_INT_ARRAY);
        for (Tag entry : list) captured.add(net.minecraft.nbt.NbtUtils.loadUUID(entry));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Age", tickCount);
        tag.putBoolean("Released", released);
        if (casterId != null) tag.putUUID("Caster", casterId);
        if (wyvernId != null) tag.putUUID("Wyvern", wyvernId);
        ListTag list = new ListTag();
        for (UUID id : captured) list.add(net.minecraft.nbt.NbtUtils.createUUID(id));
        tag.put("Captured", list);
    }

    @Override public boolean isPickable() { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public boolean hurt(DamageSource source, float amount) { return false; }
    @Override public PushReaction getPistonPushReaction() { return PushReaction.IGNORE; }
    @Override public AABB getBoundingBoxForCulling() { return getBoundingBox().inflate(RADIUS + 2.0); }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "hurricane", 0, state -> {
            state.getController().setAnimation(VORTEX);
            return PlayState.CONTINUE;
        }));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}

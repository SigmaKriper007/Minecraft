package com.opus.fire.entity.projectile;

import com.opus.fire.registry.FireEntities;
import com.opus.fire.registry.FireParticles;
import com.opus.fire.sound.FireSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class FireballProjectile extends Projectile implements GeoAnimatable {
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int lifetime;
    private boolean detonated;

    public FireballProjectile(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public FireballProjectile(Level level, LivingEntity owner, double x, double y, double z) {
        this(FireEntities.FIREBALL, level);
        setOwner(owner);
        shoot(x, y, z, 1.6f, 0.0f);
    }

    @Override protected void defineSynchedData() { }

    @Override
    public boolean canHitEntity(Entity entity) {
        Entity owner = getOwner();
        return entity != owner && (owner == null || !owner.isAlliedTo(entity)) && super.canHitEntity(entity);
    }

    @Override
    public void tick() {
        super.tick();
        if (++lifetime > 200) { detonate(); return; }
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS) {
            onHit(hit);
            if (isRemoved()) return;
        }
        Vec3 velocity = getDeltaMovement();
        setPos(getX() + velocity.x, getY() + velocity.y, getZ() + velocity.z);
        if (level().isClientSide) {
            int density = tickCount % 2 == 0 ? 2 : 1;
            for (int i = 0; i < density; i++) {
                level().addParticle(FireParticles.EMBER, getX() - velocity.x * 0.45, getY() - velocity.y * 0.45,
                    getZ() - velocity.z * 0.45, -velocity.x * 0.03, -velocity.y * 0.03, -velocity.z * 0.03);
            }
        }
    }

    @Override
    protected void onHit(HitResult hit) {
        if (level().isClientSide || detonated) return;
        if (hit instanceof EntityHitResult entityHit) {
            Entity target = entityHit.getEntity();
            DamageSource source = getOwner() instanceof LivingEntity owner
                ? damageSources().mobProjectile(this, owner) : damageSources().magic();
            target.hurt(source, 8.0f);
            target.setSecondsOnFire(8);
        }
        detonate();
    }

    private void detonate() {
        if (detonated) return;
        detonated = true;
        if (level() instanceof ServerLevel server) {
            for (LivingEntity target : server.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(6.0),
                entity -> entity.isAlive() && (getOwner() == null || (entity != getOwner() && !getOwner().isAlliedTo(entity))))) target.setSecondsOnFire(8);
            server.sendParticles(FireParticles.EMBER, getX(), getY(), getZ(), 42, 1.2, 1.2, 1.2, 0.28);
            server.sendParticles(net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE, getX(), getY(), getZ(), 18, 1.0, 0.7, 1.0, 0.08);
            ProtectedFireExplosion.explode(server, this, getOwner(), 6.0f);
            server.playSound(null, this, FireSounds.FIRE_EXPLODE, SoundSource.HOSTILE, 2.0f, 0.88f);
        }
        discard();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "flight", 0, state -> {
            state.getController().setAnimation(FLY); return PlayState.CONTINUE;
        }));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
    @Override public double getTick(Object ignored) { return tickCount; }
    @Override protected void readAdditionalSaveData(CompoundTag tag) { lifetime = tag.getInt("Life"); }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { tag.putInt("Life", lifetime); }
}

final class ProtectedFireExplosion {
    private ProtectedFireExplosion() { }

    static void explode(ServerLevel level, Entity source, Entity owner, float strength) {
        List<Entity> protectedEntities = level.getEntities(source, source.getBoundingBox().inflate(strength * 2.0),
            entity -> owner != null && (entity == owner || owner.isAlliedTo(entity)));
        if (owner != null && !protectedEntities.contains(owner)) protectedEntities.add(owner);
        Map<Entity, Boolean> invulnerability = new IdentityHashMap<>();
        Map<Entity, Vec3> motion = new IdentityHashMap<>();
        for (Entity entity : protectedEntities) {
            invulnerability.put(entity, entity.isInvulnerable());
            motion.put(entity, entity.getDeltaMovement());
            entity.setInvulnerable(true);
        }
        try {
            level.explode(source, source.getX(), source.getY(), source.getZ(), strength, true, Level.ExplosionInteraction.BLOCK);
        } finally {
            for (Entity entity : protectedEntities) {
                entity.setInvulnerable(invulnerability.get(entity));
                entity.setDeltaMovement(motion.get(entity));
                entity.hurtMarked = true;
            }
        }
    }
}

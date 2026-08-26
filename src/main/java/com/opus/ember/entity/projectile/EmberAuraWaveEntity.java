package com.opus.ember.entity.projectile;

import com.opus.ember.registry.EmberEntities;
import com.opus.ember.sound.EmberSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** Expanding three-dimensional pulse emitted by the Flame Demon of the Ember line. */
public class EmberAuraWaveEntity extends Entity implements GeoAnimatable {
    public static final int LIFETIME_TICKS = 24;
    public static final float MAX_RADIUS = 9.0f;
    public static final float DAMAGE = 6.0f;
    public static final double KNOCKBACK = 1.8;

    private static final RawAnimation PULSE_ANIM = RawAnimation.begin().thenLoop("pulse");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Set<UUID> damaged = new HashSet<>();
    private LivingEntity owner;

    public EmberAuraWaveEntity(EntityType<? extends Entity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public EmberAuraWaveEntity(Level level, LivingEntity owner) {
        super(EmberEntities.EMBER_AURA_WAVE, level);
        this.setNoGravity(true);
        this.owner = owner;
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    public float currentRadius() {
        float t = Math.min(this.tickCount / (float) LIFETIME_TICKS, 1.0f);
        return MAX_RADIUS * (t * t * (3.0f - 2.0f * t));
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.tickCount % 2 == 0) {
                float r = Math.max(currentRadius(), 1.0f);
                for (int i = 0; i < 12; i++) {
                    double a = this.random.nextDouble() * Math.PI * 2;
                    double rad = r * (0.9 + 0.1 * this.random.nextDouble());
                    this.level().addParticle(ParticleTypes.FLAME,
                        this.getX() + Math.cos(a) * rad,
                        this.getY() + 0.3 + this.random.nextDouble(),
                        this.getZ() + Math.sin(a) * rad,
                        Math.cos(a) * 0.1, 0.1, Math.sin(a) * 0.1);
                }
            }
            return;
        }
        if (this.tickCount == 1) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                EmberSounds.FLAME_DEMON_AURA, SoundSource.HOSTILE, 1.5f, 1.0f);
        }
        this.dealDamage();
        if (this.tickCount >= LIFETIME_TICKS) {
            this.discard();
        }
    }

    private void dealDamage() {
        double r = Math.max(currentRadius(), 0.5);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
            this.getBoundingBox().inflate(r + 2.0, 2.0, r + 2.0),
            e -> e.isAlive() && !e.isSpectator()
                && (this.owner == null || (!e.getUUID().equals(this.owner.getUUID())
                    && !this.owner.isAlliedTo(e))));
        DamageSource src = this.owner != null
            ? this.level().damageSources().mobAttack(this.owner)
            : this.level().damageSources().generic();
        for (LivingEntity target : targets) {
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > r || !this.damaged.add(target.getUUID())) {
                continue;
            }
            target.hurt(src, DAMAGE);
            target.setSecondsOnFire(6);
            if (dist > 0.01) {
                target.push(dx / dist * KNOCKBACK, 0.5, dz / dist * KNOCKBACK);
            }
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "aura_ring", 0, state -> {
            state.getController().setAnimation(PULSE_ANIM);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public double getTick(Object object) {
        return this.tickCount;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}

package com.opus.item;

import com.opus.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class CombatEffects {
    private CombatEffects() {}

    public static void shockwave(LivingEntity source, double radius, float damage, double knockback, boolean launch) {
        Level level = source.level();
        if (level.isClientSide) return;
        var entities = level.getEntities(source, source.getBoundingBox().inflate(radius), e -> e instanceof LivingEntity living && living != source && !source.isAlliedTo(e));
        for (Entity entity : entities) {
            LivingEntity target = (LivingEntity) entity;
            double dx = target.getX() - source.getX();
            double dz = target.getZ() - source.getZ();
            double len = Math.max(0.1, Math.sqrt(dx * dx + dz * dz));
            target.knockback(knockback, dx / len, dz / len);
            if (launch) target.setDeltaMovement(target.getDeltaMovement().x, Math.max(target.getDeltaMovement().y, 0.8), target.getDeltaMovement().z);
            target.hurt(level.damageSources().mobAttack(source), damage);
        }
        level.playSound(null, source.blockPosition(), ModSounds.SHOCKWAVE, net.minecraft.sounds.SoundSource.PLAYERS, 1.5f, 0.7f);
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.EXPLOSION_EMITTER, source.getX(), source.getY() + 0.2, source.getZ(), 1, 0, 0, 0, 0);
            server.sendParticles(ParticleTypes.POOF, source.getX(), source.getY() + 0.4, source.getZ(), 80, radius * .35, .25, radius * .35, .12);
            server.sendParticles(ParticleTypes.END_ROD, source.getX(), source.getY() + .5, source.getZ(), 35, radius * .25, .15, radius * .25, .08);
        }
    }
}

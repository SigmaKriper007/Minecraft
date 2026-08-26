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
        shockwave(source, radius, radius, damage, knockback, launch);
    }

    /**
     * Ударная волна с раздельными радиусом по XZ и высотой по Y (задача 15.2):
     * у SLAM босса ширина ×2 и высота ×4 прежних значений, чтобы волна
     * доставала высоко летящих/стоящих на пьедесталах целей.
     */
    public static void shockwave(LivingEntity source, double radiusXZ, double heightY,
                                 float damage, double knockback, boolean launch) {
        Level level = source.level();
        if (level.isClientSide) return;
        var entities = level.getEntities(source, source.getBoundingBox().inflate(radiusXZ, heightY, radiusXZ), e -> e instanceof LivingEntity living && living != source && !source.isAlliedTo(e));
        for (Entity entity : entities) {
            LivingEntity target = (LivingEntity) entity;
            double dx = target.getX() - source.getX();
            double dz = target.getZ() - source.getZ();
            double len = Math.max(0.1, Math.sqrt(dx * dx + dz * dz));
            target.knockback(knockback, dx / len, dz / len);
            if (launch) target.setDeltaMovement(target.getDeltaMovement().x, Math.max(target.getDeltaMovement().y, 0.8), target.getDeltaMovement().z);
            target.hurt(level.damageSources().mobAttack(source), damage);
        }
        level.playSound(null, source.blockPosition(), ModSounds.SHOCKWAVE, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 0.7f);
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.EXPLOSION_EMITTER, source.getX(), source.getY() + 0.2, source.getZ(), 1, 0, 0, 0, 0);
            server.sendParticles(ParticleTypes.POOF, source.getX(), source.getY() + 0.4, source.getZ(), 80, radiusXZ * .35, heightY * .5, radiusXZ * .35, .12);
            server.sendParticles(ParticleTypes.END_ROD, source.getX(), source.getY() + .5, source.getZ(), 35, radiusXZ * .25, heightY * .4, radiusXZ * .25, .08);
        }
    }
}

package com.opus.ember.entity.projectile;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

final class ProtectedEmberExplosion {
    private ProtectedEmberExplosion() { }

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

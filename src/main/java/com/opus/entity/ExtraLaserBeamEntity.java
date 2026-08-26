package com.opus.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

/**
 * EXO-6 "Extra Laser": a heavy blaster beam scaled up to 2.5x diameter. On top
 * of burning everything in the wider beam, every solid block it touches is
 * blown apart with a TNT-strength explosion — the beam carves terrain as it
 * travels.
 */
public class ExtraLaserBeamEntity extends BlasterBeamEntity {

    /** 2.5x the heavy blaster's 0.5-block diameter. */
    public static final float DIAMETER_BLOCKS = 1.25F;
    public static final float BASE_DAMAGE = 18.0F;
    public static final float EXPLOSION_POWER = 2.0F;

    public ExtraLaserBeamEntity(EntityType<? extends BlasterBeamEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        // Base tick handles lifetime discard, the client early return, muzzle
        // particles and the standard (narrow) damage pulse.
        super.tick();
        if (this.level().isClientSide || !this.isAlive()) {
            return;
        }
        if (this.tickCount >= DAMAGE_START_TICK && this.tickCount % DAMAGE_INTERVAL_TICKS == 0) {
            this.damageAndExplode();
        }
    }

    private void damageAndExplode() {
        Vec3 start = this.position();
        Vec3 dir = this.getBeamDirection();
        Vec3 end = start.add(dir.scale(this.getBeamLength()));
        double radius = DIAMETER_BLOCKS / 2.0D + 0.3D;
        AABB beam = new AABB(start, end).inflate(radius);

        UUID shooter = this.getShooterUUID();
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, beam,
                entity -> entity.isAlive() && !entity.isSpectator()
                        && (shooter == null || !entity.getUUID().equals(shooter)));
        Player player = shooter != null ? this.level().getPlayerByUUID(shooter) : null;
        DamageSource source = player != null
                ? this.level().damageSources().playerAttack(player)
                : this.level().damageSources().generic();
        for (LivingEntity target : targets) {
            target.hurt(source, BASE_DAMAGE);
        }

        if (this.level() instanceof ServerLevel server) {
            this.explodePath(server, start, end, radius);
        }
    }

    /**
     * Blows up every solid block touched by the wide beam, across the full beam
     * volume (not just the centre line). Blocks are found by walking the beam's
     * bounding box and testing each block centre against the beam segment.
     */
    private void explodePath(ServerLevel server, Vec3 start, Vec3 end, double radius) {
        double reach = radius + 0.5D;
        int minX = Mth.floor(Math.min(start.x, end.x) - reach);
        int maxX = Mth.floor(Math.max(start.x, end.x) + reach);
        int minY = Mth.floor(Math.min(start.y, end.y) - reach);
        int maxY = Mth.floor(Math.max(start.y, end.y) + reach);
        int minZ = Mth.floor(Math.min(start.z, end.z) - reach);
        int maxZ = Mth.floor(Math.max(start.z, end.z) + reach);
        double reachSq = reach * reach;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = server.getBlockState(pos);
                    if (state.isAir()) {
                        continue;
                    }
                    Vec3 centre = new Vec3(x + 0.5D, y + 0.5D, z + 0.5D);
                    if (distanceSqToSegment(centre, start, end) > reachSq) {
                        continue;
                    }
                    server.explode(null, centre.x, centre.y, centre.z,
                            EXPLOSION_POWER, false, Level.ExplosionInteraction.TNT);
                }
            }
        }
    }

    private static double distanceSqToSegment(Vec3 p, Vec3 a, Vec3 b) {
        Vec3 ab = b.subtract(a);
        double lenSq = ab.lengthSqr();
        double t = lenSq <= 1.0E-6D ? 0.0D : p.subtract(a).dot(ab) / lenSq;
        t = Mth.clamp(t, 0.0D, 1.0D);
        Vec3 proj = a.add(ab.scale(t));
        return p.distanceToSqr(proj);
    }
}

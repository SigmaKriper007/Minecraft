package com.opus.paradise.world;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.registry.ParadiseBlocks;
import com.opus.paradise.registry.ParadiseEntities;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

/** Guarantees paradise wildlife spawns on the floating sky island (where the battle angel
 *  awakens): whenever a player stands on the island, wildlife trickles in around them. */
public final class ParadiseIslandSpawner {
    private static final int INTERVAL_TICKS = 100;
    private static final int MIN_RANGE = 16;
    private static final int MAX_RANGE = 42;
    private static final double CAP_RANGE = 64.0;
    private static final int MAX_SUNFINCH = 8;
    private static final int MAX_GRAZER = 5;
    private static final int MAX_WYVERN = 3;

    private ParadiseIslandSpawner() { }

    public static void init() {
        ServerTickEvents.END_WORLD_TICK.register(ParadiseIslandSpawner::tick);
    }

    private static void tick(net.minecraft.world.level.Level level) {
        if (!(level instanceof ServerLevel server) || server.getGameTime() % INTERVAL_TICKS != 0) return;
        for (ServerPlayer player : server.players()) {
            if (player.isSpectator() || player.blockPosition().getY() < ParadiseLine.SKY_ISLAND_MIN_Y) continue;
            if (!server.getBlockState(player.blockPosition().below()).is(ParadiseBlocks.PARADISE_GRASS)) continue;
            trySpawnNear(server, player.getRandom(), player.blockPosition());
        }
    }

    private static void trySpawnNear(ServerLevel server, RandomSource random, BlockPos center) {
        int roll = random.nextInt(9);
        if (roll < 5) {
            if (countAround(server, center, ParadiseEntities.SUNFINCH) >= MAX_SUNFINCH) return;
            spawn(server, random, center, ParadiseEntities.SUNFINCH);
        } else if (roll < 8) {
            if (countAround(server, center, ParadiseEntities.CLOUD_GRAZER) >= MAX_GRAZER) return;
            spawn(server, random, center, ParadiseEntities.CLOUD_GRAZER);
        } else {
            if (random.nextInt(3) != 0) return;
            if (countAround(server, center, ParadiseEntities.PARADISE_WYVERN) >= MAX_WYVERN) return;
            spawn(server, random, center, ParadiseEntities.PARADISE_WYVERN);
        }
    }

    private static int countAround(ServerLevel server, BlockPos center, EntityType<?> type) {
        AABB area = new AABB(center).inflate(CAP_RANGE, 24, CAP_RANGE);
        return server.getEntitiesOfClass(type.getBaseClass(), area, e -> e.isAlive()).size();
    }

    private static void spawn(ServerLevel server, RandomSource random, BlockPos center, EntityType<? extends Mob> type) {
        for (int attempt = 0; attempt < 6; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = MIN_RANGE + random.nextDouble() * (MAX_RANGE - MIN_RANGE);
            int x = center.getX() + Mth.floor(Math.cos(angle) * distance);
            int z = center.getZ() + Mth.floor(Math.sin(angle) * distance);
            int y = server.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos pos = new BlockPos(x, y, z);
            if (y < ParadiseLine.SKY_ISLAND_MIN_Y) continue;
            if (!server.getBlockState(pos.below()).is(ParadiseBlocks.PARADISE_GRASS)) continue;
            if (!server.getBlockState(pos).getCollisionShape(server, pos).isEmpty()) continue;
            if (!server.getBlockState(pos.above()).getCollisionShape(server, pos.above()).isEmpty()) continue;
            if (server.getRawBrightness(pos, 0) <= 8) continue;
            Mob mob = type.create(server);
            if (mob == null) return;
            mob.moveTo(x + 0.5, y, z + 0.5, random.nextFloat() * 360.0F, 0.0F);
            mob.finalizeSpawn(server, server.getCurrentDifficultyAt(pos), MobSpawnType.NATURAL, null, null);
            server.addFreshEntity(mob);
            return;
        }
    }
}

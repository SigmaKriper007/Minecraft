package com.opus.client;

import com.opus.blockentity.AltarHeartBlockEntity;
import com.opus.blockentity.ModBlockEntities;
import com.opus.sound.ModSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * Клиентский эмбиент «Сердца Алтаря» (задача 2026-08-22).
 *
 * Серверный блок-entity пустой — это просто «маяк». Здесь, на клиенте, раз в
 * секунду сканируются загруженные чанки вокруг игрока (LevelChunk.getBlockEntities)
 * и, если рядом есть сердце алтаря, проигрывается зацикленный гул
 * altar_heart_loop с громкостью, зависящей от дистанции. Выход из радиуса
 * 64 блока — звук гаснет.
 */
@Environment(EnvType.CLIENT)
public final class AltarHeartAmbience {

    private static final double RADIUS = 64.0D;
    private static final long SCAN_INTERVAL = 20L;
    private static final RandomSource SOUND_RANDOM = RandomSource.create(42L);

    private static SimpleSoundInstance loop = null;
    private static long nextScanAt = 0L;

    private AltarHeartAmbience() {
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Minecraft mc = Minecraft.getInstance();
            ClientLevel level = mc.level;
            if (level == null || mc.player == null) {
                stopLoop();
                return;
            }

            long gameTime = level.getGameTime();
            if (gameTime < nextScanAt) {
                return;
            }
            nextScanAt = gameTime + SCAN_INTERVAL;

            BlockPos nearest = findNearestHeart(level, mc.player.blockPosition());
            boolean nearby = nearest != null && mc.player.distanceToSqr(nearest.getX() + 0.5,
                    nearest.getY() + 0.5, nearest.getZ() + 0.5) <= RADIUS * RADIUS;

            if (nearby) {
                startLoop(nearest);
            } else {
                stopLoop();
            }
        });
    }

    private static BlockPos findNearestHeart(ClientLevel level, BlockPos playerPos) {
        int radiusChunks = (int) Math.ceil(RADIUS / 16.0D) + 1;
        int pcx = playerPos.getX() >> 4;
        int pcz = playerPos.getZ() >> 4;
        BlockPos nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (int cx = pcx - radiusChunks; cx <= pcx + radiusChunks; cx++) {
            for (int cz = pcz - radiusChunks; cz <= pcz + radiusChunks; cz++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(cx, cz);
                if (chunk == null) {
                    continue;
                }
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (be.getType() == ModBlockEntities.ALTAR_HEART) {
                        BlockPos p = be.getBlockPos();
                        double d = playerPos.distSqr(p);
                        if (d < nearestDist) {
                            nearestDist = d;
                            nearest = p;
                        }
                    }
                }
            }
        }
        return nearest;
    }

    private static void startLoop(BlockPos pos) {
        Minecraft mc = Minecraft.getInstance();
        if (loop != null && mc.getSoundManager().isActive(loop)) {
            return;
        }
        stopLoop();
        loop = new SimpleSoundInstance(
                ModSounds.ALTAR_HEART_LOOP.getLocation(),
                SoundSource.AMBIENT,
                0.9F, 1.0F,
                SOUND_RANDOM,
                true, 0,
                SoundInstance.Attenuation.NONE,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                false);
        mc.getSoundManager().play(loop);
    }

    private static void stopLoop() {
        if (loop != null) {
            Minecraft.getInstance().getSoundManager().stop(loop);
            loop = null;
        }
    }
}

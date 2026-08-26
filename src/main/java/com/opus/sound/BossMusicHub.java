package com.opus.sound;

import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

/**
 * Shared non-positional boss-music lifecycle, mirroring the Omega encounter
 * pattern: play a streaming music track to every player inside a radius,
 * interrupt the previous instance first, and stop the track on cleanup.
 */
public final class BossMusicHub {
    private static final double DEFAULT_RANGE = 4096.0D; // 64 blocks squared

    private BossMusicHub() { }

    /** Start the boss track for every player within {@code rangeBlocks} of the entity. */
    public static void start(ServerLevel level, Entity source, SoundEvent track, double rangeBlocks) {
        if (level == null || source == null || track == null) return;
        double rangeSq = rangeBlocks * rangeBlocks;
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(source) <= rangeSq) {
                player.connection.send(new ClientboundStopSoundPacket(track.getLocation(), SoundSource.RECORDS));
                player.playNotifySound(track, SoundSource.RECORDS, 1.0F, 1.0F);
            }
        }
    }

    /** Start with the default 64-block radius. */
    public static void start(ServerLevel level, Entity source, SoundEvent track) {
        start(level, source, track, 64.0D);
    }

    /** Start the boss track for every player on the level regardless of distance. */
    public static void startForAll(ServerLevel level, SoundEvent track) {
        if (level == null || track == null) return;
        for (ServerPlayer player : level.players()) {
            player.connection.send(new ClientboundStopSoundPacket(track.getLocation(), SoundSource.RECORDS));
            player.playNotifySound(track, SoundSource.RECORDS, 1.0F, 1.0F);
        }
    }

    /** Stop the boss track for every player on the level. */
    public static void stop(ServerLevel level, SoundEvent track) {
        if (level == null || track == null) return;
        for (ServerPlayer player : level.players()) {
            player.connection.send(new ClientboundStopSoundPacket(track.getLocation(), SoundSource.RECORDS));
        }
    }
}
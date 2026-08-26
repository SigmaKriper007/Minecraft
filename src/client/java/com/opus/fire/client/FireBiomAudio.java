package com.opus.fire.client;

import com.opus.fire.registry.FireBlocks;
import com.opus.fire.sound.FireSounds;
import com.opus.fire.block.FirePortalBlock;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

/**
 * Атмосфера «Тюрьмы Демона» (клиент).
 * - фоновый луп firebiom_sound в измерении (тихо, 0.35, «в голове» у игрока);
 * - слабый луп portal_sound у блока «Breakthrough In Space».
 * Боевой трек Diablo и его реплики управляются на сервере (как у Омеги).
 */
public final class FireBiomAudio {
    private static LoopSound ambient;
    private static LoopSound portal;
    private static LoopSound diablo;
    private static boolean diabloRequested;
    private static BlockPos portalPos;
    private static int scanTimer;
    private FireBiomAudio() { }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(FireBiomAudio::tick);
    }

    private static void tick(Minecraft client) {
        if (client.player == null || client.level == null) { stopAll(client); return; }
        boolean inRealm = client.player.level().dimension().equals(FirePortalBlock.FIRE_REALM);

        // Фон измерения — всегда в измерении, «в голове» (NoAttenuation).
        if (inRealm && !diabloRequested) {
            if (ambient == null) {
                ambient = new LoopSound(FireSounds.FIREBIOM_SOUND, SoundSource.AMBIENT,
                        0.35f, 1.0f, client.player.getX(), client.player.getY(), client.player.getZ(),
                        SoundInstance.Attenuation.NONE);
                client.getSoundManager().play(ambient);
            }
        } else if (ambient != null) {
            client.getSoundManager().stop(ambient);
            ambient = null;
        }

        if (inRealm && diabloRequested) {
            if (diablo == null) {
                diablo = new LoopSound(FireSounds.DIABLO_THEME, SoundSource.MUSIC,
                        1.0F, 1.0F, 0.0D, 0.0D, 0.0D,
                        SoundInstance.Attenuation.NONE, true);
                client.getSoundManager().play(diablo);
            }
        } else if (diablo != null) {
            client.getSoundManager().stop(diablo);
            diablo = null;
        }
        if (!inRealm) {
            diabloRequested = false;
        }

        // Слабый гул портала (сканируем раз в 15 тиков вокруг игрока).
        if (++scanTimer >= 15) { scanTimer = 0; portalPos = findPortal(client); }
        if (portalPos != null) {
            if (portal == null) {
                Vec3 c = Vec3.atCenterOf(portalPos);
                portal = new LoopSound(FireSounds.PORTAL_SOUND, SoundSource.AMBIENT,
                        0.38f, 1.0f, c.x, c.y, c.z, SoundInstance.Attenuation.LINEAR);
                client.getSoundManager().play(portal);
            } else {
                Vec3 c = Vec3.atCenterOf(portalPos);
                portal.setPos(c.x, c.y, c.z);
            }
        } else if (portal != null) {
            client.getSoundManager().stop(portal);
            portal = null;
        }
    }

    private static BlockPos findPortal(Minecraft client) {
        BlockPos p = client.player.blockPosition();
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (int dx = -10; dx <= 10; dx++) {
            for (int dz = -10; dz <= 10; dz++) {
                for (int dy = -3; dy <= 3; dy++) {
                    BlockPos pos = p.offset(dx, dy, dz);
                    if (client.level.getBlockState(pos).is(FireBlocks.FIRE_PORTAL)) {
                        double d = pos.distSqr(p);
                        if (d < bestDist) { bestDist = d; best = pos.immutable(); }
                    }
                }
            }
        }
        return best;
    }

    private static void stopAll(Minecraft client) {
        if (ambient != null) { client.getSoundManager().stop(ambient); ambient = null; }
        if (portal != null) { client.getSoundManager().stop(portal); portal = null; }
        if (diablo != null) { client.getSoundManager().stop(diablo); diablo = null; }
        diabloRequested = false;
    }

    public static void setDiabloBattle(boolean active) {
        diabloRequested = active;
    }

    /** Простой зацикленный звук с позицией. */
    private static final class LoopSound extends AbstractSoundInstance {
        private final SoundEvent event;
        private final float vol;
        private final float pit;
        private final SoundInstance.Attenuation attenuation;
        private final boolean relative;
        private double px, py, pz;

        LoopSound(SoundEvent event, SoundSource source, float vol, float pitch, double x, double y, double z,
                  SoundInstance.Attenuation att) {
            this(event, source, vol, pitch, x, y, z, att, false);
        }

        LoopSound(SoundEvent event, SoundSource source, float vol, float pitch, double x, double y, double z,
                  SoundInstance.Attenuation att, boolean relative) {
            super(event.getLocation(), source, RandomSource.create());
            this.event = event; this.vol = vol; this.pit = pitch;
            this.px = x; this.py = y; this.pz = z;
            this.attenuation = att;
            this.relative = relative;
        }
        void setPos(double x, double y, double z) { this.px = x; this.py = y; this.pz = z; }

        @Override public float getVolume() { return vol; }
        @Override public float getPitch() { return pit; }
        @Override public double getX() { return px; }
        @Override public double getY() { return py; }
        @Override public double getZ() { return pz; }
        @Override public boolean isLooping() { return true; }
        @Override public boolean isRelative() { return relative; }
        @Override public SoundInstance.Attenuation getAttenuation() { return attenuation; }
    }
}

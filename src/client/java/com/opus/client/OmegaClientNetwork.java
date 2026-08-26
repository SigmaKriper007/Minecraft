package com.opus.client;

import com.opus.network.ModNetwork;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

/**
 * Приёмник эффектов боя Омеги (задача 13): тряска камеры через ванильный
 * механизм урона (hurtDuration/hurtTime — лёгкий крен камеры), тип FX по
 * коду из ModNetwork.
 */
@Environment(EnvType.CLIENT)
public final class OmegaClientNetwork {

    private OmegaClientNetwork() {
    }

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ModNetwork.OMEGA_FX, (client, handler, buf, sender) -> {
            int fxType = buf.readVarInt();
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            float radius = buf.readFloat();
            client.execute(() -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player == null) return;
                // тряска пропорциональна близости к эпицентру
                double dist = mc.player.distanceToSqr(new Vec3(x, y, z));
                double maxDist = (radius + 60.0F) * (radius + 60.0F);
                float intensity = (float) Math.max(0.0D, 1.0D - dist / maxDist);
                switch (fxType) {
                    case ModNetwork.FX_SHAKE_MINOR -> OmegaCameraShake.trigger(0.5F * intensity, 6);
                    case ModNetwork.FX_SHAKE_MAJOR -> OmegaCameraShake.trigger(0.85F * intensity, 12);
                    case ModNetwork.FX_PHASE_OPEN -> OmegaCameraShake.trigger(0.6F, 8);
                    case ModNetwork.FX_PHASE_ENRAGE -> OmegaCameraShake.trigger(1.0F, 14);
                    case ModNetwork.FX_REQUIEM -> OmegaCameraShake.trigger(0.7F * intensity, 10);
                    case ModNetwork.FX_MOON_START -> CrimsonMoonClient.onBattleStarted();
                    case ModNetwork.FX_MOON_END -> CrimsonMoonClient.onBattleEnded();
                    default -> { }
                }
            });
        });
    }
}

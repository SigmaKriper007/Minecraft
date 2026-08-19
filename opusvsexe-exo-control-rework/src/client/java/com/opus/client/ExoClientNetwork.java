package com.opus.client;

import com.opus.network.ModNetwork;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Applies server issued movement impulses (dash, thrusters, launch).
 * Without this the server would set velocity on an entity whose movement is
 * simulated by the piloting client, and nothing would happen at all.
 */
public final class ExoClientNetwork {

    private ExoClientNetwork() {
    }

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ModNetwork.EXO_IMPULSE, (client, handler, buf, sender) -> {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            client.execute(() -> {
                if (client.player != null && client.player.getVehicle() instanceof ExosuitEntity exo) {
                    exo.setDeltaMovement(x, y, z);
                    exo.hasImpulse = true;
                }
            });
        });
    }
}

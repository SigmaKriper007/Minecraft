package com.opus.fire.client;

import com.opus.fire.client.layer.FireChargeLayer;
import com.opus.fire.network.FireNetwork;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class FireClientNetwork {
    private FireClientNetwork() { }
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(FireNetwork.FIRE_CHARGE_FX, (client, handler, buffer, response) -> {
            int duration = buffer.readVarInt();
            client.execute(() -> FireChargeLayer.startCharge(duration));
        });
        ClientPlayNetworking.registerGlobalReceiver(FireNetwork.DIABLO_AUDIO, (client, handler, buffer, response) -> {
            boolean active = buffer.readBoolean();
            client.execute(() -> FireBiomAudio.setDiabloBattle(active));
        });
    }
}

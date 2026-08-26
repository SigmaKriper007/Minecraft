package com.opus.ember.client;

import com.opus.ember.client.layer.EmberChargeLayer;
import com.opus.ember.network.EmberNetwork;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class EmberClientNetwork {
    private EmberClientNetwork() { }
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(EmberNetwork.EMBER_CHARGE_FX, (client, handler, buffer, response) -> {
            int duration = buffer.readVarInt();
            client.execute(() -> EmberChargeLayer.startCharge(duration));
        });
    }
}

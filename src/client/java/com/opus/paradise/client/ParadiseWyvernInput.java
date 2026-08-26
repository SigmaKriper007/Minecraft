package com.opus.paradise.client;

import com.opus.paradise.entity.ParadiseWyvernEntity;
import com.opus.paradise.network.ParadiseNetwork;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;

public final class ParadiseWyvernInput {
    private static boolean useWasDown;
    private static int lastVehicleId = Integer.MIN_VALUE;

    private ParadiseWyvernInput() { }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || !(client.player.getVehicle() instanceof ParadiseWyvernEntity wyvern)
                || client.screen != null) {
                useWasDown = false;
                lastVehicleId = Integer.MIN_VALUE;
                return;
            }
            boolean newlyMounted = lastVehicleId != wyvern.getId();
            lastVehicleId = wyvern.getId();
            boolean useDown = client.options.keyUse.isDown();
            if (newlyMounted) useWasDown = useDown;

            boolean ascend = client.options.keyJump.isDown();
            boolean descend = client.options.keyShift.isDown();
            wyvern.acceptPilotInput(client.player, client.player.xxa, client.player.zza,
                client.player.getYRot(), client.player.getXRot(), ascend, descend);

            FriendlyByteBuf input = PacketByteBufs.create();
            input.writeFloat(client.player.xxa);
            input.writeFloat(client.player.zza);
            input.writeFloat(client.player.getYRot());
            input.writeFloat(client.player.getXRot());
            input.writeBoolean(ascend);
            input.writeBoolean(descend);
            ClientPlayNetworking.send(ParadiseNetwork.WYVERN_INPUT, input);

            if (useDown && !useWasDown) {
                ClientPlayNetworking.send(ParadiseNetwork.WIND_CORE, PacketByteBufs.empty());
            }
            useWasDown = useDown;
        });
    }
}

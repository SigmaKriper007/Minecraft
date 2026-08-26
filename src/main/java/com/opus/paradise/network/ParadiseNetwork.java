package com.opus.paradise.network;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.entity.ParadiseWyvernEntity;
import com.opus.paradise.item.ParthenonArmorBonus;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public final class ParadiseNetwork {
    public static final ResourceLocation WYVERN_INPUT = ParadiseLine.id("wyvern_input");
    public static final ResourceLocation WIND_CORE = ParadiseLine.id("wyvern_wind_core");
    public static final ResourceLocation REGALIA_HURRICANE = ParadiseLine.id("regalia_hurricane");

    private ParadiseNetwork() { }

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(WYVERN_INPUT, (server, player, handler, buffer, response) -> {
            float strafe = buffer.readFloat();
            float forward = buffer.readFloat();
            float yaw = buffer.readFloat();
            float pitch = buffer.readFloat();
            boolean ascend = buffer.readBoolean();
            boolean descend = buffer.readBoolean();
            server.execute(() -> {
                if (player.getVehicle() instanceof ParadiseWyvernEntity wyvern) {
                    wyvern.acceptPilotInput(player, strafe, forward, yaw, pitch, ascend, descend);
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(WIND_CORE, (server, player, handler, buffer, response) ->
            server.execute(() -> {
                if (player.getVehicle() instanceof ParadiseWyvernEntity wyvern) wyvern.tryFireWindCore(player);
            }));
        ServerPlayNetworking.registerGlobalReceiver(REGALIA_HURRICANE,(server,player,handler,buffer,response)->server.execute(()->ParthenonArmorBonus.tryAimedHurricane(player)));
    }
}

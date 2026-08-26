package com.opus.darkforest.network;

import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.item.DarkForestEquipmentBonus;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public final class DarkForestNetwork {
    public static final ResourceLocation VESTMENT_TELEPORT=DarkForestLine.id("vestment_teleport");
    private DarkForestNetwork(){ }
    public static void init(){ServerPlayNetworking.registerGlobalReceiver(VESTMENT_TELEPORT,(server,player,handler,buffer,response)->server.execute(()->DarkForestEquipmentBonus.tryAimedTeleport(player)));}
}

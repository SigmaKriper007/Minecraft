package com.opus.paradise;

import com.opus.OpusVsExe;
import com.opus.paradise.registry.ParadiseBlocks;
import com.opus.paradise.registry.ParadiseCreativeTab;
import com.opus.paradise.registry.ParadiseItems;
import com.opus.paradise.registry.ParadiseEntities;
import com.opus.paradise.entity.CloudGrazerEntity;
import com.opus.paradise.entity.SunfinchEntity;
import com.opus.paradise.entity.ParadiseWyvernEntity;
import com.opus.paradise.network.ParadiseNetwork;
import com.opus.paradise.entity.AngelBoyEntity;
import com.opus.paradise.registry.ParadiseBlockEntities;
import com.opus.paradise.registry.ParadiseRecipes;
import com.opus.paradise.registry.ParadiseMenus;
import com.opus.paradise.sound.ParadiseSounds;
import com.opus.paradise.item.ParthenonArmorBonus;
import com.opus.paradise.item.ParthenonGearQa;
import com.opus.paradise.blockentity.ParadiseResurrectionQa;
import com.opus.paradise.world.ParadiseSpawns;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.resources.ResourceLocation;

public final class ParadiseLine {
    /** Floating paradise island structure generates at absolute y=148; mobs may only spawn up there. */
    public static final int SKY_ISLAND_MIN_Y = 120;

    private ParadiseLine() { }

    public static void init() {
        ParadiseBlocks.init();
        ParadiseBlockEntities.init();
        ParadiseEntities.init();
        ParadiseItems.init();
        ParadiseRecipes.init();
        ParadiseMenus.init();
        ParadiseCreativeTab.init();
        FabricDefaultAttributeRegistry.register(ParadiseEntities.SUNFINCH, SunfinchEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ParadiseEntities.CLOUD_GRAZER, CloudGrazerEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ParadiseEntities.PARADISE_WYVERN, ParadiseWyvernEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ParadiseEntities.ANGEL_BOY, AngelBoyEntity.createAttributes());
        ParadiseNetwork.init();
        ParadiseSounds.init();
        ParthenonArmorBonus.init();
        ParthenonGearQa.init();
        ParadiseResurrectionQa.init();
        ParadiseSpawns.init();
    }

    public static ResourceLocation id(String path) {
        return OpusVsExe.id(path);
    }
}

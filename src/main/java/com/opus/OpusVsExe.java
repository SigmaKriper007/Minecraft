package com.opus;

import com.opus.blockentity.ModBlockEntities;
import com.opus.entity.haiku.*;
import com.opus.item.OpusArmorBonus;
import com.opus.network.ModNetwork;
import com.opus.registry.ModBlocks;
import com.opus.registry.ModCreativeTab;
import com.opus.registry.ModEntities;
import com.opus.registry.ModItems;
import com.opus.registry.ModMenus;
import com.opus.sound.ModSounds;
import com.opus.structure.ModStructures;
import com.opusvsexe.entity.custom.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpusVsExe implements ModInitializer {
    public static final String MOD_ID = "opusvsexe";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.init();
        ModBlocks.init();
        ModBlockEntities.init();
        ModEntities.init();
        ModCreativeTab.init();
        ModMenus.init();
        ModNetwork.init();
        ModSounds.init();
        OpusArmorBonus.init();
        ModStructures.init();

        FabricDefaultAttributeRegistry.register(ModEntities.HAIKU_1_5, Haiku15Entity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.HAIKU_2, Haiku2Entity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.HAIKU_3, Haiku3Entity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.HAIKU_4, Haiku4Entity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.HAIKU_5, Haiku5Entity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.HAIKU_OMEGA, HaikuOmegaEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.EXO_1_SENTINEL, Exo1Sentinel.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.EXO_2_HUNTER, Exo2Hunter.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.EXO_3_VANGUARD, Exo3Vanguard.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.EXO_4_TITAN, Exo4Titan.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.EXO_5_VENGEANCE, Exo5Vengeance.createAttributes());

        LOGGER.info("OpusVsExe mod initialized - Haiku's world awaits!");
        LOGGER.info("All registries loaded: items, blocks, entities");
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
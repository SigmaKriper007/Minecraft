package com.opus;

import com.opus.registry.ModBlocks;
import com.opus.registry.ModEntities;
import com.opus.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(OpusVsExe.MOD_ID)
public class OpusVsExe implements ModInitializer {
    public static final String MOD_ID = "opusvsexe";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("OpusVsExe mod initialized - Haiku's world awaits!");
        
        // Регистрируем предметы блоков
        ModBlocks.registerBlockItems(ModItems.ITEMS);
        
        // Регистрируем всё
        ModItems.ITEMS.register();
        ModBlocks.BLOCKS.register();
        ModEntities.ENTITIES.register();
        
        LOGGER.info("All registries loaded: items, blocks, entities");
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}

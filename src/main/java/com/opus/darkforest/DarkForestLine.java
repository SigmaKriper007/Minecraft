package com.opus.darkforest;

import com.opus.OpusVsExe;
import com.opus.darkforest.registry.DarkForestBlocks;
import com.opus.darkforest.registry.DarkForestCreativeTab;
import com.opus.darkforest.registry.DarkForestItems;
import com.opus.darkforest.registry.DarkForestEntities;
import com.opus.darkforest.registry.DarkForestBlockEntities;
import com.opus.darkforest.entity.GloomBroodmotherEntity;
import com.opus.darkforest.entity.MoonwingBatEntity;
import com.opus.darkforest.entity.ShadeSpiderlingEntity;
import com.opus.darkforest.entity.MossboundEndermanEntity;
import com.opus.darkforest.entity.MossboundEncounterQa;
import com.opus.darkforest.item.DarkForestEquipmentBonus;
import com.opus.darkforest.item.DarkForestGearQa;
import com.opus.darkforest.blockentity.DarkForestResurrectionQa;
import com.opus.darkforest.network.DarkForestNetwork;
import com.opus.darkforest.entity.DarkForestEcologyQa;
import com.opus.darkforest.world.DarkForestSpawns;
import com.opus.darkforest.world.DarkForestWorldgen;
import com.opus.darkforest.sound.DarkForestSounds;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public final class DarkForestLine {
    public static final ResourceKey<Biome> DARK_FOREST=ResourceKey.create(Registries.BIOME,id("dark_forest"));
    private DarkForestLine(){ }
    public static void init(){
        DarkForestBlocks.init();DarkForestBlockEntities.init();DarkForestEntities.init();DarkForestItems.init();DarkForestCreativeTab.init();
        FabricDefaultAttributeRegistry.register(DarkForestEntities.SHADE_SPIDERLING,ShadeSpiderlingEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(DarkForestEntities.GLOOM_BROODMOTHER,GloomBroodmotherEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(DarkForestEntities.MOONWING_BAT,MoonwingBatEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(DarkForestEntities.MOSSBOUND_ENDERMAN,MossboundEndermanEntity.createAttributes());
        DarkForestNetwork.init();DarkForestEquipmentBonus.init();DarkForestSpawns.init();DarkForestWorldgen.init();DarkForestEcologyQa.init();MossboundEncounterQa.init();DarkForestGearQa.init();DarkForestResurrectionQa.init();DarkForestSounds.init();
    }
    public static ResourceLocation id(String path){return OpusVsExe.id(path);}
}

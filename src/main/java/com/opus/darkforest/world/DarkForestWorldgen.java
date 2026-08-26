package com.opus.darkforest.world;

import com.opus.darkforest.DarkForestLine;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class DarkForestWorldgen {
    public static final Feature<NoneFeatureConfiguration> FOREST_FLOOR=Registry.register(BuiltInRegistries.FEATURE,DarkForestLine.id("dark_forest_floor"),new DarkForestFeature(NoneFeatureConfiguration.CODEC));
    public static final ResourceKey<ConfiguredFeature<?,?>> FOREST_FLOOR_CONFIGURED=ResourceKey.create(Registries.CONFIGURED_FEATURE,DarkForestLine.id("dark_forest_floor"));
    public static final ResourceKey<PlacedFeature> FOREST_FLOOR_PLACED=ResourceKey.create(Registries.PLACED_FEATURE,DarkForestLine.id("dark_forest_floor"));
    private DarkForestWorldgen(){ }
    public static void init(){BiomeModifications.addFeature(BiomeSelectors.includeByKey(DarkForestLine.DARK_FOREST),GenerationStep.Decoration.VEGETAL_DECORATION,FOREST_FLOOR_PLACED);DarkForestWorldgenQa.init();}
}

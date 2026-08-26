package com.opus.fire.world;

import com.opus.fire.FireLine;
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

public final class FireWorldgen {
    public static final Feature<NoneFeatureConfiguration> BREAKTHROUGH = Registry.register(
        BuiltInRegistries.FEATURE, FireLine.id("breakthrough_in_space"),
        new BreakthroughFeature(NoneFeatureConfiguration.CODEC));

    public static final ResourceKey<ConfiguredFeature<?, ?>> BREAKTHROUGH_CONFIGURED = ResourceKey.create(
        Registries.CONFIGURED_FEATURE, FireLine.id("breakthrough_in_space"));
    public static final ResourceKey<PlacedFeature> BREAKTHROUGH_PLACED = ResourceKey.create(
        Registries.PLACED_FEATURE, FireLine.id("breakthrough_in_space"));

    private FireWorldgen() { }

    public static void init() {
        BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(),
            GenerationStep.Decoration.SURFACE_STRUCTURES, BREAKTHROUGH_PLACED);
    }
}

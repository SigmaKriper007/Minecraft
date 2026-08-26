package com.opus.paradise.world;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.entity.CloudGrazerEntity;
import com.opus.paradise.entity.SunfinchEntity;
import com.opus.paradise.entity.ParadiseWyvernEntity;
import com.opus.paradise.registry.ParadiseEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

public final class ParadiseSpawns {
    private static final TagKey<Biome> ELIGIBLE_BIOMES = TagKey.create(Registries.BIOME, ParadiseLine.id("has_paradise_island"));

    private ParadiseSpawns() { }

    public static void init() {
        BiomeModifications.addSpawn(BiomeSelectors.tag(ELIGIBLE_BIOMES), MobCategory.CREATURE,
            ParadiseEntities.SUNFINCH, 7, 3, 5);
        BiomeModifications.addSpawn(BiomeSelectors.tag(ELIGIBLE_BIOMES), MobCategory.CREATURE,
            ParadiseEntities.CLOUD_GRAZER, 4, 2, 3);
        BiomeModifications.addSpawn(BiomeSelectors.tag(ELIGIBLE_BIOMES), MobCategory.CREATURE,
            ParadiseEntities.PARADISE_WYVERN, 1, 1, 1);
        SpawnPlacements.register(ParadiseEntities.SUNFINCH, SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SunfinchEntity::canSpawn);
        SpawnPlacements.register(ParadiseEntities.CLOUD_GRAZER, SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CloudGrazerEntity::canSpawn);
        SpawnPlacements.register(ParadiseEntities.PARADISE_WYVERN, SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ParadiseWyvernEntity::canSpawn);
        ParadiseIslandSpawner.init();
    }
}

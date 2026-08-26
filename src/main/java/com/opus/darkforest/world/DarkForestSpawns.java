package com.opus.darkforest.world;

import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.entity.GloomBroodmotherEntity;
import com.opus.darkforest.entity.MoonwingBatEntity;
import com.opus.darkforest.entity.ShadeSpiderlingEntity;
import com.opus.darkforest.registry.DarkForestEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

public final class DarkForestSpawns {
    private DarkForestSpawns() { }

    public static void init() {
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(DarkForestLine.DARK_FOREST), MobCategory.MONSTER,
            DarkForestEntities.SHADE_SPIDERLING, 70, 2, 4);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(DarkForestLine.DARK_FOREST), MobCategory.MONSTER,
            DarkForestEntities.MOONWING_BAT, 24, 1, 2);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(DarkForestLine.DARK_FOREST), MobCategory.MONSTER,
            DarkForestEntities.GLOOM_BROODMOTHER, 22, 1, 1);
        SpawnPlacements.register(DarkForestEntities.SHADE_SPIDERLING, SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ShadeSpiderlingEntity::canSpawn);
        SpawnPlacements.register(DarkForestEntities.GLOOM_BROODMOTHER, SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GloomBroodmotherEntity::canSpawn);
        SpawnPlacements.register(DarkForestEntities.MOONWING_BAT, SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MoonwingBatEntity::canSpawn);
    }
}

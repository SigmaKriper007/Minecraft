package com.opus.darkforest.registry;

import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.blockentity.MoonFountainCoreBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class DarkForestBlockEntities {
    public static final BlockEntityType<MoonFountainCoreBlockEntity> MOON_FOUNTAIN_CORE = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE, DarkForestLine.id("moon_fountain_core"),
        FabricBlockEntityTypeBuilder.create(MoonFountainCoreBlockEntity::new, DarkForestBlocks.MOON_FOUNTAIN_CORE).build());
    private DarkForestBlockEntities() { }
    public static void init() { }
}

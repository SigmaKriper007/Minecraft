package com.opus.paradise.registry;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.blockentity.AngelDaisBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ParadiseBlockEntities {
    public static final BlockEntityType<AngelDaisBlockEntity> ANGEL_DAIS = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE, ParadiseLine.id("angel_dais"),
        FabricBlockEntityTypeBuilder.create(AngelDaisBlockEntity::new, ParadiseBlocks.ANGEL_DAIS).build());
    private ParadiseBlockEntities() { }
    public static void init() { }
}

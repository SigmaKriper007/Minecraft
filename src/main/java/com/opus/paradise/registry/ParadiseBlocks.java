package com.opus.paradise.registry;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.block.ParadiseSaplingBlock;
import com.opus.paradise.block.AngelDaisBlock;
import com.opus.paradise.block.ParthenonForgeBlock;
import com.opus.paradise.block.SeraphicReliquaryBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class ParadiseBlocks {
    public static final Block CELESTIAL_STONE = register("celestial_stone", new Block(BlockBehaviour.Properties.of()
        .mapColor(MapColor.QUARTZ).strength(2.8F, 7.0F).requiresCorrectToolForDrops().sound(SoundType.CALCITE)));
    public static final Block PARADISE_SOIL = register("paradise_soil", new Block(BlockBehaviour.Properties.of()
        .mapColor(MapColor.TERRACOTTA_WHITE).strength(0.65F).sound(SoundType.ROOTED_DIRT)));
    public static final Block PARADISE_GRASS = register("paradise_grass", new Block(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_LIGHT_GREEN).strength(0.7F).sound(SoundType.GRASS)));
    public static final Block PARADISE_LOG = register("paradise_log", new RotatedPillarBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_YELLOW).strength(2.1F).sound(SoundType.CHERRY_WOOD)));
    public static final Block PARADISE_LEAVES = register("paradise_leaves", new LeavesBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_LIGHT_GREEN).strength(0.25F).sound(SoundType.AZALEA_LEAVES)
        .noOcclusion().randomTicks()));
    public static final Block PARADISE_SAPLING = register("paradise_sapling", new ParadiseSaplingBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_LIGHT_GREEN).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
    public static final Block PARTHENON_MARBLE = register("parthenon_marble", new Block(BlockBehaviour.Properties.of()
        .mapColor(MapColor.QUARTZ).strength(2.2F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.CALCITE)));
    public static final Block GILDED_MARBLE = register("gilded_marble", new Block(BlockBehaviour.Properties.of()
        .mapColor(MapColor.GOLD).strength(2.5F, 7.0F).requiresCorrectToolForDrops().sound(SoundType.METAL)
        .lightLevel(state -> 3)));
    public static final Block ANGEL_DAIS = register("angel_dais", new AngelDaisBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.GOLD).strength(-1.0F, 3600000.0F).sound(SoundType.AMETHYST)
        .lightLevel(state -> 8)));
    public static final Block PARTHENON_FORGE = register("parthenon_forge", new ParthenonForgeBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.GOLD).strength(3.5F,8F).requiresCorrectToolForDrops().sound(SoundType.METAL).lightLevel(state->5)));
    public static final Block SERAPHIC_RELIQUARY = register("seraphic_reliquary",new SeraphicReliquaryBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.QUARTZ).strength(-1F,3600000F).noOcclusion().sound(SoundType.AMETHYST).lightLevel(state->state.getValue(SeraphicReliquaryBlock.CHARGED)?9:2)));

    private ParadiseBlocks() { }

    private static <T extends Block> T register(String id, T block) {
        return Registry.register(BuiltInRegistries.BLOCK, ParadiseLine.id(id), block);
    }

    public static void init() { }
}

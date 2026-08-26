package com.opus.darkforest.registry;

import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.block.DarkForestPlantBlock;
import com.opus.darkforest.block.GloomwoodSaplingBlock;
import com.opus.darkforest.block.MoonFountainCoreBlock;
import com.opus.darkforest.block.RootboundPedestalBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class DarkForestBlocks {
    public static final Block MOONLIT_SOIL=register("moonlit_soil",new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(.75F).sound(SoundType.ROOTED_DIRT)));
    public static final Block MOONLIT_GRASS=register("moonlit_grass",new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(.8F).sound(SoundType.GRASS)));
    public static final Block GLOOMWOOD_LOG=register("gloomwood_log",new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(2.2F).sound(SoundType.WOOD)));
    public static final Block GLOOMWOOD_LEAVES=register("gloomwood_leaves",new LeavesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(.28F).sound(SoundType.AZALEA_LEAVES).noOcclusion().randomTicks()));
    public static final Block GLOOMWOOD_SAPLING=register("gloomwood_sapling",new GloomwoodSaplingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
    public static final Block MOONFLOWER=register("moonflower",new DarkForestPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).noCollission().instabreak().sound(SoundType.GRASS).lightLevel(state->3),Block.box(3,0,3,13,13,13)));
    public static final Block THORN_FERN=register("thorn_fern",new DarkForestPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).noCollission().instabreak().sound(SoundType.GRASS),Block.box(2,0,2,14,12,14)));
    public static final Block FOUNTAIN_STONE=register("fountain_stone",new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(2.8F,7F).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE_TILES)));
    public static final Block MOON_FOUNTAIN_CORE=register("moon_fountain_core",new MoonFountainCoreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(-1F,3600000F).sound(SoundType.AMETHYST).lightLevel(state->8).noOcclusion()));
    public static final Block ROOTBOUND_PEDESTAL=register("rootbound_pedestal",new RootboundPedestalBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(-1F,3600000F).sound(SoundType.DEEPSLATE_TILES).noOcclusion().lightLevel(state->state.getValue(RootboundPedestalBlock.CHARGED)?9:2)));
    private DarkForestBlocks(){ }
    private static <T extends Block>T register(String id,T block){return Registry.register(BuiltInRegistries.BLOCK,DarkForestLine.id(id),block);}
    public static void init(){ }
}

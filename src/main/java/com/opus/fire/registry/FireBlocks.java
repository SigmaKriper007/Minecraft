package com.opus.fire.registry;

import com.opus.fire.FireLine;
import com.opus.fire.block.EmberBeanBlock;
import com.opus.fire.block.FirePortalBlock;
import com.opus.fire.block.CrimsonIceBlock;
import com.opus.fire.block.FireVineBlock;
import com.opus.fire.block.EmberKapokSaplingBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public final class FireBlocks {
    public static final Block FIRE_SOIL = register("fire_soil", new Block(BlockBehaviour.Properties.of()
        .mapColor(MapColor.TERRACOTTA_BROWN).strength(0.65f).sound(SoundType.SAND)));
    public static final Block MAGMA_CRUST = register("magma_crust", new Block(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_BLACK).strength(3.4f, 5.5f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE)
        .lightLevel(state -> 3)));
    public static final Block ASH_BLOCK = register("ash_block", new Block(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_GRAY).strength(0.55f).sound(SoundType.SOUL_SAND)));
    public static final Block EMBER_LOG = register("ember_log", new RotatedPillarBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_BLACK).strength(2.2f).sound(SoundType.STEM).lightLevel(state -> 2)));
    public static final Block EMBER_LEAVES = register("ember_leaves", new LeavesBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_PURPLE).strength(0.25f).sound(SoundType.AZALEA_LEAVES).noOcclusion().randomTicks()
        .lightLevel(state -> 3)));
    public static final EmberKapokSaplingBlock EMBER_SAPLING = register("ember_sapling",
        new EmberKapokSaplingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE)
            .strength(0.0F).sound(SoundType.GRASS).noCollission().noOcclusion()
            .pushReaction(PushReaction.DESTROY).lightLevel(state -> 2)));
    public static final FireVineBlock FIRE_VINE = register("fire_vine", new FireVineBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_RED).strength(0.1f).sound(SoundType.WEEPING_VINES).noCollission().noOcclusion()
        .pushReaction(PushReaction.DESTROY).lightLevel(state -> 2)));
    public static final EmberBeanBlock FIRE_BEAN = register("fire_bean", new EmberBeanBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_ORANGE).strength(0.35f).sound(SoundType.CROP).noOcclusion().randomTicks()
        .pushReaction(PushReaction.DESTROY).lightLevel(state -> 3 + state.getValue(EmberBeanBlock.AGE) * 2)));
    public static final Block CRIMSON_ICE = register("crimson_ice", new CrimsonIceBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_RED).strength(1.4f, 7.0f).sound(SoundType.GLASS).friction(0.97f).noOcclusion()
        .lightLevel(state -> 2)));
    public static final Block FIRE_PORTAL = register("fire_portal", new FirePortalBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.FIRE).strength(-1.0f, 3_600_000.0f).sound(SoundType.GLASS).noCollission().noOcclusion()
        .pushReaction(PushReaction.BLOCK).lightLevel(state -> 12)));

    private FireBlocks() { }

    private static <T extends Block> T register(String name, T block) {
        return Registry.register(BuiltInRegistries.BLOCK, FireLine.id(name), block);
    }

    public static void init() { }
}

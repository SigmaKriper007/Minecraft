package com.opus.ember.registry;

import com.opus.ember.EmberLine;
import com.opus.ember.block.CinderBeanBlock;
import com.opus.ember.block.CinderVineBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public final class EmberBlocks {
    public static final Block CINDER_SOIL = register("cinder_soil", new Block(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_GRAY).strength(0.65f).sound(SoundType.SAND)));
    public static final Block CINDER_CRUST = register("cinder_crust", new Block(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_BLACK).strength(3.4f, 5.5f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE)
        .lightLevel(state -> 3)));
    public static final Block CINDER_ASH = register("cinder_ash", new Block(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_GRAY).strength(0.55f).sound(SoundType.SOUL_SAND)));
    public static final Block CINDER_LOG = register("cinder_log", new RotatedPillarBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_BLACK).strength(2.2f).sound(SoundType.STEM).lightLevel(state -> 2)));
    public static final Block CINDER_LEAVES = register("cinder_leaves", new LeavesBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_GRAY).strength(0.25f).sound(SoundType.AZALEA_LEAVES).noOcclusion().randomTicks()
        .lightLevel(state -> 3)));
    public static final CinderVineBlock CINDER_VINE = register("cinder_vine", new CinderVineBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_GRAY).strength(0.1f).sound(SoundType.WEEPING_VINES).noCollission().noOcclusion()
        .pushReaction(PushReaction.DESTROY).lightLevel(state -> 2)));
    public static final CinderBeanBlock CINDER_BEAN = register("cinder_bean", new CinderBeanBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_GRAY).strength(0.35f).sound(SoundType.CROP).noOcclusion().randomTicks()
        .pushReaction(PushReaction.DESTROY).lightLevel(state -> 3 + state.getValue(CinderBeanBlock.AGE) * 2)));
    public static final Block CINDER_SEAL = register("cinder_seal", new HalfTransparentBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_GRAY).strength(1.4f, 7.0f).sound(SoundType.GLASS).friction(0.97f).noOcclusion()
        .lightLevel(state -> 2)));
    public static final Block CINDER_PORTAL = register("cinder_portal", new Block(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_GRAY).strength(-1.0f, 3_600_000.0f).sound(SoundType.GLASS).noCollission().noOcclusion()
        .pushReaction(PushReaction.BLOCK).lightLevel(state -> 12)));

    private EmberBlocks() { }

    private static <T extends Block> T register(String name, T block) {
        return Registry.register(BuiltInRegistries.BLOCK, EmberLine.id(name), block);
    }

    public static void init() { }
}

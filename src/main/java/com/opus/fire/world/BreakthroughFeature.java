package com.opus.fire.world;

import com.mojang.serialization.Codec;
import com.opus.fire.registry.FireBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.List;

/** A complete, usable Four Veins rift that may appear exposed or buried in Nether terrain. */
public final class BreakthroughFeature extends Feature<NoneFeatureConfiguration> {
    public BreakthroughFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        boolean alongX = random.nextBoolean();
        List<Cell> cells = design(origin, alongX);

        for (Cell cell : cells) {
            BlockState existing = level.getBlockState(cell.pos());
            if (existing.is(Blocks.BEDROCK) || !existing.getFluidState().isEmpty()
                    || existing.getFluidState().is(FluidTags.LAVA)
                    || level.getBlockEntity(cell.pos()) != null) {
                return false;
            }
        }

        for (Cell cell : cells) {
            level.setBlock(cell.pos(), cell.state(), 3);
        }
        return true;
    }

    private static List<Cell> design(BlockPos origin, boolean alongX) {
        List<Cell> cells = new ArrayList<>();
        for (int v = -3; v <= 3; v++) {
            for (int u = -2; u <= 2; u++) {
                boolean core = Math.abs(u) <= 1 && Math.abs(v) <= 2;
                boolean frame = (Math.abs(u) == 2 && Math.abs(v) <= 2)
                    || (Math.abs(v) == 3 && Math.abs(u) <= 1)
                    || (Math.abs(u) == 2 && Math.abs(v) == 3);
                if (!core && !frame) continue;

                BlockPos target = offset(origin, u, v, 0, alongX);
                if (core) {
                    cells.add(new Cell(target, FireBlocks.FIRE_PORTAL.defaultBlockState()));
                    cells.add(new Cell(offset(origin, u, v, -1, alongX), Blocks.AIR.defaultBlockState()));
                    cells.add(new Cell(offset(origin, u, v, 1, alongX), Blocks.AIR.defaultBlockState()));
                } else {
                    boolean corner = Math.abs(u) == 2 && Math.abs(v) == 3;
                    BlockState state = corner ? Blocks.CRYING_OBSIDIAN.defaultBlockState()
                        : ((u + v) & 3) == 0 ? Blocks.GILDED_BLACKSTONE.defaultBlockState()
                        : Blocks.OBSIDIAN.defaultBlockState();
                    cells.add(new Cell(target, state));
                }
            }
        }

        for (int v : new int[]{-2, 0, 2}) {
            cells.add(new Cell(offset(origin, -3, v, 0, alongX), Blocks.POLISHED_BASALT.defaultBlockState()));
            cells.add(new Cell(offset(origin, 3, v, 0, alongX), Blocks.POLISHED_BASALT.defaultBlockState()));
        }
        cells.add(new Cell(offset(origin, 0, -4, 0, alongX), Blocks.MAGMA_BLOCK.defaultBlockState()));
        cells.add(new Cell(offset(origin, 0, 4, 0, alongX), Blocks.MAGMA_BLOCK.defaultBlockState()));
        return cells;
    }

    private static BlockPos offset(BlockPos origin, int u, int v, int depth, boolean alongX) {
        return alongX ? origin.offset(u, v, depth) : origin.offset(depth, v, u);
    }

    private record Cell(BlockPos pos, BlockState state) { }
}

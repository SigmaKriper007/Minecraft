package com.opus.darkforest.block;

import com.opus.darkforest.registry.DarkForestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Renewable Gloomwood with the same guarded silhouette as natural trees. */
public final class GloomwoodSaplingBlock extends BushBlock implements BonemealableBlock {
    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 15, 14);

    public GloomwoodSaplingBlock(Properties properties) { super(properties); }

    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter level, BlockPos pos) {
        return floor.is(DarkForestBlocks.MOONLIT_GRASS) || floor.is(DarkForestBlocks.MOONLIT_SOIL)
            || super.mayPlaceOn(floor, level, pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getMaxLocalRawBrightness(pos.above()) >= 7 && random.nextInt(7) == 0) grow(level, pos, random);
    }

    @Override public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean client) { return hasClearance(level, pos, 12); }
    @Override public boolean isBonemealSuccess(net.minecraft.world.level.Level level, RandomSource random, BlockPos pos, BlockState state) { return random.nextFloat() < .65F; }
    @Override public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) { grow(level, pos, random); }

    public static boolean grow(ServerLevel level, BlockPos pos, RandomSource random) {
        int height = 8 + random.nextInt(5);
        if (!hasClearance(level, pos, height)) return false;
        BlockState log = DarkForestBlocks.GLOOMWOOD_LOG.defaultBlockState();
        BlockState leaves = DarkForestBlocks.GLOOMWOOD_LEAVES.defaultBlockState()
            .setValue(LeavesBlock.PERSISTENT, false).setValue(LeavesBlock.DISTANCE, 1);
        for (int y = 0; y < height; y++) level.setBlock(pos.above(y), log, 3);
        BlockPos crown = pos.above(height - 2);
        for (int oy = -2; oy <= 3; oy++) {
            int radius = oy <= 0 ? 4 : oy == 1 ? 3 : 2;
            for (int x = -radius; x <= radius; x++) for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius + 2 || (x == 0 && z == 0 && oy <= 1)) continue;
                BlockPos leafPos = crown.offset(x, oy, z);
                if (level.getBlockState(leafPos).isAir()) level.setBlock(leafPos, leaves, 3);
            }
        }
        for (int[] branch : new int[][]{{2,0},{-2,0},{0,2},{0,-2}}) {
            level.setBlock(pos.offset(branch[0] / 2, height - 3, branch[1] / 2), log, 3);
            level.setBlock(pos.offset(branch[0], height - 2, branch[1]), log, 3);
        }
        return true;
    }

    private static boolean hasClearance(LevelReader level, BlockPos pos, int height) {
        for (int y = 0; y <= height + 3; y++) {
            int radius = y < height - 4 ? 1 : 4;
            for (int x = -radius; x <= radius; x++) for (int z = -radius; z <= radius; z++) {
                if (y == 0 && x == 0 && z == 0) continue;
                BlockPos checkPos = pos.offset(x, y, z);
                BlockState existing = level.getBlockState(checkPos);
                if (!existing.isAir() && !existing.is(DarkForestBlocks.GLOOMWOOD_LEAVES) && !existing.canBeReplaced()) return false;
                if (!existing.getFluidState().isEmpty() || level.getBlockEntity(checkPos) != null) return false;
            }
        }
        return true;
    }
}

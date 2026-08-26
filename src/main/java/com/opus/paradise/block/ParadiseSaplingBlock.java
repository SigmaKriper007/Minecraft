package com.opus.paradise.block;

import com.opus.paradise.registry.ParadiseBlocks;
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

public final class ParadiseSaplingBlock extends BushBlock implements BonemealableBlock {
    private static final VoxelShape SHAPE = box(2.0, 0.0, 2.0, 14.0, 15.0, 14.0);

    public ParadiseSaplingBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter level, BlockPos pos) {
        return floor.is(ParadiseBlocks.PARADISE_GRASS) || floor.is(ParadiseBlocks.PARADISE_SOIL)
            || super.mayPlaceOn(floor, level, pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getMaxLocalRawBrightness(pos.above()) >= 9 && random.nextInt(7) == 0) grow(level, pos, random);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean client) {
        return hasClearance(level, pos, 10);
    }

    @Override
    public boolean isBonemealSuccess(net.minecraft.world.level.Level level, RandomSource random, BlockPos pos, BlockState state) {
        return random.nextFloat() < 0.72F;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        grow(level, pos, random);
    }

    private static boolean hasClearance(LevelReader level, BlockPos pos, int height) {
        for (int y = 1; y <= height + 2; y++) {
            int radius = y < height - 3 ? 1 : 4;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockState existing = level.getBlockState(pos.offset(x, y, z));
                    if (!existing.isAir() && !existing.is(ParadiseBlocks.PARADISE_LEAVES)) return false;
                    if (!existing.getFluidState().isEmpty()) return false;
                }
            }
        }
        return true;
    }

    private static boolean grow(ServerLevel level, BlockPos pos, RandomSource random) {
        int height = 7 + random.nextInt(3);
        if (!hasClearance(level, pos, height)) return false;

        BlockState log = ParadiseBlocks.PARADISE_LOG.defaultBlockState();
        BlockState leaves = ParadiseBlocks.PARADISE_LEAVES.defaultBlockState()
            .setValue(LeavesBlock.PERSISTENT, false).setValue(LeavesBlock.DISTANCE, 1);
        for (int y = 0; y < height; y++) level.setBlock(pos.above(y), log, 3);

        BlockPos crown = pos.above(height - 1);
        for (int oy = -2; oy <= 3; oy++) {
            int radius = switch (oy) { case -2 -> 2; case -1, 0 -> 4; case 1 -> 3; default -> 2; };
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z > radius * radius + 1 || (x == 0 && z == 0 && oy <= 0)) continue;
                    BlockPos leafPos = crown.offset(x, oy, z);
                    if (level.getBlockState(leafPos).isAir()) level.setBlock(leafPos, leaves, 3);
                }
            }
        }
        level.setBlock(crown.above(4), leaves, 3);
        return true;
    }
}

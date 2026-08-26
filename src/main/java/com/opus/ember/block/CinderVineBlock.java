package com.opus.ember.block;

import com.opus.ember.registry.EmberBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CinderVineBlock extends Block {
    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(7, 0, 1, 9, 16, 15), Block.box(1, 0, 7, 15, 16, 9));

    public CinderVineBlock(Properties properties) { super(properties); }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState above = level.getBlockState(pos.above());
        return above.is(this) || above.is(EmberBlocks.CINDER_LEAVES) || above.is(EmberBlocks.CINDER_LOG);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}

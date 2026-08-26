package com.opus.darkforest.block;

import com.opus.darkforest.registry.DarkForestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Small forest-floor plant restricted to the native moonlit substrate. */
public final class DarkForestPlantBlock extends BushBlock {
    private final VoxelShape shape;

    public DarkForestPlantBlock(Properties properties, VoxelShape shape) {
        super(properties);
        this.shape = shape;
    }

    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter level, BlockPos pos) {
        return floor.is(DarkForestBlocks.MOONLIT_GRASS) || floor.is(DarkForestBlocks.MOONLIT_SOIL);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
    }
}

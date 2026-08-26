package com.opus.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EnergyBeamBlock extends RotatedPillarBlock {
    // тонкий луч: квадратное сечение 4/16
    private static final VoxelShape SHAPE_Y = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    private static final VoxelShape SHAPE_X = Block.box(0.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);
    private static final VoxelShape SHAPE_Z = Block.box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 16.0D);

    public EnergyBeamBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(AXIS)) {
            case X -> SHAPE_X;
            case Z -> SHAPE_Z;
            default -> SHAPE_Y;
        };
    }
}
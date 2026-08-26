package com.opus.ember.block;

import com.opus.ember.EmberLine;
import com.opus.ember.registry.EmberBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CinderBeanBlock extends HorizontalDirectionalBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 2);
    private static final VoxelShape NORTH = Block.box(4, 3, 0, 12, 13, 7);
    private static final VoxelShape SOUTH = Block.box(4, 3, 9, 12, 13, 16);
    private static final VoxelShape WEST = Block.box(9, 3, 4, 16, 13, 12);
    private static final VoxelShape EAST = Block.box(0, 3, 4, 7, 13, 12);

    public CinderBeanBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, AGE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        for (Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis().isHorizontal()) {
                BlockState state = defaultBlockState().setValue(FACING, direction);
                if (state.canSurvive(context.getLevel(), context.getClickedPos())) return state;
            }
        }
        return null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.relative(state.getValue(FACING).getOpposite())).is(EmberBlocks.CINDER_LOG);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < 2;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(AGE) < 2 && random.nextInt(5) == 0) {
            level.setBlock(pos, state.setValue(AGE, state.getValue(AGE) + 1), 2);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
            default -> NORTH;
        };
    }
}

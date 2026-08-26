package com.opus.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PhasedBarrierBlock extends EnergyBarrierBlock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public PhasedBarrierBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(ACTIVE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(ACTIVE)
                ? super.getCollisionShape(state, level, pos, context)
                : Shapes.empty();
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide) {
            // сигнал открывает барьер (active = false)
            boolean active = !level.hasNeighborSignal(pos);
            if (state.getValue(ACTIVE) != active) {
                level.setBlock(pos, state.setValue(ACTIVE, active), 2);
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(state.getBlock()) && !level.isClientSide) {
            boolean active = !level.hasNeighborSignal(pos);
            if (active != state.getValue(ACTIVE)) {
                level.setBlock(pos, state.setValue(ACTIVE, active), 2);
            }
        }
    }
}
package com.opus.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Энергоузел — красстоун-проводник для пазлов «маршрут энергии».
 * Релеё качает питание от источника (любой красстоун-сигнал) к соседним блокам,
 * позволяя вести скрытые линии через массивы стен к phased_barrier/trial_trigger.
 */
public class EnergyRelayBlock extends Block {
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public EnergyRelayBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        updatePowered(state, level, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                BlockPos neighborPos, boolean movedByPiston) {
        updatePowered(state, level, pos);
    }

    private void updatePowered(BlockState state, Level level, BlockPos pos) {
        if (level.isClientSide) return;
        boolean powered = level.hasNeighborSignal(pos);
        if (powered != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, powered), 3);
        }
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(POWERED) && random.nextInt(4) == 0) {
            level.addParticle(ParticleTypes.END_ROD,
                    pos.getX() + 0.5, pos.getY() + 1.05, pos.getZ() + 0.5,
                    0.0, 0.02, 0.0);
        }
    }
}
package com.opus.block;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

public class ForceFieldProjectorBlock extends PoweredHorizontalBlock {
    public ForceFieldProjectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // линза смотрит на игрока
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }
}
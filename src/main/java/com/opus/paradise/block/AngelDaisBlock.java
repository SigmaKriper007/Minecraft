package com.opus.paradise.block;

import com.opus.paradise.blockentity.AngelDaisBlockEntity;
import com.opus.paradise.registry.ParadiseBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public final class AngelDaisBlock extends BaseEntityBlock {
    public AngelDaisBlock(Properties properties) { super(properties); }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new AngelDaisBlockEntity(pos, state); }

    @Override public InteractionResult use(BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(level.isClientSide)return InteractionResult.SUCCESS;
        if(level.getBlockEntity(pos) instanceof AngelDaisBlockEntity dais){dais.tryStartRitual(player,player.getItemInHand(hand));return InteractionResult.CONSUME;}
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ParadiseBlockEntities.ANGEL_DAIS, AngelDaisBlockEntity::serverTick);
    }
}

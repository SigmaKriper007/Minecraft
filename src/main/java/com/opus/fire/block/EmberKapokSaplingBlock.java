package com.opus.fire.block;

import com.opus.fire.registry.FireParticles;
import com.opus.fire.world.FireRealmBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class EmberKapokSaplingBlock extends BushBlock {
    private static final VoxelShape SHAPE = box(3.0, 0.0, 3.0, 13.0, 15.0, 13.0);

    public EmberKapokSaplingBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.isFaceSturdy(level, pos, net.minecraft.core.Direction.UP);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!player.getItemInHand(hand).is(Items.BLAZE_POWDER)) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level instanceof ServerLevel server)
                || !FireRealmBuilder.growPlayerKapok(server, pos, server.random)) {
            return InteractionResult.FAIL;
        }

        if (!player.getAbilities().instabuild) player.getItemInHand(hand).shrink(1);
        server.sendParticles(FireParticles.EMBER, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
            36, 1.7, 2.2, 1.7, 0.12);
        server.playSound(null, pos, SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS, 1.0F, 0.72F);
        if (player instanceof ServerPlayer serverPlayer) serverPlayer.swing(hand, true);
        return InteractionResult.CONSUME;
    }
}

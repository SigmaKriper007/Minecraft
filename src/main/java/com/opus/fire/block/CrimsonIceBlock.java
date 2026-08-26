package com.opus.fire.block;

import com.opus.fire.registry.FireItems;
import com.opus.fire.sound.FireSounds;
import com.opus.fire.world.FireRealmBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class CrimsonIceBlock extends HalfTransparentBlock {
    private static final int ESSENCE_COST = 4;

    public CrimsonIceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(FireItems.FIRE_ESSENCE) || !level.dimension().equals(FirePortalBlock.FIRE_REALM)
                || Math.abs(pos.getX()) > 3 || Math.abs(pos.getZ()) > 3) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (stack.getCount() < ESSENCE_COST) {
            player.displayClientMessage(Component.translatable("message.opusvsexe.diablo.need_essence", ESSENCE_COST), true);
            return InteractionResult.CONSUME;
        }
        ServerLevel server = (ServerLevel) level;
        if (!FireRealmBuilder.restoreBossSeal(server)) {
            player.displayClientMessage(Component.translatable("message.opusvsexe.diablo.already_present"), true);
            return InteractionResult.CONSUME;
        }
        if (!(player instanceof ServerPlayer serverPlayer) || !serverPlayer.isCreative()) {
            stack.shrink(ESSENCE_COST);
        }
        server.sendParticles(ParticleTypes.SNOWFLAKE, 0.5D, pos.getY() + 2.0D, 0.5D,
                90, 2.3D, 2.0D, 2.3D, 0.08D);
        server.playSound(null, new BlockPos(0, pos.getY(), 0), FireSounds.DEMON_ROAR,
                SoundSource.HOSTILE, 1.4F, 1.35F);
        player.displayClientMessage(Component.translatable("message.opusvsexe.diablo.seal_restored"), true);
        return InteractionResult.CONSUME;
    }
}

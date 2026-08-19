package com.opus.block;

import com.opus.blockentity.ModBlockEntities;
import com.opus.blockentity.ResonanceForgeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ResonanceForgeBlock extends OpusHorizontalBlock implements EntityBlock {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    public ResonanceForgeBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.sidedSuccess(true);
        if (!(level.getBlockEntity(pos) instanceof ResonanceForgeBlockEntity forge)) {
            return InteractionResult.PASS;
        }
        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(Items.FLINT_AND_STEEL)) {
            boolean lit = !state.getValue(LIT);
            level.setBlock(pos, state.setValue(LIT, lit), 3);
            level.playSound(null, pos, lit ? SoundEvents.FLINTANDSTEEL_USE : SoundEvents.FIRE_EXTINGUISH,
                    SoundSource.BLOCKS, 1.0f, 1.0f);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(lit ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMOKE,
                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 12, 0.2, 0.3, 0.2, 0.0);
            }
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (stack.isEmpty() && !forge.getOutput().isEmpty()) {
            player.addItem(forge.getOutput());
            forge.setOutput(ItemStack.EMPTY);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!stack.isEmpty() && forge.getInput().isEmpty()) {
            forge.setInput(stack.copyWithCount(1));
            stack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            double x = (double) pos.getX() + 0.5 + random.nextDouble() * 0.4 - 0.2;
            double y = (double) pos.getY() + 0.8 + random.nextDouble() * 0.4 - 0.2;
            double z = (double) pos.getZ() + 0.5 + random.nextDouble() * 0.4 - 0.2;
            level.addParticle(ParticleTypes.PORTAL, x, y, z, 0.0, 0.1, 0.0);
            if (random.nextInt(10) == 0) {
                level.playLocalSound(x, y, z, SoundEvents.BLASTFURNACE_FIRE_CRACKLE,
                        SoundSource.BLOCKS, 0.5f + random.nextFloat() * 0.3f,
                        0.8f + random.nextFloat() * 0.4f, false);
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ResonanceForgeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.RESONANCE_FORGE
                ? (lvl, pos, st, be) -> ((ResonanceForgeBlockEntity) be).tick(lvl, pos, st)
                : null;
    }
}
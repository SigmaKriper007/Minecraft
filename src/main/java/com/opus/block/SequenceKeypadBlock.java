package com.opus.block;

import com.opus.blockentity.ModBlockEntities;
import com.opus.blockentity.SequenceKeypadBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Клавиатура последовательности — пазл «порядок нажатия».
 * Ряд keypad-блоков одной группы (nbt id) имеет скрытые порядковые номера (nbt order).
 * Нажимать нужно строго в порядке возрастания order; ошибка сбрасывает всю группу.
 * Когда решена последняя — открывает nearby reward_vault и светится.
 */
public class SequenceKeypadBlock extends OpusHorizontalBlock implements EntityBlock {
    public static final BooleanProperty SOLVED = BooleanProperty.create("solved");

    public SequenceKeypadBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(SOLVED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SOLVED);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof SequenceKeypadBlockEntity keypad) {
            keypad.press(player);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(SOLVED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(SOLVED) ? 15 : 0;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SequenceKeypadBlockEntity(pos, state);
    }
}
package com.opus.blockentity;

import com.opus.block.RewardVaultBlock;
import com.opus.block.SequenceKeypadBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * Логика пазла порядковых клавиатур.
 * NBT: "id" (группа), "order" (порядковый номер 0..n-1), "solved" (0/1).
 */
public class SequenceKeypadBlockEntity extends BlockEntity {
    private static final int GROUP_SCAN = 24;
    private static final int VAULT_SCAN = 16;
    private static final float WRONG_DAMAGE = 3.0f;

    private String puzzleId = "";
    private int order = 0;
    private boolean solved = false;

    public SequenceKeypadBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SEQUENCE_KEYPAD, pos, state);
    }

    public void press(Player player) {
        if (level == null || level.isClientSide || solved) return;

        List<SequenceKeypadBlockEntity> group = gatherGroup();
        if (group.isEmpty()) return;

        int myOrder = order;
        int solvedCount = 0;
        boolean invalid = false;
        for (SequenceKeypadBlockEntity pad : group) {
            if (pad.solved) {
                solvedCount++;
                if (pad.order > myOrder) invalid = true;
            } else if (pad.order < myOrder) {
                invalid = true;
            }
        }
        if (invalid) {
            resetGroup(group);
            if (player != null) {
                player.hurt(level.damageSources().magic(), WRONG_DAMAGE);
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.translatable("message.opusvsexe.sequence_keypad.wrong"), true);
            }
            return;
        }

        // правильный шаг
        solved = true;
        level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(SequenceKeypadBlock.SOLVED, true), 3);
        level.playSound(null, worldPosition, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0f, 1.4f);
        setChanged();

        if (solvedCount + 1 >= group.size()) {
            unlockNearbyVaults();
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                        worldPosition.getX() + 0.5, worldPosition.getY() + 1.1, worldPosition.getZ() + 0.5,
                        40, 1.0, 1.0, 1.0, 0.05);
            }
            level.playSound(null, worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    private List<SequenceKeypadBlockEntity> gatherGroup() {
        List<SequenceKeypadBlockEntity> group = new ArrayList<>();
        if (level == null) return group;
        for (BlockPos pos : BlockPos.betweenClosed(
                worldPosition.offset(-GROUP_SCAN, -GROUP_SCAN, -GROUP_SCAN),
                worldPosition.offset(GROUP_SCAN, GROUP_SCAN, GROUP_SCAN))) {
            if (level.getBlockEntity(pos) instanceof SequenceKeypadBlockEntity pad
                    && pad.puzzleId.equals(puzzleId)
                    && puzzleId != null && !puzzleId.isEmpty()) {
                group.add(pad);
            }
        }
        return group;
    }

    private void resetGroup(List<SequenceKeypadBlockEntity> group) {
        if (level == null) return;
        for (SequenceKeypadBlockEntity pad : group) {
            pad.solved = false;
            pad.setChanged();
            BlockState st = level.getBlockState(pad.worldPosition);
            if (st.getBlock() instanceof SequenceKeypadBlock && st.getValue(SequenceKeypadBlock.SOLVED)) {
                level.setBlock(pad.worldPosition, st.setValue(SequenceKeypadBlock.SOLVED, false), 3);
            }
        }
        level.playSound(null, worldPosition, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.BLOCKS, 0.6f, 1.6f);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5,
                    12, 0.4, 0.4, 0.4, 0.02);
        }
    }

    private void unlockNearbyVaults() {
        if (level == null) return;
        for (BlockPos pos : BlockPos.betweenClosed(
                worldPosition.offset(-VAULT_SCAN, -VAULT_SCAN, -VAULT_SCAN),
                worldPosition.offset(VAULT_SCAN, VAULT_SCAN, VAULT_SCAN))) {
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof RewardVaultBlock && !state.getValue(RewardVaultBlock.OPEN)) {
                RewardVaultBlock.unlock(level, pos.immutable());
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("puzzle_id", puzzleId);
        tag.putInt("order", order);
        tag.putBoolean("solved", solved);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        puzzleId = tag.getString("puzzle_id");
        order = tag.getInt("order");
        solved = tag.getBoolean("solved");
    }
}
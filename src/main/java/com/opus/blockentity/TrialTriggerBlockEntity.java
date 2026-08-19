package com.opus.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TrialTriggerBlockEntity extends BlockEntity {
    public static final int COOLDOWN_TICKS = 100;
    private int cooldown = 0;

    public TrialTriggerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRIAL_TRIGGER, pos, state);
    }

    public void tryTrigger() {
        if (cooldown > 0) return;
        if (level != null && !level.isClientSide) {
            cooldown = COOLDOWN_TICKS;
            com.opus.block.TrialTriggerBlock.triggerEffects(level, worldPosition, level.getBlockState(worldPosition));
            unlockNearbyVaults();
        }
    }

    private void unlockNearbyVaults() {
        if (level == null) return;
        int radius = 16;
        for (BlockPos pos : BlockPos.betweenClosed(
                worldPosition.offset(-radius, -radius, -radius), worldPosition.offset(radius, radius, radius))) {
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof com.opus.block.RewardVaultBlock
                    && !state.getValue(com.opus.block.RewardVaultBlock.OPEN)) {
                com.opus.block.RewardVaultBlock.unlock(level, pos.immutable());
            }
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (cooldown > 0) {
            cooldown--;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("cooldown", cooldown);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        cooldown = tag.getInt("cooldown");
    }
}
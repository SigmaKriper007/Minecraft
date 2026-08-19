package com.opus.blockentity;

import com.opus.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ResonanceForgeBlockEntity extends BlockEntity {
    private final NonNullList<ItemStack> input = NonNullList.withSize(1, ItemStack.EMPTY);
    private final NonNullList<ItemStack> output = NonNullList.withSize(1, ItemStack.EMPTY);
    private int progress = 0;
    private static final int CRAFT_TIME = 160;

    public ResonanceForgeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESONANCE_FORGE, pos, state);
    }

    public ItemStack getInput() {
        return input.get(0);
    }

    public void setInput(ItemStack stack) {
        input.set(0, stack);
        setChanged();
    }

    public ItemStack getOutput() {
        return output.get(0);
    }

    public void setOutput(ItemStack stack) {
        output.set(0, stack);
        setChanged();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        boolean lit = state.getValue(com.opus.block.ResonanceForgeBlock.LIT);
        if (!lit || level.isClientSide) return;
        ItemStack in = input.get(0);
        ItemStack out = output.get(0);
        ItemStack result = recipeFor(in);
        if (result.isEmpty() || !out.isEmpty()) {
            progress = 0;
            return;
        }
        if (++progress >= CRAFT_TIME) {
            progress = 0;
            in.shrink(1);
            if (in.isEmpty()) input.set(0, ItemStack.EMPTY);
            output.set(0, result.copy());
            setChanged();
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, pos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0f, 0.8f);
            }
        }
    }

    private static ItemStack recipeFor(ItemStack stack) {
        if (stack.is(ModItems.RAW_OPUS)) return new ItemStack(ModItems.STABILIZED_OPUS);
        if (stack.is(ModItems.STABILIZED_OPUS)) return new ItemStack(ModItems.RESONANT_OPUS);
        if (stack.is(ModItems.RESONANT_OPUS)) return new ItemStack(ModItems.CORE_OPUS);
        return ItemStack.EMPTY;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Input", ContainerHelper.saveAllItems(new CompoundTag(), input));
        tag.put("Output", ContainerHelper.saveAllItems(new CompoundTag(), output));
        tag.putInt("progress", progress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag.getCompound("Input"), input);
        ContainerHelper.loadAllItems(tag.getCompound("Output"), output);
        progress = tag.getInt("progress");
    }
}
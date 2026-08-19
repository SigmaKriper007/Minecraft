package com.opus.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MemoryConsoleBlockEntity extends BlockEntity {
    private String memoryText = "";

    public MemoryConsoleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MEMORY_CONSOLE, pos, state);
    }

    public void setMemoryText(String text) {
        this.memoryText = text;
    }

    public void activate(Player player) {
        if (memoryText == null || memoryText.isBlank()) {
            player.displayClientMessage(Component.translatable("message.opusvsexe.memory_console.blank"), false);
        } else {
            player.displayClientMessage(Component.literal(memoryText), false);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("memory_text", memoryText);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        memoryText = tag.getString("memory_text");
    }
}
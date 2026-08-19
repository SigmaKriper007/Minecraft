package com.opusvsexe.entity.custom;

import com.opus.registry.ModTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class ExoContainer extends SimpleContainer {
    public ExoContainer(int size) {
        super(size);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return stack.is(ModTags.KATANAS) || stack.getItem() instanceof net.minecraft.world.item.SwordItem || stack.getItem() instanceof com.opus.item.WarhammerItem;
    }
}
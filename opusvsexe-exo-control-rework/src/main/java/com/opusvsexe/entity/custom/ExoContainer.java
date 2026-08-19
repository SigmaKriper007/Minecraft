package com.opusvsexe.entity.custom;

import com.opus.item.WarhammerItem;
import com.opus.registry.ModTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

/** Two bays: a weapon mount and a free module slot. */
public class ExoContainer extends SimpleContainer {

    public static final int SLOT_WEAPON = 0;
    public static final int SLOT_MODULE = 1;
    public static final int SIZE = 2;

    public ExoContainer() {
        super(SIZE);
    }

    /** Kept so old call sites still compile. */
    public ExoContainer(int size) {
        super(Math.max(SIZE, size));
    }

    public static boolean isWeapon(ItemStack stack) {
        return stack.is(ModTags.KATANAS)
                || stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof WarhammerItem;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        return index != SLOT_WEAPON || isWeapon(stack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}

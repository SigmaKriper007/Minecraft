package com.opusvsexe.inventory;

import com.opus.registry.ModMenus;
import com.opusvsexe.entity.custom.ExoContainer;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ExoInventoryMenu extends AbstractContainerMenu {
    private static final int EXO_SLOTS = 2;
    private final Container container;
    private final ExosuitEntity exo;

    public ExoInventoryMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, null);
    }

    public ExoInventoryMenu(int syncId, Inventory playerInventory, ExosuitEntity exo) {
        super(ModMenus.EXO_INVENTORY, syncId);
        this.exo = exo;
        this.container = exo != null ? exo.getInventory() : new ExoContainer(EXO_SLOTS);

        this.addSlot(new Slot(this.container, 0, 39, 44));
        this.addSlot(new Slot(this.container, 1, 119, 44));

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 130 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 184));
        }
    }

    public ExosuitEntity getExosuit() {
        return this.exo;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemStack = stack.copy();
            if (index < EXO_SLOTS) {
                if (!this.moveItemStackTo(stack, EXO_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, EXO_SLOTS, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.exo == null || this.exo.isAlive() && player.distanceToSqr(this.exo) < 64.0;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.setChanged();
    }
}
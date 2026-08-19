package com.opusvsexe.inventory;

import com.opus.registry.ModMenus;
import com.opusvsexe.entity.custom.ExoContainer;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ExoInventoryMenu extends AbstractContainerMenu {

    private static final int EXO_SLOTS = ExoContainer.SIZE;

    private final ExoContainer container;
    private final ExosuitEntity exo;

    public ExoInventoryMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, null);
    }

    public ExoInventoryMenu(int syncId, Inventory playerInventory, ExosuitEntity exo) {
        super(ModMenus.EXO_INVENTORY, syncId);
        this.exo = exo;
        this.container = exo != null ? exo.getInventory() : new ExoContainer();
        this.container.startOpen(playerInventory.player);

        this.addSlot(new RestrictedSlot(this.container, ExoContainer.SLOT_WEAPON, 39, 44));
        this.addSlot(new RestrictedSlot(this.container, ExoContainer.SLOT_MODULE, 119, 44));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 130 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 188));
        }
    }

    public ExosuitEntity getExosuit() {
        return this.exo;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
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
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.exo == null) {
            return true;
        }
        if (!this.exo.isAlive()) {
            return false;
        }
        // The pilot is inside the frame, so a plain distance check is not enough.
        return player.getVehicle() == this.exo || player.distanceToSqr(this.exo) < 256.0D;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
        this.container.setChanged();
    }

    private static class RestrictedSlot extends Slot {
        private final ExoContainer exoContainer;

        RestrictedSlot(ExoContainer container, int index, int x, int y) {
            super(container, index, x, y);
            this.exoContainer = container;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return this.exoContainer.canPlaceItem(this.getContainerSlot(), stack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}

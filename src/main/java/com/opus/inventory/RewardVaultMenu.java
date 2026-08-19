package com.opus.inventory;

import com.opus.OpusVsExe;
import com.opus.blockentity.RewardVaultBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RewardVaultMenu extends AbstractContainerMenu {
    private static final int VAULT_SLOTS = 27;
    private final Container vault;

    public RewardVaultMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, null);
    }

    public RewardVaultMenu(int syncId, Inventory playerInventory, RewardVaultBlockEntity vault) {
        super(com.opus.registry.ModMenus.REWARD_VAULT, syncId);
        this.vault = vault != null ? vault : new net.minecraft.world.SimpleContainer(VAULT_SLOTS);

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(this.vault, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 198));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemStack = stack.copy();
            if (index < VAULT_SLOTS) {
                if (!this.moveItemStackTo(stack, VAULT_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, VAULT_SLOTS, false)) {
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
        return this.vault.stillValid(player);
    }
}
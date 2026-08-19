package com.opus.client.screen;

import com.opusvsexe.entity.custom.ExosuitEntity;
import com.opusvsexe.inventory.ExoInventoryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

/**
 * Drawn entirely in code, so it no longer depends on a gui texture that was
 * never shipped (that is why the panel used to render as a black box).
 */
public class ExoInventoryScreen extends AbstractContainerScreen<ExoInventoryMenu> {

    private static final int PANEL = 0xFF23272E;
    private static final int PANEL_EDGE = 0xFF11141A;
    private static final int PANEL_LIGHT = 0xFF3A414B;
    private static final int SLOT_BG = 0xFF15181D;
    private static final int SLOT_EDGE = 0xFF4A525E;
    private static final int ENERGY_FILL = 0xFF3FD0FF;
    private static final int TEXT = 0xFFDCE6F0;

    public ExoInventoryScreen(ExoInventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 214;
        this.inventoryLabelY = 119;
        this.titleLabelY = 8;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        gui.fill(x - 1, y - 1, x + this.imageWidth + 1, y + this.imageHeight + 1, PANEL_EDGE);
        gui.fill(x, y, x + this.imageWidth, y + this.imageHeight, PANEL);
        gui.fill(x, y, x + this.imageWidth, y + 1, PANEL_LIGHT);

        for (Slot slot : this.menu.slots) {
            gui.fill(x + slot.x - 1, y + slot.y - 1, x + slot.x + 17, y + slot.y + 17, SLOT_EDGE);
            gui.fill(x + slot.x, y + slot.y, x + slot.x + 16, y + slot.y + 16, SLOT_BG);
        }

        ExosuitEntity exo = this.menu.getExosuit();
        if (exo != null) {
            int barX = x + 20;
            int barY = y + 88;
            int barWidth = this.imageWidth - 40;
            int energy = exo.getEnergy();
            int maxEnergy = Math.max(1, exo.getMaxEnergy());
            int filled = Math.round(barWidth * Math.min(1.0F, energy / (float) maxEnergy));
            gui.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + 11, SLOT_EDGE);
            gui.fill(barX, barY, barX + barWidth, barY + 10, SLOT_BG);
            if (filled > 0) {
                gui.fill(barX, barY, barX + filled, barY + 10, ENERGY_FILL);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        ExosuitEntity exo = this.menu.getExosuit();
        Component header = exo != null
                ? Component.literal(exo.getTier().displayName())
                : this.title;
        gui.drawString(this.font, header, this.titleLabelX, this.titleLabelY, TEXT, false);
        gui.drawString(this.font, Component.translatable("container.opusvsexe.exo_weapon"), 20, 32, TEXT, false);
        gui.drawString(this.font, Component.translatable("container.opusvsexe.exo_module"), 100, 32, TEXT, false);
        if (exo != null) {
            gui.drawString(this.font, Component.translatable("hud.opusvsexe.exo.energy",
                    exo.getEnergy(), exo.getMaxEnergy()), 22, 90, 0xFF0A0A0A, false);
        }
        gui.drawString(this.font, this.playerInventoryTitle, this.titleLabelX, this.inventoryLabelY, TEXT, false);
    }
}

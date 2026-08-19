package com.opus.client.screen;

import com.opusvsexe.inventory.ExoInventoryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ExoInventoryScreen extends AbstractContainerScreen<ExoInventoryMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("opusvsexe", "textures/gui/exo_inventory.png");

    public ExoInventoryScreen(ExoInventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 210;
        this.inventoryLabelY = 121;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(this.font, this.playerInventoryTitle, this.titleLabelX, this.inventoryLabelY, 4210752, false);
    }
}
package com.opus.paradise.client.screen;

import com.opus.paradise.inventory.ParthenonForgeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class ParthenonForgeScreen extends AbstractContainerScreen<ParthenonForgeMenu> {
    private static final int MARBLE=0xFFF4E8C9;
    private static final int SHADOW=0xFF4D5965;
    private static final int GOLD=0xFFE0AF42;
    private static final int CYAN=0xFF53DCE5;

    public ParthenonForgeScreen(ParthenonForgeMenu menu,Inventory inventory,Component title){
        super(menu,inventory,title);imageWidth=176;imageHeight=166;inventoryLabelY=72;
    }

    @Override public void render(GuiGraphics gui,int mouseX,int mouseY,float partialTick){
        renderBackground(gui);super.render(gui,mouseX,mouseY,partialTick);renderTooltip(gui,mouseX,mouseY);
    }

    @Override protected void renderBg(GuiGraphics gui,float partialTick,int mouseX,int mouseY){
        int x=leftPos,y=topPos;
        gui.fill(x,y,x+imageWidth,y+imageHeight,SHADOW);
        gui.fill(x+2,y+2,x+imageWidth-2,y+imageHeight-2,MARBLE);
        gui.fill(x+4,y+4,x+imageWidth-4,y+12,0xFFB99036);
        for(int row=0;row<3;row++)for(int col=0;col<3;col++)slot(gui,x+29+col*18,y+16+row*18);
        slot(gui,x+123,y+34);
        gui.fill(x+91,y+38,x+113,y+42,GOLD);
        gui.fill(x+108,y+34,x+114,y+46,GOLD);
        gui.fill(x+111,y+37,x+117,y+43,GOLD);
        gui.fill(x+92,y+39,x+112,y+41,CYAN);
        gui.fill(x+7,y+82,x+169,y+164,0x5595A8AE);
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)slot(gui,x+7+col*18,y+83+row*18);
        for(int col=0;col<9;col++)slot(gui,x+7+col*18,y+141);
    }

    private static void slot(GuiGraphics gui,int x,int y){
        gui.fill(x,y,x+18,y+18,0xFF8C795A);gui.fill(x+1,y+1,x+17,y+17,0xFF263844);gui.fill(x+2,y+2,x+16,y+16,0xFF52636A);
    }

    @Override protected void renderLabels(GuiGraphics gui,int mouseX,int mouseY){
        gui.drawString(font,title,titleLabelX,titleLabelY,0x263844,false);
        gui.drawString(font,playerInventoryTitle,titleLabelX,inventoryLabelY,0x263844,false);
    }
}

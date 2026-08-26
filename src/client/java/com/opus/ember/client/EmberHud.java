package com.opus.ember.client;

import com.opus.ember.client.layer.EmberChargeLayer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class EmberHud {
    private static final int WIDTH = 64;
    private EmberHud() { }
    public static void init() { HudRenderCallback.EVENT.register(EmberHud::render); }

    private static void render(GuiGraphics gui, float tickDelta) {
        Minecraft client = Minecraft.getInstance();
        if (!EmberChargeLayer.isCharging() || client.options.hideGui || client.player == null || client.screen != null) return;
        int x = (client.getWindow().getGuiScaledWidth() - WIDTH) / 2;
        int y = client.getWindow().getGuiScaledHeight() - 62;
        float progress = EmberChargeLayer.progress(tickDelta);
        int fill = Math.round(WIDTH * progress);
        gui.fill(x - 2, y - 2, x + WIDTH + 2, y + 7, 0xC00E0A10);
        gui.fill(x, y, x + WIDTH, y + 5, 0xFF22141C);
        if (fill > 0) {
            gui.fill(x, y, x + fill, y + 5, 0xFFC21A1E);
            gui.fill(x, y, x + fill, y + 1, 0xFFFF9A1F);
        }
        Component label = Component.translatable("hud.opusvsexe.ember_charge", Math.round(progress * 100.0f));
        gui.drawString(client.font, label, (client.getWindow().getGuiScaledWidth() - client.font.width(label)) / 2,
            y - 11, 0xFFFFF6C9, true);
    }
}

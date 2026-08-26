package com.opus.fire.client;

import com.opus.fire.client.layer.FireChargeLayer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class FireHud {
    private static final int WIDTH = 64;
    private FireHud() { }
    public static void init() { HudRenderCallback.EVENT.register(FireHud::render); }

    private static void render(GuiGraphics gui, float tickDelta) {
        Minecraft client = Minecraft.getInstance();
        if (!FireChargeLayer.isCharging() || client.options.hideGui || client.player == null || client.screen != null) return;
        int x = (client.getWindow().getGuiScaledWidth() - WIDTH) / 2;
        int y = client.getWindow().getGuiScaledHeight() - 62;
        float progress = FireChargeLayer.progress(tickDelta);
        int fill = Math.round(WIDTH * progress);
        gui.fill(x - 2, y - 2, x + WIDTH + 2, y + 7, 0xC0160B0D);
        gui.fill(x, y, x + WIDTH, y + 5, 0xFF2B1B20);
        if (fill > 0) {
            gui.fill(x, y, x + fill, y + 5, 0xFFD33A1F);
            gui.fill(x, y, x + fill, y + 1, 0xFFFFB52A);
        }
        Component label = Component.translatable("hud.opusvsexe.fire_charge", Math.round(progress * 100.0f));
        gui.drawString(client.font, label, (client.getWindow().getGuiScaledWidth() - client.font.width(label)) / 2,
            y - 11, 0xFFFFF2B0, true);
    }
}

package com.opus.client.mixin;

import com.opus.client.gui.BloodMoonTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Кроваво-неоновый стиль ползунков (sliders) в настройках (задача 17).
 *
 * Слайдеры наследуют {@link AbstractWidget}, а не {@link AbstractButton}, поэтому
 * им нужен отдельный миксин на {@link AbstractSliderButton#renderWidget}.
 */
@Mixin(AbstractSliderButton.class)
public abstract class AbstractSliderButtonMixin {

    private static final int HANDLE_WIDTH = 8;

    @Shadow
    protected double value;

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void opusvsexe$render(GuiGraphics gui, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        AbstractSliderButton self = (AbstractSliderButton) (Object) this;
        boolean active = self.isActive();
        boolean hovered = active && self.isHoveredOrFocused();

        int x = self.getX();
        int y = self.getY();
        int w = self.getWidth();
        int h = self.getHeight();

        // трек
        gui.fill(x, y, x + w, y + h, BloodMoonTheme.PANEL_BG);

        // заполненная часть (слева до ползунка)
        int filled = (int) (this.value * (w - HANDLE_WIDTH));
        if (filled > 0) {
            int fill = active ? BloodMoonTheme.BLOOD : BloodMoonTheme.TEXT_MUTED;
            gui.fill(x, y + 2, x + filled + HANDLE_WIDTH / 2, y + h - 2, fill);
        }

        // ручка ползунка
        int hx = x + (int) (this.value * (w - HANDLE_WIDTH));
        int knob = active ? (hovered ? BloodMoonTheme.NEON : BloodMoonTheme.BLOOD_BRIGHT) : BloodMoonTheme.TEXT_MUTED;
        gui.fill(hx, y, hx + HANDLE_WIDTH, y + h, knob);

        // рамка
        int edge = hovered ? BloodMoonTheme.NEON : BloodMoonTheme.PANEL_EDGE;
        gui.fill(x, y, x + w, y + 1, edge);
        gui.fill(x, y + h - 1, x + w, y + h, edge);
        gui.fill(x, y, x + 1, y + h, edge);
        gui.fill(x + w - 1, y, x + w, y + h, edge);

        // текст
        int textColor = !active ? BloodMoonTheme.TEXT_MUTED : BloodMoonTheme.TEXT;
        gui.drawCenteredString(Minecraft.getInstance().font, self.getMessage(), x + w / 2, y + (h - 8) / 2, textColor);

        ci.cancel();
    }
}

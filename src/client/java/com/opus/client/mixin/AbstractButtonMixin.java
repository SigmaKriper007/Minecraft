package com.opus.client.mixin;

import com.opus.client.gui.BloodMoonTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Глобальный кроваво-неоновый стиль всех кнопок (задача 17).
 *
 * Перехватывает {@link AbstractButton#renderWidget} и рисует неоновую кнопку
 * вместо ванильной текстуры. Плавный hover через {@link #opusvsexe$hoverAmount}.
 * Кнопки-контролы (чекбоксы, слайдеры, cycle-кнопки) переопределяют renderWidget
 * сами и потому не затрагиваются.
 */
@Mixin(AbstractButton.class)
public abstract class AbstractButtonMixin {

    @Unique
    private float opusvsexe$hoverAmount = 0.0F;

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void opusvsexe$render(GuiGraphics gui, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        AbstractButton self = (AbstractButton) (Object) this;
        boolean active = self.isActive();
        boolean hovered = active && self.isHoveredOrFocused();
        float target = hovered ? 1.0F : 0.0F;
        this.opusvsexe$hoverAmount = BloodMoonTheme.approach(this.opusvsexe$hoverAmount, target, BloodMoonTheme.HOVER_SPEED);
        float h = this.opusvsexe$hoverAmount;

        int x = self.getX();
        int y = self.getY();
        int w = self.getWidth();
        int bh = self.getHeight();

        // корпус
        int body = active ? BloodMoonTheme.mix(BloodMoonTheme.PANEL_SOFT, BloodMoonTheme.PANEL_HOVER, h)
                : BloodMoonTheme.PANEL_BG;
        gui.fill(x, y, x + w, y + bh, body);

        // мягкое неоновое свечение при hover
        if (h > 0.01F) {
            int glow = (int) (0x2E * h);
            gui.fill(x - 2, y - 2, x + w + 2, y + bh + 2, (glow << 24) | (BloodMoonTheme.NEON & 0x00FFFFFF));
        }

        // рамка
        int edge = active ? BloodMoonTheme.mix(BloodMoonTheme.PANEL_EDGE, BloodMoonTheme.NEON, h)
                : BloodMoonTheme.PANEL_EDGE;
        gui.fill(x, y, x + w, y + 1, edge);
        gui.fill(x, y + bh - 1, x + w, y + bh, edge);
        gui.fill(x, y, x + 1, y + bh, edge);
        gui.fill(x + w - 1, y, x + w, y + bh, edge);

        // текст
        int textColor;
        if (!active) {
            textColor = BloodMoonTheme.TEXT_MUTED;
        } else {
            textColor = BloodMoonTheme.mix(BloodMoonTheme.TEXT, BloodMoonTheme.TEXT_PALE, h);
        }

        gui.drawCenteredString(Minecraft.getInstance().font, self.getMessage(), x + w / 2, y + (bh - 8) / 2, textColor);

        ci.cancel();
    }
}

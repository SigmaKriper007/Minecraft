package com.opus.client.mixin;

import com.opus.client.gui.BloodMoonScene;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Кроваво-неоновый фон для экрана «Одиночная игра» (задача 17).
 *
 * {@link SelectWorldScreen#render} не вызывает {@code renderBackground} сам,
 * поэтому фон рисуем в начале рендера.
 */
@Mixin(SelectWorldScreen.class)
public abstract class SelectWorldScreenMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void opusvsexe$bloodMoonBackground(GuiGraphics gui, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        BloodMoonScene.renderMenuBackground(gui, mc.getWindow().getGuiScaledWidth(),
                mc.getWindow().getGuiScaledHeight(), BloodMoonScene.time());
    }
}

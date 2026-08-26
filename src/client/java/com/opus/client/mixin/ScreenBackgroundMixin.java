package com.opus.client.mixin;

import com.opus.client.gui.BloodMoonScene;
import com.opus.client.gui.BloodMoonTheme;
import com.opus.client.gui.BloodMoonTitleScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Кроваво-неоновый фон для ВСЕХ меню (задача 17), кроме инвентарей и тайтла.
 *
 *  - {@code renderDirtBackground} — единственный источник «земляного» фона:
 *    заменяем его на анимированный кровавый фон. Это покрывает все меню
 *    (настройки, под-опции, выбор/создание мира, мультиплеер, realms).
 *  - {@code renderBackground} в игре — только пауза и настройки получают
 *    кровавую дымку; остальные оверлеи (чат/смерть/книга) остаются ванильными.
 *  - Инвентари ({@link AbstractContainerScreen}) и тайтл не трогаем.
 */
@Mixin(Screen.class)
public abstract class ScreenBackgroundMixin {

    @Inject(method = "renderDirtBackground(Lnet/minecraft/client/gui/GuiGraphics;)V",
            at = @At("HEAD"), cancellable = true)
    private void opusvsexe$bloodMoonDirt(GuiGraphics gui, CallbackInfo ci) {
        ci.cancel();
        Minecraft mc = Minecraft.getInstance();
        BloodMoonScene.renderMenuBackground(gui, mc.getWindow().getGuiScaledWidth(),
                mc.getWindow().getGuiScaledHeight(), BloodMoonScene.time());
    }

    @Inject(method = "renderBackground(Lnet/minecraft/client/gui/GuiGraphics;)V",
            at = @At("HEAD"), cancellable = true)
    private void opusvsexe$bloodMoonPauseTint(GuiGraphics gui, CallbackInfo ci) {
        Screen self = (Screen) (Object) this;
        if (self instanceof AbstractContainerScreen || self instanceof BloodMoonTitleScreen) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return; // в контексте меню всё уже покрывает renderDirtBackground
        }
        if (self instanceof PauseScreen || self instanceof OptionsScreen) {
            ci.cancel();
            int w = mc.getWindow().getGuiScaledWidth();
            int h = mc.getWindow().getGuiScaledHeight();
            gui.fillGradient(0, 0, w, h, 0xD0260A10, 0xD00A0306);
            gui.fill(0, 0, w, 1, BloodMoonTheme.withAlpha(BloodMoonTheme.BLOOD_BRIGHT, 90));
            gui.fill(0, h - 1, w, h, BloodMoonTheme.withAlpha(BloodMoonTheme.BLOOD_BRIGHT, 90));
        }
    }
}

package com.opus.client.mixin;

import com.opus.client.gui.BloodMoonTitleScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Заменяет ванильный {@link TitleScreen} на {@link BloodMoonTitleScreen} ещё ДО
 * установки экрана (задача 17, фикс «залипшего» экрана).
 *
 * Перехватывает аргумент {@link Minecraft#setScreen(Screen)} и подменяет
 * TitleScreen. Покрывает оба пути создания тайтла: явный
 * {@code setScreen(new TitleScreen(...))} и неявный {@code setScreen(null)}
 * при {@code level == null} (ваниль в этом случае сама создаёт TitleScreen).
 * Никакой ре-ентрантности внутри init() — навигация «меню → подменю → назад»
 * больше не оставляет «хвостов».
 */
@Mixin(Minecraft.class)
public abstract class TitleScreenReplaceMixin {

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void opusvsexe$swapTitleScreen(Screen screen, CallbackInfo ci) {
        Minecraft self = (Minecraft) (Object) this;
        boolean titleRequested = screen instanceof TitleScreen
                || (screen == null && self.level == null);
        if (titleRequested) {
            ci.cancel();
            self.setScreen(new BloodMoonTitleScreen());
        }
    }
}

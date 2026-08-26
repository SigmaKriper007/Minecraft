package com.opus.client.mixin;

import com.opus.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Проигрывает {@code menu_music} во ВСЕХ меню (задача 17).
 *
 * Пока {@code level == null} (главное меню, настройки, выбор/создание мира,
 * мультиплеер, realms) возвращаем кастомную музыку вместо ванильной — так
 * трек не обрывается при переходе в опции, а другие встроенные треки меню
 * не воспроизводятся. В игре музыка остаётся ванильной.
 */
@Mixin(Minecraft.class)
public class MinecraftMusicMixin {

    @Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
    private void opusvsexe$menuMusic(CallbackInfoReturnable<Music> cir) {
        Minecraft self = (Minecraft) (Object) this;
        if (self.level == null) {
            cir.setReturnValue(new Music(Holder.direct(ModSounds.MENU_MUSIC), 20, 600, true));
        }
    }
}

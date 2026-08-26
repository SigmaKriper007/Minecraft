package com.opus.client.mixin;

import com.opus.darkforest.client.DarkForestClient;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
public abstract class DarkForestMoonMixin {
    @Redirect(method="renderSky",at=@At(value="INVOKE",target="Lnet/minecraft/client/multiplayer/ClientLevel;getTimeOfDay(F)F"))
    private float opusvsexe$holdLocalMoon(ClientLevel level,float partialTick){return DarkForestClient.moonAngle(level.getTimeOfDay(partialTick),partialTick);}
}

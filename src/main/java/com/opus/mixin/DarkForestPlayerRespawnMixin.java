package com.opus.mixin;

import com.opus.darkforest.item.DarkForestPlayerState;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class DarkForestPlayerRespawnMixin {
    @Inject(method="restoreFrom",at=@At("TAIL"))private void opusvsexe$copyDarkForestState(ServerPlayer oldPlayer,boolean keepEverything,CallbackInfo ci){DarkForestPlayerState oldState=(DarkForestPlayerState)oldPlayer,newState=(DarkForestPlayerState)this;newState.opusvsexe$setDarkTeleportReady(oldState.opusvsexe$getDarkTeleportReady());newState.opusvsexe$setDarkSetEffects(false);newState.opusvsexe$setDarkHasteAmplifier(-1);}
}

package com.opus.mixin;

import com.opus.paradise.item.ParadisePlayerState;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ParadisePlayerRespawnMixin {
    @Inject(method="restoreFrom",at=@At("TAIL"))
    private void opusvsexe$copyRegaliaState(ServerPlayer oldPlayer,boolean keepEverything,CallbackInfo ci){
        ParadisePlayerState oldState=(ParadisePlayerState)oldPlayer;
        ParadisePlayerState newState=(ParadisePlayerState)this;
        newState.opusvsexe$setRegaliaHurricaneReady(oldState.opusvsexe$getRegaliaHurricaneReady());
        newState.opusvsexe$setRegaliaFlightGranted(false);
    }
}

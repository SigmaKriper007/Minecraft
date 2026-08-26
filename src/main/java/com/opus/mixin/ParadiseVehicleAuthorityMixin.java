package com.opus.mixin;

import com.opus.paradise.entity.ParadiseWyvernEntity;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ParadiseVehicleAuthorityMixin {
    @Shadow public ServerPlayer player;

    @Inject(method = "handleMoveVehicle", at = @At("HEAD"), cancellable = true)
    private void opusvsexe$keepWyvernMovementServerOwned(ServerboundMoveVehiclePacket packet, CallbackInfo ci) {
        if (player.getVehicle() instanceof ParadiseWyvernEntity) ci.cancel();
    }
}

package com.opus.client.mixin;

import com.opus.network.ModNetwork;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftAttackMixin {

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void opusvsexe$redirectAttackWhilePiloting(CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.player.getVehicle() instanceof ExosuitEntity) {
            ClientPlayNetworking.send(ModNetwork.EXO_ATTACK, PacketByteBufs.create());
            cir.setReturnValue(true);
        }
    }
}
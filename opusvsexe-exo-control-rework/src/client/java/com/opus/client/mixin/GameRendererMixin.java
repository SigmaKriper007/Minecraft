package com.opus.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Only one job left: hide the pilot's own arms while piloting.
 *
 * The old version also redirected GameRenderer#pick and rewrote the camera
 * position, which fought vanilla ray tracing and desynced the crosshair from
 * where the suit was actually looking. The pilot is now seated at the frame's
 * head height instead, so vanilla picking and the vanilla camera are already
 * correct and no hooks are needed.
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(method = "renderItemInHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/Camera;F)V",
            at = @At("HEAD"), cancellable = true)
    private void opusvsexe$hideHandsWhilePiloting(PoseStack poseStack, Camera camera, float partialTick,
                                                  CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.player.getVehicle() instanceof ExosuitEntity) {
            ci.cancel();
        }
    }
}

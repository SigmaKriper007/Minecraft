package com.opus.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(method = "renderItemInHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/Camera;F)V",
            at = @At("HEAD"), cancellable = true)
    private void opusvsexe$hideHandsWhilePiloting(PoseStack poseStack, Camera camera, float partialTick, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.player.getVehicle() instanceof ExosuitEntity) {
            ci.cancel();
        }
    }

    @Redirect(method = "pick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;pick(DFZ)Lnet/minecraft/world/phys/HitResult;"))
    private HitResult opusvsexe$pilotBlockPick(Entity entity, double distance, float partialTick, boolean includeFluids) {
        if (entity instanceof net.minecraft.client.player.AbstractClientPlayer player
                && player.getVehicle() instanceof ExosuitEntity exo) {
            return exo.pick(distance, partialTick, includeFluids);
        }
        return entity.pick(distance, partialTick, includeFluids);
    }

    @Redirect(method = "pick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;getEyePosition(F)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 opusvsexe$pilotEyePosition(Entity entity, float partialTick) {
        if (entity instanceof net.minecraft.client.player.AbstractClientPlayer player
                && player.getVehicle() instanceof ExosuitEntity exo) {
            return exo.getEyePosition(partialTick);
        }
        return entity.getEyePosition(partialTick);
    }
}
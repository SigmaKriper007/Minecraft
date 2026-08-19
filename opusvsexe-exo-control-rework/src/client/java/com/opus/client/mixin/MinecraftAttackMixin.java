package com.opus.client.mixin;

import com.opus.client.ExoInputHandler;
import com.opus.network.ModNetwork;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Left click while piloting swings the frame instead of the pilot's fist.
 *
 * Difference from the old version: a block under the crosshair still goes to
 * vanilla, so a pilot can mine. Attack packets are rate limited to the frame's
 * own attack cooldown instead of one packet per click.
 */
@Mixin(Minecraft.class)
public abstract class MinecraftAttackMixin {

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void opusvsexe$exoAttack(CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || !(minecraft.player.getVehicle() instanceof ExosuitEntity exo)) {
            return;
        }
        HitResult hit = minecraft.hitResult;
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            return;
        }
        if (ExoInputHandler.tryClientAttack(exo)) {
            ClientPlayNetworking.send(ModNetwork.EXO_ATTACK, PacketByteBufs.empty());
        }
        cir.setReturnValue(true);
    }
}

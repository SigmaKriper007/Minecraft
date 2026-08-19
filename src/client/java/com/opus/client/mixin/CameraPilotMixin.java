package com.opus.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.minecraft.client.Camera;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraPilotMixin {

    @Shadow
    protected abstract void setPosition(double x, double y, double z);

    @Shadow
    public abstract Vec3 getPosition();

    @Inject(method = "setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V",
            at = @At("TAIL"))
    private void opusvsexe$lookThroughExoEyes(BlockGetter level, Entity entity, boolean detached,
                                              boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        if (entity instanceof AbstractClientPlayer player && player.getVehicle() instanceof ExosuitEntity exo) {
            double exoEyeY = exo.getEyePosition(partialTick).y;
            double riderEyeY = player.getY() + player.getEyeHeight();
            Vec3 pos = this.getPosition();
            this.setPosition(pos.x, pos.y + (exoEyeY - riderEyeY), pos.z);
        }
    }
}
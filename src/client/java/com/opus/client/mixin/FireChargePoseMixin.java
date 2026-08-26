package com.opus.client.mixin;

import com.opus.fire.client.layer.FireChargeLayer;
import com.opus.ember.client.layer.EmberChargeLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class FireChargePoseMixin<T extends LivingEntity> {
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void opusvsexe$fireChargePose(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                                          float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(entity instanceof Player) || entity != Minecraft.getInstance().player
            || (!FireChargeLayer.isCharging() && !EmberChargeLayer.isCharging())) return;
        HumanoidModel<?> model = (HumanoidModel<?>) (Object) this;
        float progress = FireChargeLayer.isCharging() ? FireChargeLayer.progress(0.0f) : EmberChargeLayer.progress(0.0f);
        model.rightArm.xRot = -1.15f - progress * 0.35f;
        model.rightArm.yRot = -0.28f;
        model.leftArm.xRot = -0.65f - progress * 0.22f;
        model.leftArm.yRot = 0.42f;
    }
}

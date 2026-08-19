package com.opus.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.opus.registry.ModItems;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> {

    @Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    private void opusvsexe$skipVanillaOpusArmor(PoseStack poseStack, MultiBufferSource bufferSource,
                                                LivingEntity livingEntity, EquipmentSlot equipmentSlot,
                                                int packedLight, HumanoidModel<?> humanoidModel,
                                                CallbackInfo ci) {
        ItemStack stack = livingEntity.getItemBySlot(equipmentSlot);
        if ((equipmentSlot == EquipmentSlot.HEAD && (stack.is(ModItems.OPUS_HELMET) || stack.is(ModItems.SHADOW_HELMET)))
                || (equipmentSlot == EquipmentSlot.CHEST && (stack.is(ModItems.OPUS_CHESTPLATE) || stack.is(ModItems.SHADOW_CHESTPLATE)))
                || (equipmentSlot == EquipmentSlot.LEGS && (stack.is(ModItems.OPUS_LEGGINGS) || stack.is(ModItems.SHADOW_LEGGINGS)))
                || (equipmentSlot == EquipmentSlot.FEET && (stack.is(ModItems.OPUS_BOOTS) || stack.is(ModItems.SHADOW_BOOTS)))) {
            ci.cancel();
        }
    }
}
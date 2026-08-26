package com.opus.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.opus.fire.registry.FireItems;
import com.opus.ember.registry.EmberItems;
import com.opus.registry.ModItems;
import com.opus.paradise.registry.ParadiseItems;
import com.opus.darkforest.registry.DarkForestItems;
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
        if ((equipmentSlot == EquipmentSlot.HEAD && (stack.is(ModItems.OPUS_HELMET) || stack.is(ModItems.SHADOW_HELMET) || stack.is(FireItems.FIRE_HELMET) || stack.is(EmberItems.EMBER_HELMET)))
                || (equipmentSlot == EquipmentSlot.CHEST && (stack.is(ModItems.OPUS_CHESTPLATE) || stack.is(ModItems.SHADOW_CHESTPLATE) || stack.is(FireItems.FIRE_CHESTPLATE) || stack.is(EmberItems.EMBER_CHESTPLATE)))
                || (equipmentSlot == EquipmentSlot.LEGS && (stack.is(ModItems.OPUS_LEGGINGS) || stack.is(ModItems.SHADOW_LEGGINGS) || stack.is(FireItems.FIRE_LEGGINGS) || stack.is(EmberItems.EMBER_LEGGINGS)))
                || (equipmentSlot == EquipmentSlot.FEET && (stack.is(ModItems.OPUS_BOOTS) || stack.is(ModItems.SHADOW_BOOTS) || stack.is(FireItems.FIRE_BOOTS) || stack.is(EmberItems.EMBER_BOOTS)))
                || stack.is(ParadiseItems.PARTHENON_HELMET) || stack.is(ParadiseItems.PARTHENON_CHESTPLATE)
                || stack.is(ParadiseItems.PARTHENON_LEGGINGS) || stack.is(ParadiseItems.PARTHENON_BOOTS)
                || stack.is(DarkForestItems.BRIARWEAVE_HELMET) || stack.is(DarkForestItems.BRIARWEAVE_CHESTPLATE)
                || stack.is(DarkForestItems.BRIARWEAVE_LEGGINGS) || stack.is(DarkForestItems.BRIARWEAVE_BOOTS)
                || stack.is(DarkForestItems.DARK_FOREST_HELMET) || stack.is(DarkForestItems.DARK_FOREST_CHESTPLATE)
                || stack.is(DarkForestItems.DARK_FOREST_LEGGINGS) || stack.is(DarkForestItems.DARK_FOREST_BOOTS)) {
            ci.cancel();
        }
    }
}

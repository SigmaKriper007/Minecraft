package com.opus.fire.client.layer;

import com.opus.fire.client.model.FireHelmetModel;
import com.opus.fire.registry.FireItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;

/** Independent three-dimensional horned crown layer for the Four Veins set. */
public class FireHelmetLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

    private static final ResourceLocation HELM_TEXTURE =
        new ResourceLocation("opusvsexe:textures/fire/armor/fire_helmet_layer.png");

    private final FireHelmetModel<T> helmModel;

    public FireHelmetLayer(RenderLayerParent<T, M> parent, FireHelmetModel<T> helmModel) {
        super(parent);
        this.helmModel = helmModel;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (!(entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorItem armorItem)) {
            return;
        }
        if (armorItem != FireItems.FIRE_HELMET) {
            return;
        }
        this.helmModel.head().copyFrom(this.getParentModel().head);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(HELM_TEXTURE));
        this.helmModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY,
            1.0F, 1.0F, 1.0F, 1.0F);
    }
}
package com.opus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.opus.client.model.GreatHelmModel;
import com.opus.registry.ModItems;
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

public class OpusHelmetLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

    private static final ResourceLocation HELM_TEXTURE = new ResourceLocation("opusvsexe:textures/entity/great_helm.png");

    private final GreatHelmModel<T> helmModel;

    public OpusHelmetLayer(RenderLayerParent<T, M> parent, GreatHelmModel<T> helmModel) {
        super(parent);
        this.helmModel = helmModel;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorItem armorItem)) {
            return;
        }
        if (armorItem != ModItems.OPUS_HELMET) {
            return;
        }
        this.helmModel.head().copyFrom(this.getParentModel().head);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(HELM_TEXTURE));
        this.helmModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
    }
}
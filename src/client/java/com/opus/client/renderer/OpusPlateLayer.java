package com.opus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.opus.client.model.OpusPlateModel;
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

public class OpusPlateLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

    private static final ResourceLocation PLATE_TEXTURE = new ResourceLocation("opusvsexe:textures/entity/opus_plate.png");

    private final OpusPlateModel<T> plateModel;

    public OpusPlateLayer(RenderLayerParent<T, M> parent, OpusPlateModel<T> plateModel) {
        super(parent);
        this.plateModel = plateModel;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        boolean chest = entity.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.OPUS_CHESTPLATE);
        boolean legs = entity.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.OPUS_LEGGINGS);
        boolean boots = entity.getItemBySlot(EquipmentSlot.FEET).is(ModItems.OPUS_BOOTS);
        if (!chest && !legs && !boots) {
            return;
        }
        this.plateModel.body().copyFrom(this.getParentModel().body);
        this.plateModel.belt().copyFrom(this.getParentModel().body);
        this.plateModel.rightArm().copyFrom(this.getParentModel().rightArm);
        this.plateModel.leftArm().copyFrom(this.getParentModel().leftArm);
        this.plateModel.rightLeg().copyFrom(this.getParentModel().rightLeg);
        this.plateModel.leftLeg().copyFrom(this.getParentModel().leftLeg);

        this.plateModel.body().visible = chest || legs;
        this.plateModel.belt().visible = chest || legs;
        this.plateModel.rightArm().visible = chest;
        this.plateModel.leftArm().visible = chest;
        this.plateModel.rightLeg().visible = legs || boots;
        this.plateModel.leftLeg().visible = legs || boots;

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(PLATE_TEXTURE));
        this.plateModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
    }
}
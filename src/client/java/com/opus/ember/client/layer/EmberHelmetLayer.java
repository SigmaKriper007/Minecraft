package com.opus.ember.client.layer;

import com.opus.ember.client.model.EmberHelmetModel;
import com.opus.ember.registry.EmberItems;
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

/** Костяная маска с вуалью, сломанным нимбом и изогнутыми рогами. */
public class EmberHelmetLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

    private static final ResourceLocation HELM_TEXTURE =
        new ResourceLocation("opusvsexe:textures/ember/armor/ember_helmet_layer.png");

    private final EmberHelmetModel<T> helmModel;

    public EmberHelmetLayer(RenderLayerParent<T, M> parent, EmberHelmetModel<T> helmModel) {
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
        if (armorItem != EmberItems.EMBER_HELMET) {
            return;
        }
        this.helmModel.head().copyFrom(this.getParentModel().head);
        animate(ageInTicks + partialTick);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(HELM_TEXTURE));
        this.helmModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY,
            1.0F, 1.0F, 1.0F, 1.0F);
    }

    /** Нимб медленно вращается; рога дышат; при заряде — рога назад, нимб быстрее. */
    private void animate(float time) {
        boolean charging = EmberChargeLayer.isCharging();
        float sway = (float) Math.sin(time * 0.05f) * 0.04f;
        float charge = charging ? -0.18f : 0.0f;
        helmModel.hornL().xRot = -0.28f + sway + charge;
        helmModel.hornL().zRot = -0.52f - sway * 0.5f;
        helmModel.hornR().xRot = -0.28f - sway + charge;
        helmModel.hornR().zRot =  0.52f + sway * 0.5f;
        float spin = charging ? time * 0.14f : time * 0.05f;
        helmModel.halo().yRot = spin;
    }
}

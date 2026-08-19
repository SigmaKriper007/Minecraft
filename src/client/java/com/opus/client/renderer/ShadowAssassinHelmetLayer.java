package com.opus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.opus.client.model.ShadowAssassinHoodModel;
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

public class ShadowAssassinHelmetLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

    private static final ResourceLocation HOOD_TEXTURE = new ResourceLocation("opusvsexe:textures/entity/shadow_assassin_hood.png");

    private final ShadowAssassinHoodModel<T> hoodModel;

    public ShadowAssassinHelmetLayer(RenderLayerParent<T, M> parent, ShadowAssassinHoodModel<T> hoodModel) {
        super(parent);
        this.hoodModel = hoodModel;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorItem armorItem)) {
            return;
        }
        if (armorItem != ModItems.SHADOW_HELMET) {
            return;
        }
        this.hoodModel.head().copyFrom(this.getParentModel().head);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(HOOD_TEXTURE));
        this.hoodModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
    }
}

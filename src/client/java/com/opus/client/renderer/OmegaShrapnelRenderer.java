package com.opus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.opus.client.model.OmegaShrapnelModel;
import com.opus.entity.omega.OmegaShrapnelEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OmegaShrapnelRenderer extends GeoEntityRenderer<OmegaShrapnelEntity> {

    public OmegaShrapnelRenderer(EntityRendererProvider.Context context) {
        super(context, new OmegaShrapnelModel());
        this.shadowRadius = 0.0F;
    }

    @Override
    public void preRender(PoseStack poseStack, OmegaShrapnelEntity entity, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight,
                          int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        float s = com.opus.entity.omega.OmegaShrapnelEntity.RENDER_SCALE;
        poseStack.scale(s, s, s);
    }
}

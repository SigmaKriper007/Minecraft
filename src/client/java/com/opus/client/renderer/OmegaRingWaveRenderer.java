package com.opus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.opus.client.model.OmegaRingWaveModel;
import com.opus.entity.omega.OmegaRingWaveEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OmegaRingWaveRenderer extends GeoEntityRenderer<OmegaRingWaveEntity> {

    public OmegaRingWaveRenderer(EntityRendererProvider.Context context) {
        super(context, new OmegaRingWaveModel());
        this.shadowRadius = 0.0F;
    }

    @Override
    public void preRender(PoseStack poseStack, OmegaRingWaveEntity entity, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight,
                          int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        // GeckoLib делит юниты геометрии на 16; модель записана сразу в
        // «блочном» размере (радиус 204), поэтому скейл = 1.0.
        float s = entity.modelRadius() / OmegaRingWaveEntity.MODEL_MAX_RADIUS;
        poseStack.scale(s, s, s);
    }
}

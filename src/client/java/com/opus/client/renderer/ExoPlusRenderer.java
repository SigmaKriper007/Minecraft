package com.opus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.opus.client.model.ExoPlusModel;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ExoPlusRenderer extends GeoEntityRenderer<ExosuitEntity> {
    private static final float MODEL_HEIGHT = 4.5f;

    public ExoPlusRenderer(EntityRendererProvider.Context context) {
        super(context, new ExoPlusModel());
    }

    @Override
    public void preRender(PoseStack poseStack, ExosuitEntity entity, software.bernie.geckolib.cache.object.BakedGeoModel model,
                          net.minecraft.client.renderer.MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                          float red, float green, float blue, float alpha) {
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        float s = entity.getBbHeight() / MODEL_HEIGHT;
        poseStack.scale(s, s, s);
    }
}

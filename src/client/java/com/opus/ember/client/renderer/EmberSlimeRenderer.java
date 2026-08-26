package com.opus.ember.client.renderer;

import com.opus.ember.client.model.EmberSlimeModel;
import com.opus.ember.entity.EmberSlimeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EmberSlimeRenderer extends GeoEntityRenderer<EmberSlimeEntity> {

    public EmberSlimeRenderer(EntityRendererProvider.Context context) {
        super(context, new EmberSlimeModel());
        this.shadowRadius = 0.42f;
    }

    @Override
    public void preRender(PoseStack poseStack, EmberSlimeEntity entity, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                          float partialTick, int packedLight, int packedOverlay,
                          float red, float green, float blue, float alpha) {
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender,
            partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        float s = switch (entity.getSlimeSize()) {
            case 1 -> 0.61f; case 3 -> 1.48f; default -> 1.0f;
        };
        this.shadowRadius = 0.42f * s;
        poseStack.scale(s, s, s);
    }
}

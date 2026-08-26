package com.opus.fire.client.renderer;

import com.opus.fire.client.model.FireSlimeModel;
import com.opus.fire.entity.FireSlimeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FireSlimeRenderer extends GeoEntityRenderer<FireSlimeEntity> {

    public FireSlimeRenderer(EntityRendererProvider.Context context) {
        super(context, new FireSlimeModel());
        this.shadowRadius = 0.42f;
    }

    @Override
    public void preRender(PoseStack poseStack, FireSlimeEntity entity, BakedGeoModel model,
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
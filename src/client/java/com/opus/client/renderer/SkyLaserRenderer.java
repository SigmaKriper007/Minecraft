package com.opus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.opus.client.model.SkyLaserModel;
import com.opus.entity.SkyLaserEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class SkyLaserRenderer extends EntityRenderer<SkyLaserEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("opusvsexe:textures/entity/sky_laser_beam.png");

    private final SkyLaserModel model;

    public SkyLaserRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SkyLaserModel(context.bakeLayer(SkyLaserModel.LAYER_LOCATION));
    }

    @Override
    public void render(SkyLaserEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        this.model.setupAnim(entity, 0.0F, 0.0F, entity.tickCount + partialTick, 0.0F, 0.0F);

        float alpha = 1.0F;
        float t = (entity.tickCount + partialTick) / SkyLaserEntity.LIFETIME_TICKS;
        if (t > 0.75F) {
            alpha = 1.0F - (t - 0.75F) / 0.25F;
        }

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE));
        this.model.renderToBuffer(poseStack, vertexConsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
            1.0F, 1.0F, 1.0F, alpha);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SkyLaserEntity entity) {
        return TEXTURE;
    }
}

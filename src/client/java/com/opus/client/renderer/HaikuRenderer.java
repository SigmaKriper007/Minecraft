package com.opus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.opus.client.model.HaikuModel;
import com.opus.entity.haiku.HaikuMob;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Рендерер всех машин Haiku. Модель спроектирована на высоту 1.875 блока
 * и масштабируется пропорционально «физической» высоте сущности:
 * Husk 1.7 → ~0.9, Scout 0.8 → ~0.43, Omega 25 → ~13.3.
 */
public class HaikuRenderer extends GeoEntityRenderer<HaikuMob> {
    private static final float BASE_HEIGHT = 1.875f;

    public HaikuRenderer(EntityRendererProvider.Context context) {
        super(context, new HaikuModel());
    }

    @Override
    public void preRender(PoseStack poseStack, HaikuMob entity, BakedGeoModel model,
                          MultiBufferSource bufferSource,
                          com.mojang.blaze3d.vertex.VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight,
                          int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        float s = entity.getBbHeight() / BASE_HEIGHT;
        poseStack.scale(s, s, s);
    }
}
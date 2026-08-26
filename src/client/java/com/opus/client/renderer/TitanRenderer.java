package com.opus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.opus.client.model.TitanModel;
import com.opus.entity.haiku.Haiku5Entity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Рендерер Haiku-5 Titan Frame. Модель спроектирована на высоту 1.875 блока
 * и масштабируется под «физическую» высоту сущности (9.0 → ~4.8).
 */
public class TitanRenderer extends GeoEntityRenderer<Haiku5Entity> {
    private static final float BASE_HEIGHT = 1.875f;

    public TitanRenderer(EntityRendererProvider.Context context) {
        super(context, new TitanModel());
    }

    @Override
    public void preRender(PoseStack poseStack, Haiku5Entity entity, BakedGeoModel model,
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

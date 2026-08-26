package com.opus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.opus.client.model.HuskModel;
import com.opus.entity.haiku.Haiku15Entity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Рендерер Haiku 1.5 Husk. Модель спроектирована на высоту 1.875 блока
 * и масштабируется пропорционально высоте сущности (Husk 1.8 → ~0.96).
 */
public class HuskRenderer extends GeoEntityRenderer<Haiku15Entity> {
    private static final float BASE_HEIGHT = 1.875f;

    public HuskRenderer(EntityRendererProvider.Context context) {
        super(context, new HuskModel());
    }

    @Override
    public void preRender(PoseStack poseStack, Haiku15Entity entity, BakedGeoModel model,
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
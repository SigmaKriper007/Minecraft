package com.opus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.opus.client.model.EnforcerModel;
import com.opus.entity.haiku.Haiku3Entity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Рендерер Haiku-3 Enforcer. Модель спроектирована на высоту 1.875 блока
 * и масштабируется под «физическую» высоту сущности (2.8 → ~1.49).
 */
public class EnforcerRenderer extends GeoEntityRenderer<Haiku3Entity> {
    private static final float BASE_HEIGHT = 1.875f;

    public EnforcerRenderer(EntityRendererProvider.Context context) {
        super(context, new EnforcerModel());
    }

    @Override
    public void preRender(PoseStack poseStack, Haiku3Entity entity, BakedGeoModel model,
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

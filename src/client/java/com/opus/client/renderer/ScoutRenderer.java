package com.opus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.opus.client.model.ScoutModel;
import com.opus.entity.haiku.Haiku2Entity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Рендерер Haiku-2 Scout. Модель компактная (~15px, ≈0.94 блока в дизайне)
 * и масштабируется под «физическую» высоту сущности (0.8 → ~0.85).
 */
public class ScoutRenderer extends GeoEntityRenderer<Haiku2Entity> {
    private static final float BASE_HEIGHT = 0.9375f;

    public ScoutRenderer(EntityRendererProvider.Context context) {
        super(context, new ScoutModel());
    }

    @Override
    public void preRender(PoseStack poseStack, Haiku2Entity entity, BakedGeoModel model,
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
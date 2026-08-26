package com.opus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.opus.client.model.OmegaSkyLaserModel;
import com.opus.entity.omega.OmegaSkyLaserEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OmegaSkyLaserRenderer extends GeoEntityRenderer<OmegaSkyLaserEntity> {

    public OmegaSkyLaserRenderer(EntityRendererProvider.Context context) {
        super(context, new OmegaSkyLaserModel());
        this.shadowRadius = 0.0F;
    }

    @Override
    public void preRender(PoseStack poseStack, OmegaSkyLaserEntity entity, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight,
                          int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        // GeckoLib делит юниты геометрии на 16; модель записана в натуральную
        // величину (метка-звезда 5× прежней, столб 60×60×80 блоков) → скейл 1.0.
        poseStack.scale(1.0F, 1.0F, 1.0F);
    }
}

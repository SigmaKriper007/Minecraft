package com.opus.paradise.client.renderer;

import com.opus.paradise.client.model.ParadiseWyvernModel;
import com.opus.paradise.entity.ParadiseWyvernEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class ParadiseWyvernRenderer extends GeoEntityRenderer<ParadiseWyvernEntity> {
    public ParadiseWyvernRenderer(EntityRendererProvider.Context context) {
        super(context, new ParadiseWyvernModel());
        shadowRadius = 0.75F;
    }

    @Override
    public void render(ParadiseWyvernEntity entity, float yaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight) {
        if (entity.isBaby()) poseStack.scale(0.58F, 0.58F, 0.58F);
        super.render(entity, yaw, partialTick, poseStack, buffers, packedLight);
    }
}

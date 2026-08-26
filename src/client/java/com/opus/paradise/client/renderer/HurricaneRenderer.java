package com.opus.paradise.client.renderer;

import com.opus.paradise.client.model.HurricaneModel;
import com.opus.paradise.entity.HurricaneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class HurricaneRenderer extends GeoEntityRenderer<HurricaneEntity> {
    public HurricaneRenderer(EntityRendererProvider.Context context) {
        super(context, new HurricaneModel());
        shadowRadius = 0.0F;
    }

    @Override
    public void render(HurricaneEntity entity, float yaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight) {
        float age = entity.tickCount + partialTick;
        float phaseScale = age < HurricaneEntity.TELEGRAPH_END
            ? 0.15F + 0.85F * age / HurricaneEntity.TELEGRAPH_END
            : age < HurricaneEntity.ACTIVE_END ? 1.0F
            : Math.max(0.12F, 1.0F - (age - HurricaneEntity.ACTIVE_END) / 20.0F);
        // subtle churn: the whole storm swells and settles with the vortex beat
        float churn = 1.0F + 0.02F * Mth.sin(age * 0.55F);
        float scale = 5.0F * phaseScale * churn;
        poseStack.scale(scale, scale, scale);
        super.render(entity, yaw, partialTick, poseStack, buffers, packedLight);
    }
}

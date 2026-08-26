package com.opus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.opus.client.model.PunchShockwaveModel;
import com.opus.entity.PunchShockwaveEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PunchShockwaveRenderer extends GeoEntityRenderer<PunchShockwaveEntity> {
    public PunchShockwaveRenderer(EntityRendererProvider.Context context) {
        super(context, new PunchShockwaveModel());
        this.shadowRadius = 0.0F;
    }

    @Override
    protected int getBlockLightLevel(PunchShockwaveEntity entity, BlockPos position) {
        return 15;
    }

    @Override
    public void preRender(PoseStack poseStack, PunchShockwaveEntity entity, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                          float partialTick, int packedLight, int packedOverlay,
                          float red, float green, float blue, float alpha) {
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        float tierScale = 0.8F + entity.getTierIndex() * 0.12F;
        float progress = entity.getVisualProgress(partialTick);
        float pulse = tierScale * (0.55F + Mth.sin(progress * Mth.PI) * 0.75F);
        poseStack.scale(pulse, pulse, pulse);
    }
}

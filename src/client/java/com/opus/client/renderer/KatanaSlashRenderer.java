package com.opus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.opus.client.model.KatanaSlashModel;
import com.opus.entity.KatanaSlashEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KatanaSlashRenderer extends GeoEntityRenderer<KatanaSlashEntity> {
    public KatanaSlashRenderer(EntityRendererProvider.Context context) {
        super(context, new KatanaSlashModel());
        this.shadowRadius = 0.0F;
    }

    @Override
    protected int getBlockLightLevel(KatanaSlashEntity entity, BlockPos position) {
        return 15;
    }

    @Override
    public void preRender(PoseStack poseStack, KatanaSlashEntity entity, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                          float partialTick, int packedLight, int packedOverlay,
                          float red, float green, float blue, float alpha) {
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        float scale = entity.getVariant() == KatanaSlashEntity.GOLD ? 1.55F
                : entity.getVariant() == KatanaSlashEntity.REFINED ? 0.72F : 1.0F;
        poseStack.scale(scale, scale, scale);
    }
}

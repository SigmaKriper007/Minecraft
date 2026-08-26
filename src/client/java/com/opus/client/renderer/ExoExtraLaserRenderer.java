package com.opus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.opus.client.model.ExoExtraLaserModel;
import com.opus.entity.ExtraLaserBeamEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * The Extra Laser is the heavy blaster beam scaled 2.5x on the cross-section,
 * reusing the same geometry and blast animation with a gold texture.
 */
public class ExoExtraLaserRenderer extends GeoEntityRenderer<ExtraLaserBeamEntity> {

    private static final Vec3 BEAM_AXIS = new Vec3(0.0D, 0.0D, -1.0D);
    private static final float WIDTH_SCALE = 2.5F;

    public ExoExtraLaserRenderer(EntityRendererProvider.Context context) {
        super(context, new ExoExtraLaserModel());
    }

    @Override
    public void preRender(PoseStack poseStack, ExtraLaserBeamEntity entity, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                          float red, float green, float blue, float alpha) {
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick,
            packedLight, packedOverlay, red, green, blue, alpha);

        Vec3 dir = entity.getSyncedDirection();
        if (dir.lengthSqr() < 1.0E-4D) {
            dir = entity.getDeltaMovement();
        }
        if (dir.lengthSqr() < 1.0E-4D) {
            float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
            float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
            poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
            poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
            poseStack.scale(WIDTH_SCALE, WIDTH_SCALE, 1.0F);
            return;
        }

        double dot = Mth.clamp(BEAM_AXIS.dot(dir), -1.0D, 1.0D);
        if (dot > 0.99999D) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.scale(WIDTH_SCALE, WIDTH_SCALE, 1.0F);
            return;
        }
        if (dot < -0.99999D) {
            poseStack.scale(WIDTH_SCALE, WIDTH_SCALE, 1.0F);
            return;
        }
        Vec3 target = dir.scale(-1.0D);
        Vec3 axis = BEAM_AXIS.cross(target).normalize();
        float angle = (float) Math.acos(Mth.clamp(BEAM_AXIS.dot(target), -1.0D, 1.0D));
        poseStack.mulPose(new Quaternionf().rotationAxis(angle, (float) axis.x, (float) axis.y, (float) axis.z));
        poseStack.scale(WIDTH_SCALE, WIDTH_SCALE, 1.0F);
    }
}

package com.opus.fire.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public final class FireChargeLayer<T extends Player, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("opusvsexe:textures/fire/entity/fireball.png");
    private static int totalTicks = 16;
    private static int ticksLeft;

    public FireChargeLayer(RenderLayerParent<T, M> parent) { super(parent); }
    public static void startCharge() { startCharge(16); }
    public static void startCharge(int duration) { totalTicks = Math.max(1, duration); ticksLeft = totalTicks; }
    public static boolean isCharging() { return ticksLeft > 0; }
    public static void tickCharge() { if (ticksLeft > 0) ticksLeft--; }
    public static float progress(float partialTick) {
        return isCharging() ? Math.min(1.0f, (totalTicks - ticksLeft + partialTick) / totalTicks) : 0.0f;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (!isCharging() || entity != Minecraft.getInstance().player) return;
        float progress = progress(partialTick);
        float pulse = 0.88f + (float) Math.sin((ageInTicks + partialTick) * 1.6f) * 0.12f;
        float scale = (0.20f + progress * 0.72f) * pulse;
        ModelPart rightArm = getParentModel().rightArm;
        poseStack.pushPose();
        rightArm.translateAndRotate(poseStack);
        poseStack.translate(0.0F, 0.72F, -0.35F);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotation((ageInTicks + partialTick) * 0.18f));
        poseStack.scale(scale, scale, scale);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        float s = 0.28f;
        quad(pose, consumer, -s,-s,s, s,-s,s, s,s,s, -s,s,s);
        quad(pose, consumer, -s,-s,-s, -s,s,-s, s,s,-s, s,-s,-s);
        quad(pose, consumer, s,-s,-s, s,s,-s, s,s,s, s,-s,s);
        quad(pose, consumer, -s,-s,s, s,-s,s, s,-s,-s, -s,-s,-s);
        quad(pose, consumer, -s,s,-s, -s,s,s, s,s,s, s,s,-s);
        quad(pose, consumer, -s,-s,-s, s,-s,-s, s,-s,s, -s,-s,s);
        poseStack.popPose();
    }

    private static void quad(PoseStack.Pose pose, VertexConsumer out, float x0,float y0,float z0,
                             float x1,float y1,float z1,float x2,float y2,float z2,float x3,float y3,float z3) {
        vertex(pose,out,x0,y0,z0,0,0); vertex(pose,out,x1,y1,z1,1,0);
        vertex(pose,out,x2,y2,z2,1,1); vertex(pose,out,x3,y3,z3,0,1);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer out, float x,float y,float z,float u,float v) {
        out.vertex(pose.pose(),x,y,z).color(255,181,42,220).uv(u,v).overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(240,240).normal(pose.normal(),0,1,0).endVertex();
    }
}

package com.opus.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.opus.entity.LaserEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class LaserModel extends EntityModel<LaserEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation("opusvsexe", "laser_mega"), "main");

    private static final float UNITS_PER_BLOCK = 16.0F;
    private static final float BEAM_BASE_HEIGHT = 26.0F;
    private static final float BEAM_BASE_WIDTH = 16.0F;
    private static final float MAX_SCALE_Y = LaserEntity.MAX_HEIGHT_BLOCKS * UNITS_PER_BLOCK / BEAM_BASE_HEIGHT;
    private static final float MAX_SCALE_XZ = LaserEntity.MAX_DIAMETER_BLOCKS * UNITS_PER_BLOCK / BEAM_BASE_WIDTH;
    private static final float BASE_SPIN = (float) Math.toRadians(-22.5D);
    private static final float SPIN_AMOUNT = (float) Math.toRadians(-125.0D);

    private final ModelPart bone;

    public LaserModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();
        PartDefinition bone = root.addOrReplaceChild("bone",
            CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        bone.addOrReplaceChild("blade_0", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-8.0F, 0.0F, -1.0F, 16.0F, 26.0F, 2.0F), PartPose.ZERO);
        bone.addOrReplaceChild("blade_90", CubeListBuilder.create()
            .texOffs(0, 28).addBox(-8.0F, 0.0F, -1.0F, 16.0F, 26.0F, 2.0F),
            PartPose.rotation(0.0F, 1.5707964F, 0.0F));
        bone.addOrReplaceChild("blade_47", CubeListBuilder.create()
            .texOffs(36, 0).addBox(-8.0F, 0.0F, -1.0F, 16.0F, 26.0F, 2.0F),
            PartPose.rotation(0.0F, 0.8290314F, 0.0F));
        bone.addOrReplaceChild("blade_137", CubeListBuilder.create()
            .texOffs(36, 28).addBox(-8.0F, 0.0F, -1.0F, 16.0F, 26.0F, 2.0F),
            PartPose.rotation(0.0F, 2.3998317F, 0.0F));

        return LayerDefinition.create(meshDefinition, 128, 128);
    }

    @Override
    public void setupAnim(LaserEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        float duration = LaserEntity.LIFETIME_TICKS;
        float grow = Math.min(ageInTicks / (duration * LaserEntity.GROW_FRACTION), 1.0F);
        float growEase = grow * grow * (3.0F - 2.0F * grow);
        float t = Math.min(ageInTicks / duration, 1.0F);
        float closeEase = Math.max(0.0F, (t - LaserEntity.GROW_FRACTION) / (1.0F - LaserEntity.GROW_FRACTION));
        closeEase = closeEase * closeEase * (3.0F - 2.0F * closeEase);

        this.bone.yRot = BASE_SPIN + SPIN_AMOUNT * growEase;
        this.bone.yScale = MAX_SCALE_Y * growEase;
        float xz = MAX_SCALE_XZ * (1.0F - closeEase);
        this.bone.xScale = xz;
        this.bone.zScale = xz;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
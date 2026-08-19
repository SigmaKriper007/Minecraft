package com.opus.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class OpusPlateModel<T extends LivingEntity> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation("opusvsexe", "opus_plate"), "main");

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart belt;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public OpusPlateModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.belt = root.getChild("belt");
        this.rightArm = root.getChild("rightArm");
        this.leftArm = root.getChild("leftArm");
        this.rightLeg = root.getChild("rightLeg");
        this.leftLeg = root.getChild("leftLeg");
    }

    public ModelPart body() { return this.body; }
    public ModelPart belt() { return this.belt; }
    public ModelPart rightArm() { return this.rightArm; }
    public ModelPart leftArm() { return this.leftArm; }
    public ModelPart rightLeg() { return this.rightLeg; }
    public ModelPart leftLeg() { return this.leftLeg; }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition belt = root.addOrReplaceChild("belt", CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition rightArm = root.addOrReplaceChild("rightArm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        PartDefinition leftArm = root.addOrReplaceChild("leftArm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));
        PartDefinition rightLeg = root.addOrReplaceChild("rightLeg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
        PartDefinition leftLeg = root.addOrReplaceChild("leftLeg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));

            addBox(body, "side_l", CubeListBuilder.create().texOffs(4, 4).addBox(-5F, 0F, -2F, 1F, 9F, 4F));
            addBox(body, "side_r", CubeListBuilder.create().texOffs(18, 4).addBox(4F, 0F, -2F, 1F, 9F, 4F));
            addBox(body, "back", CubeListBuilder.create().texOffs(32, 4).addBox(-4.5F, 0F, 2F, 9F, 9F, 1F));
            addBox(body, "pec", CubeListBuilder.create().texOffs(50, 45).addBox(-4.5F, 0.1F, -3F, 9F, 4F, 1F));
            addBox(body, "band_v_top", CubeListBuilder.create().texOffs(74, 45).addBox(-1.5F, 0F, -3.75F, 3F, 3.5F, 1F));
            addBox(body, "band_v_bot", CubeListBuilder.create().texOffs(86, 45).addBox(-1.5F, 5.5F, -3.75F, 3F, 3.5F, 1F));
            addBox(body, "belly", CubeListBuilder.create().texOffs(32, 64).addBox(-4F, 6.1F, -3F, 8F, 3F, 1F));
            addBox(body, "band_h", CubeListBuilder.create().texOffs(68, 64).addBox(-4.25F, 3.5F, -3.75F, 8.5F, 2F, 1F));

            addBox(rightArm, "gauntlet", CubeListBuilder.create().texOffs(56, 4).addBox(-3.5F, 9.1F, -2.9F, 4.5F, 3F, 5.8F));
            addBox(rightArm, "pauldron_1", CubeListBuilder.create().texOffs(36, 21).addBox(-4.1F, -3.5F, -3.1F, 5F, 2F, 6F));
            addBox(rightArm, "pauldron_2", CubeListBuilder.create().texOffs(30, 33).addBox(-3.5F, -1.5F, -2.5F, 4F, 2F, 5F));
            addBox(rightArm, "pauldron_3", CubeListBuilder.create().texOffs(74, 33).addBox(-3F, 0.5F, -2F, 3F, 2F, 4F));
            addBox(rightArm, "upper_front", CubeListBuilder.create().texOffs(92, 33).addBox(-3F, 0.5F, -3F, 3.5F, 4.5F, 1F));
            addBox(rightArm, "upper_back", CubeListBuilder.create().texOffs(106, 33).addBox(-3F, 0.5F, 2.1F, 3.5F, 4.5F, 1F));
            addBox(rightArm, "forearm_front", CubeListBuilder.create().texOffs(98, 45).addBox(-2.75F, 6.5F, -3F, 3.5F, 3.5F, 1F));
            addBox(rightArm, "forearm_back", CubeListBuilder.create().texOffs(112, 45).addBox(-2.75F, 6.5F, 2.1F, 3.5F, 3.5F, 1F));
            addBox(rightArm, "elbow", CubeListBuilder.create().texOffs(92, 64).addBox(-3.75F, 4.5F, -3.5F, 4F, 2F, 1F));
            addBox(rightArm, "knuckle", CubeListBuilder.create().texOffs(106, 64).addBox(-2.5F, 10F, -4F, 3F, 2F, 1F));

            addBox(leftArm, "gauntlet", CubeListBuilder.create().texOffs(82, 4).addBox(-1F, 9.1F, -2.9F, 4.5F, 3F, 5.8F));
            addBox(leftArm, "pauldron_1", CubeListBuilder.create().texOffs(62, 21).addBox(-0.9F, -3.5F, -3.1F, 5F, 2F, 6F));
            addBox(leftArm, "pauldron_2", CubeListBuilder.create().texOffs(52, 33).addBox(-0.5F, -1.5F, -2.5F, 4F, 2F, 5F));
            addBox(leftArm, "pauldron_3", CubeListBuilder.create().texOffs(4, 45).addBox(0F, 0.5F, -2F, 3F, 2F, 4F));
            addBox(leftArm, "upper_front", CubeListBuilder.create().texOffs(22, 45).addBox(-0.5F, 0.5F, -3F, 3.5F, 4.5F, 1F));
            addBox(leftArm, "upper_back", CubeListBuilder.create().texOffs(36, 45).addBox(-0.5F, 0.5F, 2.1F, 3.5F, 4.5F, 1F));
            addBox(leftArm, "forearm_front", CubeListBuilder.create().texOffs(4, 55).addBox(-0.75F, 6.5F, -3F, 3.5F, 3.5F, 1F));
            addBox(leftArm, "forearm_back", CubeListBuilder.create().texOffs(18, 55).addBox(-0.75F, 6.5F, 2.1F, 3.5F, 3.5F, 1F));
            addBox(leftArm, "elbow", CubeListBuilder.create().texOffs(4, 73).addBox(-0.25F, 4.5F, -3.5F, 4F, 2F, 1F));
            addBox(leftArm, "knuckle", CubeListBuilder.create().texOffs(18, 73).addBox(-0.5F, 10F, -4F, 3F, 2F, 1F));

            addBox(belt, "belt_wrap", CubeListBuilder.create().texOffs(4, 21).addBox(-4.5F, 9.05F, -2.5F, 9F, 3F, 5F));
            addBox(belt, "belt_buckle", CubeListBuilder.create().texOffs(54, 64).addBox(-2F, 9.05F, -3.5F, 4F, 3F, 1F));

            addBox(rightLeg, "boot", CubeListBuilder.create().texOffs(88, 21).addBox(-1.9F, 10F, -3F, 4.75F, 2F, 6F));
            addBox(rightLeg, "thigh_front", CubeListBuilder.create().texOffs(32, 55).addBox(-1.75F, 2F, -2.75F, 3.5F, 4F, 1F));
            addBox(rightLeg, "thigh_back", CubeListBuilder.create().texOffs(60, 55).addBox(-1.75F, 2F, 1.75F, 3.5F, 4F, 1F));
            addBox(rightLeg, "shin_front", CubeListBuilder.create().texOffs(88, 55).addBox(-1.75F, 7.5F, -2.75F, 3.5F, 3.5F, 1F));
            addBox(rightLeg, "shin_back", CubeListBuilder.create().texOffs(4, 64).addBox(-1.75F, 7.5F, 1.75F, 3.5F, 3.5F, 1F));
            addBox(rightLeg, "hip", CubeListBuilder.create().texOffs(30, 73).addBox(-1.8F, 0F, -3F, 4.5F, 2F, 1F));
            addBox(rightLeg, "knee", CubeListBuilder.create().texOffs(62, 73).addBox(-1.9F, 5.5F, -3.5F, 4.75F, 2F, 1F));

            addBox(leftLeg, "boot", CubeListBuilder.create().texOffs(4, 33).addBox(-2.85F, 10F, -3F, 4.75F, 2F, 6F));
            addBox(leftLeg, "thigh_front", CubeListBuilder.create().texOffs(46, 55).addBox(-1.75F, 2F, -2.75F, 3.5F, 4F, 1F));
            addBox(leftLeg, "thigh_back", CubeListBuilder.create().texOffs(74, 55).addBox(-1.75F, 2F, 1.75F, 3.5F, 4F, 1F));
            addBox(leftLeg, "shin_front", CubeListBuilder.create().texOffs(102, 55).addBox(-1.75F, 7.5F, -2.75F, 3.5F, 3.5F, 1F));
            addBox(leftLeg, "shin_back", CubeListBuilder.create().texOffs(18, 64).addBox(-1.75F, 7.5F, 1.75F, 3.5F, 3.5F, 1F));
            addBox(leftLeg, "hip", CubeListBuilder.create().texOffs(46, 73).addBox(-2.7F, 0F, -3F, 4.5F, 2F, 1F));
            addBox(leftLeg, "knee", CubeListBuilder.create().texOffs(78, 73).addBox(-2.85F, 5.5F, -3.5F, 4.75F, 2F, 1F));

        return LayerDefinition.create(meshDefinition, 128, 128);
    }

    private static void addBox(PartDefinition part, String name, CubeListBuilder builder) {
        part.addOrReplaceChild(name, builder, PartPose.ZERO);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

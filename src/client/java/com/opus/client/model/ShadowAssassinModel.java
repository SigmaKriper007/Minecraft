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

public class ShadowAssassinModel<T extends LivingEntity> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation("opusvsexe", "shadow_assassin"), "main");

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public ShadowAssassinModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.rightArm = root.getChild("rightArm");
        this.leftArm = root.getChild("leftArm");
        this.rightLeg = root.getChild("rightLeg");
        this.leftLeg = root.getChild("leftLeg");
    }

    public ModelPart body() { return this.body; }
    public ModelPart rightArm() { return this.rightArm; }
    public ModelPart leftArm() { return this.leftArm; }
    public ModelPart rightLeg() { return this.rightLeg; }
    public ModelPart leftLeg() { return this.leftLeg; }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition rightArm = root.addOrReplaceChild("rightArm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        PartDefinition leftArm = root.addOrReplaceChild("leftArm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));
        PartDefinition rightLeg = root.addOrReplaceChild("rightLeg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
        PartDefinition leftLeg = root.addOrReplaceChild("leftLeg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        addChild(body, "collar_front", CubeListBuilder.create()
                .texOffs(74, 67).addBox(-4.5F, -1.0F, -3.4F, 9.0F, 2.4F, 0.6F),
                PartPose.ZERO);
        addChild(body, "collar_back", CubeListBuilder.create()
                .texOffs(25, 67).addBox(-4.5F, -1.6F, 2.9F, 9.0F, 3.0F, 0.6F),
                PartPose.ZERO);
        addChild(body, "collar_left", CubeListBuilder.create()
                .texOffs(20, 21).addBox(-4.7F, -1.3F, -3.0F, 0.6F, 2.8F, 6.0F),
                PartPose.ZERO);
        addChild(body, "collar_right", CubeListBuilder.create()
                .texOffs(34, 21).addBox(4.1F, -1.3F, -3.0F, 0.6F, 2.8F, 6.0F),
                PartPose.ZERO);
        addChild(body, "collar_inner", CubeListBuilder.create()
                .texOffs(94, 67).addBox(-3.4F, -0.6F, -3.25F, 6.8F, 2.0F, 0.5F),
                PartPose.ZERO);
        addChild(body, "chest_base_front", CubeListBuilder.create()
                .texOffs(55, 60).addBox(-4.6F, 1.5F, -2.4F, 9.2F, 4.3F, 0.6F),
                PartPose.ZERO);
        addChild(body, "chest_base_side_l", CubeListBuilder.create()
                .texOffs(84, 0).addBox(-5.0F, 1.5F, -2.0F, 0.5F, 5.8F, 4.2F),
                PartPose.ZERO);
        addChild(body, "chest_base_side_r", CubeListBuilder.create()
                .texOffs(94, 0).addBox(4.5F, 1.5F, -2.0F, 0.5F, 5.8F, 4.2F),
                PartPose.ZERO);
        addChild(body, "chest_base_back", CubeListBuilder.create()
                .texOffs(73, 46).addBox(-4.5F, 1.5F, 2.1F, 9.0F, 6.4F, 0.5F),
                PartPose.ZERO);
        addChild(body, "chest_mid", CubeListBuilder.create()
                .texOffs(35, 60).addBox(-4.1F, 1.8F, -1.7F, 8.2F, 3.8F, 1.2F),
                PartPose.ZERO);
        addChild(body, "chest_plate_left", CubeListBuilder.create()
                .texOffs(0, 67).addBox(-4.4F, 0.3F, -3.75F, 4.2F, 3.4F, 0.5F),
                PartPose.ZERO);
        addChild(body, "chest_trim_left_red", CubeListBuilder.create()
                .texOffs(21, 71).addBox(-4.4F, 3.7F, -3.75F, 4.2F, 0.5F, 0.5F),
                PartPose.ZERO);
        addChild(body, "chest_right_cloth", CubeListBuilder.create()
                .texOffs(45, 67).addBox(0.7F, 0.6F, -3.5F, 3.8F, 3.1F, 0.5F),
                PartPose.ZERO);
        addChild(body, "chest_trim_right_red", CubeListBuilder.create()
                .texOffs(31, 71).addBox(0.7F, 0.55F, -3.5F, 3.8F, 0.5F, 0.5F),
                PartPose.ZERO);
        addChild(body, "chest_strap", CubeListBuilder.create()
                .texOffs(0, 71).addBox(-4.8F, 0.6F, -4.0F, 9.6F, 0.7F, 0.5F),
                PartPose.rotation(0.0F, 0.0F, -0.5F));
        addChild(body, "chest_emblem", CubeListBuilder.create()
                .texOffs(55, 67).addBox(-1.7F, 0.9F, -4.45F, 3.4F, 3.0F, 0.6F),
                PartPose.ZERO);
        addChild(body, "belt_main", CubeListBuilder.create()
                .texOffs(47, 12).addBox(-5.2F, 6.0F, -3.0F, 10.4F, 3.0F, 6.0F),
                PartPose.ZERO);
        addChild(body, "belt_buckle", CubeListBuilder.create()
                .texOffs(64, 67).addBox(-2.0F, 6.3F, -3.6F, 4.0F, 2.4F, 0.7F),
                PartPose.ZERO);
        addChild(body, "belt_ring_l", CubeListBuilder.create()
                .texOffs(116, 38).addBox(-6.4F, 6.9F, -2.3F, 0.5F, 1.4F, 4.6F),
                PartPose.ZERO);
        addChild(body, "belt_ring_r", CubeListBuilder.create()
                .texOffs(116, 46).addBox(5.9F, 6.9F, -2.3F, 0.5F, 1.4F, 4.6F),
                PartPose.ZERO);
        addChild(body, "belt_strap_l", CubeListBuilder.create()
                .texOffs(121, 12).addBox(-5.5F, 9.0F, -2.0F, 0.6F, 3.6F, 0.5F),
                PartPose.ZERO);
        addChild(body, "belt_strap_r", CubeListBuilder.create()
                .texOffs(124, 12).addBox(4.9F, 9.0F, -2.0F, 0.6F, 2.8F, 0.5F),
                PartPose.ZERO);
        addChild(body, "pouch_left", CubeListBuilder.create()
                .texOffs(70, 53).addBox(-7.3F, 6.4F, -1.8F, 2.5F, 2.9F, 3.6F),
                PartPose.ZERO);
        addChild(body, "pouch_right", CubeListBuilder.create()
                .texOffs(83, 53).addBox(4.8F, 6.5F, -1.8F, 2.5F, 2.7F, 3.6F),
                PartPose.ZERO);
        addChild(body, "front_cloth_center", CubeListBuilder.create()
                .texOffs(21, 60).addBox(-3.0F, 9.0F, -2.45F, 6.0F, 5.6F, 0.5F),
                PartPose.ZERO);
        addChild(body, "front_cloth_left", CubeListBuilder.create()
                .texOffs(118, 0).addBox(-6.9F, 9.3F, -2.55F, 3.9F, 6.0F, 0.5F),
                PartPose.ZERO);
        addChild(body, "front_cloth_right", CubeListBuilder.create()
                .texOffs(118, 53).addBox(3.0F, 9.6F, -2.45F, 3.7F, 5.2F, 0.5F),
                PartPose.ZERO);
        addChild(body, "hip_under_dark", CubeListBuilder.create()
                .texOffs(76, 60).addBox(-3.4F, 9.0F, -1.7F, 6.8F, 4.2F, 0.5F),
                PartPose.ZERO);
        addChild(body, "hip_under_dark_back", CubeListBuilder.create()
                .texOffs(91, 60).addBox(-3.6F, 9.0F, 1.6F, 7.2F, 4.0F, 0.5F),
                PartPose.ZERO);
        addChild(body, "cloak_base", CubeListBuilder.create()
                .texOffs(0, 46).addBox(-6.0F, -1.6F, 2.5F, 12.0F, 2.0F, 5.4F),
                PartPose.ZERO);
        addChild(body, "cloak_outer_top", CubeListBuilder.create()
                .texOffs(0, 12).addBox(-5.0F, 0.4F, 2.9F, 10.0F, 8.5F, 0.7F),
                PartPose.ZERO);
        addChild(body, "cloak_outer_mid", CubeListBuilder.create()
                .texOffs(49, 21).addBox(-4.2F, 8.9F, 3.0F, 8.4F, 8.0F, 0.7F),
                PartPose.ZERO);
        addChild(body, "cloak_outer_bot", CubeListBuilder.create()
                .texOffs(108, 60).addBox(-3.4F, 16.9F, 3.0F, 6.8F, 3.4F, 0.7F),
                PartPose.ZERO);
        addChild(body, "cloak_inner_red_top", CubeListBuilder.create()
                .texOffs(0, 21).addBox(-4.6F, 0.6F, 4.9F, 9.2F, 8.3F, 0.6F),
                PartPose.ZERO);
        addChild(body, "cloak_inner_red_mid", CubeListBuilder.create()
                .texOffs(0, 30).addBox(-3.9F, 8.9F, 5.0F, 7.8F, 7.8F, 0.6F),
                PartPose.ZERO);
        addChild(body, "cloak_inner_red_bot", CubeListBuilder.create()
                .texOffs(10, 67).addBox(-3.2F, 16.7F, 5.1F, 6.4F, 3.2F, 0.6F),
                PartPose.ZERO);
        addChild(body, "cloak_hem_red", CubeListBuilder.create()
                .texOffs(110, 67).addBox(-3.5F, 19.6F, 3.0F, 7.0F, 0.6F, 0.7F),
                PartPose.ZERO);
        addChild(leftArm, "shoulder_left_1", CubeListBuilder.create()
                .texOffs(22, 12).addBox(-0.7F, -3.8F, -3.3F, 5.4F, 2.6F, 6.6F),
                PartPose.ZERO);
        addChild(leftArm, "shoulder_left_metal", CubeListBuilder.create()
                .texOffs(31, 30).addBox(-0.2F, -1.5F, -3.0F, 4.4F, 2.2F, 6.0F),
                PartPose.ZERO);
        addChild(leftArm, "shoulder_left_red", CubeListBuilder.create()
                .texOffs(53, 30).addBox(-0.3F, -1.4F, -3.1F, 1.1F, 2.0F, 6.2F),
                PartPose.ZERO);
        addChild(leftArm, "shoulder_left_3", CubeListBuilder.create()
                .texOffs(97, 38).addBox(0.3F, 0.9F, -2.4F, 4.0F, 2.3F, 4.8F),
                PartPose.ZERO);
        addChild(leftArm, "upper_arm_cloth_left", CubeListBuilder.create()
                .texOffs(62, 0).addBox(-0.2F, 3.2F, -2.9F, 4.4F, 4.5F, 5.8F),
                PartPose.ZERO);
        addChild(leftArm, "forearm_armor_left", CubeListBuilder.create()
                .texOffs(84, 30).addBox(-0.1F, 7.4F, -2.7F, 4.2F, 2.6F, 5.4F),
                PartPose.ZERO);
        addChild(leftArm, "forearm_band_left", CubeListBuilder.create()
                .texOffs(114, 21).addBox(-0.2F, 7.6F, -2.8F, 0.6F, 2.8F, 5.6F),
                PartPose.ZERO);
        addChild(leftArm, "glove_left", CubeListBuilder.create()
                .texOffs(35, 46).addBox(-0.1F, 9.2F, -2.4F, 4.2F, 2.3F, 4.8F),
                PartPose.ZERO);
        addChild(rightArm, "shoulder_right_cloth", CubeListBuilder.create()
                .texOffs(81, 12).addBox(-4.7F, -3.3F, -3.2F, 4.8F, 2.6F, 6.4F),
                PartPose.ZERO);
        addChild(rightArm, "shoulder_right_pad", CubeListBuilder.create()
                .texOffs(0, 38).addBox(-4.1F, -1.0F, -2.8F, 4.0F, 2.3F, 5.6F),
                PartPose.ZERO);
        addChild(rightArm, "shoulder_right_metal", CubeListBuilder.create()
                .texOffs(104, 12).addBox(-4.7F, -2.7F, -3.4F, 0.9F, 2.2F, 6.8F),
                PartPose.ZERO);
        addChild(rightArm, "shoulder_right_red", CubeListBuilder.create()
                .texOffs(68, 30).addBox(-1.8F, -1.0F, -3.0F, 1.2F, 2.2F, 6.0F),
                PartPose.ZERO);
        addChild(rightArm, "upper_arm_cloth_right", CubeListBuilder.create()
                .texOffs(41, 0).addBox(-4.2F, 3.1F, -2.9F, 4.4F, 4.6F, 5.8F),
                PartPose.ZERO);
        addChild(rightArm, "upper_red_stripe_right", CubeListBuilder.create()
                .texOffs(104, 0).addBox(-4.55F, 3.2F, -2.8F, 0.7F, 4.4F, 5.6F),
                PartPose.ZERO);
        addChild(rightArm, "forearm_armor_right", CubeListBuilder.create()
                .texOffs(104, 30).addBox(-4.1F, 7.4F, -2.7F, 4.2F, 2.6F, 5.4F),
                PartPose.ZERO);
        addChild(rightArm, "forearm_band_right", CubeListBuilder.create()
                .texOffs(17, 30).addBox(-4.5F, 7.6F, -2.8F, 0.6F, 2.8F, 5.6F),
                PartPose.ZERO);
        addChild(rightArm, "glove_right", CubeListBuilder.create()
                .texOffs(54, 46).addBox(-4.1F, 9.2F, -2.4F, 4.2F, 2.3F, 4.8F),
                PartPose.ZERO);
        addChild(rightLeg, "thigh_cloth_right", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.3F, 0.0F, -2.6F, 4.6F, 7.0F, 5.2F),
                PartPose.ZERO);
        addChild(rightLeg, "thigh_strap_right", CubeListBuilder.create()
                .texOffs(97, 53).addBox(-2.5F, 3.2F, -2.7F, 5.0F, 0.8F, 5.4F),
                PartPose.ZERO);
        addChild(rightLeg, "leg_armor_right", CubeListBuilder.create()
                .texOffs(61, 38).addBox(-2.0F, 6.3F, -2.3F, 4.0F, 3.2F, 4.6F),
                PartPose.ZERO);
        addChild(rightLeg, "shin_cloth_right", CubeListBuilder.create()
                .texOffs(20, 38).addBox(-2.3F, 7.0F, -2.6F, 4.6F, 2.6F, 5.2F),
                PartPose.ZERO);
        addChild(rightLeg, "boot_right", CubeListBuilder.create()
                .texOffs(68, 21).addBox(-2.4F, 9.5F, -3.1F, 4.8F, 2.4F, 6.2F),
                PartPose.ZERO);
        addChild(rightLeg, "boot_strap_right", CubeListBuilder.create()
                .texOffs(93, 46).addBox(-2.4F, 9.8F, -3.1F, 4.8F, 0.7F, 6.2F),
                PartPose.ZERO);
        addChild(rightLeg, "boot_sole_right", CubeListBuilder.create()
                .texOffs(0, 53).addBox(-2.5F, 11.5F, -3.2F, 5.0F, 0.5F, 6.4F),
                PartPose.ZERO);
        addChild(leftLeg, "thigh_cloth_left", CubeListBuilder.create()
                .texOffs(20, 0).addBox(-2.3F, 0.0F, -2.6F, 4.6F, 6.6F, 5.2F),
                PartPose.ZERO);
        addChild(leftLeg, "thigh_strap_left", CubeListBuilder.create()
                .texOffs(0, 60).addBox(-2.5F, 3.0F, -2.7F, 5.0F, 0.8F, 5.4F),
                PartPose.ZERO);
        addChild(leftLeg, "leg_armor_left", CubeListBuilder.create()
                .texOffs(79, 38).addBox(-2.0F, 6.3F, -2.3F, 4.0F, 3.2F, 4.6F),
                PartPose.ZERO);
        addChild(leftLeg, "shin_cloth_left", CubeListBuilder.create()
                .texOffs(40, 38).addBox(-2.3F, 7.0F, -2.6F, 4.6F, 2.6F, 5.2F),
                PartPose.ZERO);
        addChild(leftLeg, "boot_left", CubeListBuilder.create()
                .texOffs(91, 21).addBox(-2.4F, 9.5F, -3.1F, 4.8F, 2.4F, 6.2F),
                PartPose.ZERO);
        addChild(leftLeg, "boot_strap_left", CubeListBuilder.create()
                .texOffs(23, 53).addBox(-2.4F, 9.8F, -3.1F, 4.8F, 0.7F, 6.2F),
                PartPose.ZERO);
        addChild(leftLeg, "boot_sole_left", CubeListBuilder.create()
                .texOffs(46, 53).addBox(-2.5F, 11.5F, -3.2F, 5.0F, 0.5F, 6.4F),
                PartPose.ZERO);
        return LayerDefinition.create(meshDefinition, 128, 128);
    }

    private static void addChild(PartDefinition parent, String name, CubeListBuilder builder, PartPose pose) {
        parent.addOrReplaceChild(name, builder, pose);
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

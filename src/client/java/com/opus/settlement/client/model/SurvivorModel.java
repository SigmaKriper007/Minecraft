package com.opus.settlement.client.model;

import com.opus.settlement.SettlementLine;
import com.opus.settlement.entity.SurvivorEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.HumanoidArm;

public final class SurvivorModel extends PlayerModel<SurvivorEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(SettlementLine.id("survivor"), "main");

    public SurvivorModel(ModelPart root) { super(root, false); }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = PlayerModel.createMesh(CubeDeformation.NONE, false);
        PartDefinition root = mesh.getRoot();
        root.getChild("body").addOrReplaceChild("field_pack",
            CubeListBuilder.create().texOffs(32, 32).addBox(-4.5F, 1F, 2F, 9F, 10F, 3F, new CubeDeformation(.15F)),
            PartPose.ZERO);
        root.getChild("head").addOrReplaceChild("headband_knot",
            CubeListBuilder.create().texOffs(0, 48).addBox(4F, -6F, 1F, 1F, 2F, 2F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(SurvivorEntity entity, float limbSwing, float limbSwingAmount, float age, float yaw, float pitch) {
        rightArmPose = entity.getMainHandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        leftArmPose = entity.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        if (entity.getMainArm() == HumanoidArm.LEFT) {
            HumanoidModel.ArmPose swap = rightArmPose; rightArmPose = leftArmPose; leftArmPose = swap;
        }
        crouching = entity.isRetreating();
        super.setupAnim(entity, limbSwing, limbSwingAmount, age, yaw, pitch);
    }
}

package com.opus.settlement.client.model;

import com.opus.settlement.SettlementLine;
import com.opus.settlement.entity.BlackNinjaEntity;
import com.opus.settlement.entity.JapaneseWarriorEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.util.Mth;

public final class BlackNinjaModel extends PlayerModel<BlackNinjaEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(SettlementLine.id("black_ninja"), "main");
    public BlackNinjaModel(ModelPart root) { super(root, true); }
    public static LayerDefinition createLayer() {
        MeshDefinition mesh = PlayerModel.createMesh(CubeDeformation.NONE, true);
        var root = mesh.getRoot();
        root.getChild("head").addOrReplaceChild("hood_ridge", CubeListBuilder.create().texOffs(32,0)
            .addBox(-4.5F,-8.5F,-4.5F,9F,9F,9F,new CubeDeformation(.15F)), PartPose.ZERO);
        root.getChild("body").addOrReplaceChild("scarf_tail", CubeListBuilder.create().texOffs(40,32)
            .addBox(-1.5F,1F,2.2F,3F,10F,1F), PartPose.rotation(.18F,0,0));
        return LayerDefinition.create(mesh,64,64);
    }
    @Override public void setupAnim(BlackNinjaEntity entity,float limb,float amount,float age,float yaw,float pitch){
        rightArmPose=entity.getMainHandItem().isEmpty()?HumanoidModel.ArmPose.EMPTY:HumanoidModel.ArmPose.ITEM;
        crouching=entity.getActionState()==JapaneseWarriorEntity.ACTION_SMOKE_STEP;
        super.setupAnim(entity,limb,amount,age,yaw,pitch);
        if(entity.getActionState()==JapaneseWarriorEntity.ACTION_SMOKE_STEP){
            rightArm.xRot=-1.65F;rightArm.yRot=-.35F;leftArm.xRot=.55F;leftArm.yRot=.45F;
            rightLeg.xRot=-.65F;leftLeg.xRot=.65F;body.xRot=.22F;
        }
    }
}

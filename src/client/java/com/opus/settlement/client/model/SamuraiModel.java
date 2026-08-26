package com.opus.settlement.client.model;

import com.opus.settlement.SettlementLine;
import com.opus.settlement.entity.JapaneseWarriorEntity;
import com.opus.settlement.entity.SamuraiEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;

public final class SamuraiModel extends PlayerModel<SamuraiEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(SettlementLine.id("samurai"), "main");
    public SamuraiModel(ModelPart root){super(root,false);}
    public static LayerDefinition createLayer(){
        MeshDefinition mesh=PlayerModel.createMesh(CubeDeformation.NONE,false);var root=mesh.getRoot();
        root.getChild("head").addOrReplaceChild("kabuto",CubeListBuilder.create().texOffs(32,0)
            .addBox(-5F,-9F,-5F,10F,3F,10F,new CubeDeformation(.1F)),PartPose.ZERO);
        root.getChild("head").addOrReplaceChild("crest",CubeListBuilder.create().texOffs(0,48)
            .addBox(-.5F,-14F,-1F,1F,6F,2F).addBox(-4F,-13F,-.5F,8F,1F,1F),PartPose.ZERO);
        root.getChild("body").addOrReplaceChild("right_sode",CubeListBuilder.create().texOffs(32,32)
            .addBox(-8F,-1F,-3F,4F,6F,6F,new CubeDeformation(.2F)),PartPose.ZERO);
        root.getChild("body").addOrReplaceChild("left_sode",CubeListBuilder.create().texOffs(32,44)
            .addBox(4F,-1F,-3F,4F,6F,6F,new CubeDeformation(.2F)),PartPose.ZERO);
        return LayerDefinition.create(mesh,64,64);
    }
    @Override public void setupAnim(SamuraiEntity entity,float limb,float amount,float age,float yaw,float pitch){
        rightArmPose=entity.getMainHandItem().isEmpty()?HumanoidModel.ArmPose.EMPTY:HumanoidModel.ArmPose.ITEM;
        super.setupAnim(entity,limb,amount,age,yaw,pitch);
        if(entity.getActionState()==JapaneseWarriorEntity.ACTION_LONG_LUNGE){
            rightArm.xRot=-1.9F;rightArm.yRot=-.25F;leftArm.xRot=-1.25F;leftArm.yRot=.35F;
            body.yRot=-.18F;rightLeg.xRot=-.38F;leftLeg.xRot=.42F;
        }
    }
}

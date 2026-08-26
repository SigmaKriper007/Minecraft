package com.opus.fire.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public final class FirePlateModel<T extends LivingEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("opusvsexe", "fire_plate_rebuilt"), "main");
    private final ModelPart root, body, belt, rightArm, leftArm, rightLeg, leftLeg, wingLeft, wingRight;

    public FirePlateModel(ModelPart root) {
        this.root=root; body=root.getChild("body"); belt=root.getChild("belt");
        rightArm=root.getChild("rightArm"); leftArm=root.getChild("leftArm");
        rightLeg=root.getChild("rightLeg"); leftLeg=root.getChild("leftLeg");
        wingLeft=root.getChild("wing_left"); wingRight=root.getChild("wing_right");
    }
    public ModelPart body(){return body;} public ModelPart belt(){return belt;}
    public ModelPart rightArm(){return rightArm;} public ModelPart leftArm(){return leftArm;}
    public ModelPart rightLeg(){return rightLeg;} public ModelPart leftLeg(){return leftLeg;}
    public ModelPart wingLeft(){return wingLeft;} public ModelPart wingRight(){return wingRight;}
    public ModelPart wingLeftForearm(){return wingLeft.getChild("forearm");}
    public ModelPart wingRightForearm(){return wingRight.getChild("forearm");}
    public void setLegVisibility(boolean leggings, boolean boots) {
        for (ModelPart leg : new ModelPart[]{rightLeg, leftLeg}) {
            leg.getChild("thigh").visible = leggings;
            leg.getChild("knee").visible = leggings;
            leg.getChild("greave").visible = leggings || boots;
            leg.getChild("split_toe").visible = boots;
        }
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition mesh=new MeshDefinition(); PartDefinition root=mesh.getRoot();
        PartDefinition body=root.addOrReplaceChild("body",CubeListBuilder.create(),PartPose.ZERO);
        PartDefinition belt=root.addOrReplaceChild("belt",CubeListBuilder.create(),PartPose.ZERO);
        PartDefinition rightArm=root.addOrReplaceChild("rightArm",CubeListBuilder.create(),PartPose.offset(-5,2,0));
        PartDefinition leftArm=root.addOrReplaceChild("leftArm",CubeListBuilder.create(),PartPose.offset(5,2,0));
        PartDefinition rightLeg=root.addOrReplaceChild("rightLeg",CubeListBuilder.create(),PartPose.offset(-1.9f,12,0));
        PartDefinition leftLeg=root.addOrReplaceChild("leftLeg",CubeListBuilder.create(),PartPose.offset(1.9f,12,0));
        CubeDeformation plate=new CubeDeformation(0.22f);

        body.addOrReplaceChild("cuirass",CubeListBuilder.create().texOffs(0,0).addBox(-4.4f,0,-2.4f,8.8f,8.0f,4.8f,plate),PartPose.ZERO);
        body.addOrReplaceChild("sternum",CubeListBuilder.create().texOffs(30,0).addBox(-1.3f,1.0f,-3.1f,2.6f,6.5f,1.0f),PartPose.ZERO);
        body.addOrReplaceChild("core",CubeListBuilder.create().texOffs(40,0).addBox(-1.6f,2.1f,-3.5f,3.2f,3.2f,1.0f),PartPose.ZERO);
        body.addOrReplaceChild("back_frame",CubeListBuilder.create().texOffs(52,0).addBox(-3.6f,1.2f,2.3f,7.2f,6.0f,1.0f),PartPose.ZERO);
        belt.addOrReplaceChild("belt_plate",CubeListBuilder.create().texOffs(0,16).addBox(-4.5f,8.8f,-2.6f,9.0f,3.2f,5.2f,plate),PartPose.ZERO);
        belt.addOrReplaceChild("buckle",CubeListBuilder.create().texOffs(30,16).addBox(-1.4f,9.3f,-3.3f,2.8f,2.0f,1.0f),PartPose.ZERO);

        armorArm(rightArm,true,0); armorArm(leftArm,false,32);
        armorLeg(rightLeg,true,0); armorLeg(leftLeg,false,32);
        wing(root,"wing_left",true,3.8f,64);
        wing(root,"wing_right",false,-3.8f,96);
        return LayerDefinition.create(mesh,128,128);
    }

    private static void armorArm(PartDefinition arm, boolean right, int u) {
        float x=right?-3.2f:-1.2f;
        arm.addOrReplaceChild("pauldron",CubeListBuilder.create().texOffs(u,28).addBox(x,-3.0f,-2.8f,4.4f,4.0f,5.6f,new CubeDeformation(0.18f)),PartPose.ZERO);
        arm.addOrReplaceChild("vambrace",CubeListBuilder.create().texOffs(u,39).addBox(right?-2.8f:-1.2f,5.8f,-2.4f,4.0f,5.7f,4.8f,new CubeDeformation(0.12f)),PartPose.ZERO);
        arm.addOrReplaceChild("knuckle",CubeListBuilder.create().texOffs(u+18,39).addBox(right?-2.6f:-1.0f,9.6f,-3.0f,3.6f,2.0f,1.0f),PartPose.ZERO);
    }

    private static void armorLeg(PartDefinition leg, boolean right, int u) {
        leg.addOrReplaceChild("thigh",CubeListBuilder.create().texOffs(u,50).addBox(-2.0f,0,-2.35f,4.0f,5.0f,4.7f,new CubeDeformation(0.12f)),PartPose.ZERO);
        leg.addOrReplaceChild("knee",CubeListBuilder.create().texOffs(u+18,50).addBox(-2.1f,4.4f,-3.0f,4.2f,2.4f,1.3f),PartPose.ZERO);
        leg.addOrReplaceChild("greave",CubeListBuilder.create().texOffs(u,61).addBox(-2.0f,6.0f,-2.5f,4.0f,5.2f,5.0f,new CubeDeformation(0.14f)),PartPose.ZERO);
        leg.addOrReplaceChild("split_toe",CubeListBuilder.create().texOffs(u+20,61).addBox(-2.15f,9.7f,-3.5f,1.8f,2.2f,2.0f)
            .texOffs(u+28,61).addBox(0.35f,9.7f,-3.5f,1.8f,2.2f,2.0f),PartPose.ZERO);
    }

    private static void wing(PartDefinition root,String name,boolean left,float x,int u) {
        float sign=left?1.0f:-1.0f;
        PartDefinition upper=root.addOrReplaceChild(name,CubeListBuilder.create().texOffs(u,76)
            .addBox(left?0:-7.0f,-1.0f,-1.0f,7.0f,2.0f,2.0f),PartPose.offset(x,4.0f,2.6f));
        PartDefinition forearm=upper.addOrReplaceChild("forearm",CubeListBuilder.create().texOffs(u,82)
            .addBox(left?0:-7.0f,-0.8f,-0.8f,7.0f,1.6f,1.6f),PartPose.offset(sign*7.0f,0,0));
        forearm.addOrReplaceChild("finger_a",CubeListBuilder.create().texOffs(u,88)
            .addBox(left?0:-7.5f,-0.45f,-0.45f,7.5f,0.9f,0.9f),PartPose.offsetAndRotation(sign*6.5f,0,0,0,0,sign*0.35f));
        forearm.addOrReplaceChild("finger_b",CubeListBuilder.create().texOffs(u,92)
            .addBox(left?0:-6.0f,-0.4f,-0.4f,6.0f,0.8f,0.8f),PartPose.offsetAndRotation(sign*5.5f,0,0,0,0,sign*0.78f));
        forearm.addOrReplaceChild("membrane",CubeListBuilder.create().texOffs(u,96)
            .addBox(left?0:-7.0f,0.35f,-0.15f,7.0f,5.5f,0.3f),PartPose.rotation(0,0,sign*0.24f));
        upper.addOrReplaceChild("inner_membrane",CubeListBuilder.create().texOffs(u,104)
            .addBox(left?0:-7.0f,0.4f,-0.12f,7.0f,4.2f,0.24f),PartPose.rotation(0,0,sign*0.2f));
    }

    @Override public void setupAnim(T entity,float a,float b,float c,float d,float e) { }
    @Override public void renderToBuffer(PoseStack pose,VertexConsumer out,int light,int overlay,float r,float g,float b,float a){root.render(pose,out,light,overlay,r,g,b,a);}
}

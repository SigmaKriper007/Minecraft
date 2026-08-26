package com.opus.paradise.client.model;

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

public final class ParthenonRegaliaModel<T extends LivingEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION=new ModelLayerLocation(new ResourceLocation("opusvsexe","parthenon_regalia"),"main");
    private final ModelPart root,head,body,rightArm,leftArm,rightLeg,leftLeg,wingLeft,wingRight,halo;
    public ParthenonRegaliaModel(ModelPart root){
        this.root=root;head=root.getChild("head");body=root.getChild("body");rightArm=root.getChild("right_arm");leftArm=root.getChild("left_arm");rightLeg=root.getChild("right_leg");leftLeg=root.getChild("left_leg");wingLeft=root.getChild("wing_left");wingRight=root.getChild("wing_right");halo=head.getChild("halo");
    }
    public ModelPart head(){return head;} public ModelPart body(){return body;} public ModelPart rightArm(){return rightArm;} public ModelPart leftArm(){return leftArm;} public ModelPart rightLeg(){return rightLeg;} public ModelPart leftLeg(){return leftLeg;} public ModelPart wingLeft(){return wingLeft;} public ModelPart wingRight(){return wingRight;} public ModelPart halo(){return halo;}
    public void setTorsoParts(boolean chest,boolean leggings){body.getChild("cuirass").visible=chest;body.getChild("pectoral").visible=chest;body.getChild("sun").visible=chest;body.getChild("skirt").visible=chest||leggings;}

    public static LayerDefinition createLayerDefinition(){
        MeshDefinition mesh=new MeshDefinition();PartDefinition root=mesh.getRoot();CubeDeformation shell=new CubeDeformation(.28F);
        PartDefinition head=root.addOrReplaceChild("head",CubeListBuilder.create(),PartPose.ZERO);
        head.addOrReplaceChild("crown",CubeListBuilder.create().texOffs(0,0).addBox(-4.25F,-8.25F,-4.25F,8.5F,3.2F,8.5F,shell),PartPose.ZERO);
        head.addOrReplaceChild("brow",CubeListBuilder.create().texOffs(0,14).addBox(-4.5F,-5.8F,-4.75F,9F,1.25F,1F),PartPose.ZERO);
        head.addOrReplaceChild("crest",CubeListBuilder.create().texOffs(36,0).addBox(-.65F,-11.7F,-1F,1.3F,4F,2F),PartPose.ZERO);
        PartDefinition halo=head.addOrReplaceChild("halo",CubeListBuilder.create(),PartPose.offset(0,-10.2F,1));
        halo.addOrReplaceChild("front",CubeListBuilder.create().texOffs(48,0).addBox(-4.2F,-.35F,-4.4F,8.4F,.7F,.7F),PartPose.ZERO);
        halo.addOrReplaceChild("back",CubeListBuilder.create().texOffs(48,2).addBox(-4.2F,-.35F,3.7F,8.4F,.7F,.7F),PartPose.ZERO);
        halo.addOrReplaceChild("left",CubeListBuilder.create().texOffs(48,4).addBox(-4.4F,-.35F,-3.7F,.7F,.7F,7.4F),PartPose.ZERO);
        halo.addOrReplaceChild("right",CubeListBuilder.create().texOffs(48,6).addBox(3.7F,-.35F,-3.7F,.7F,.7F,7.4F),PartPose.ZERO);

        PartDefinition body=root.addOrReplaceChild("body",CubeListBuilder.create(),PartPose.ZERO);
        body.addOrReplaceChild("cuirass",CubeListBuilder.create().texOffs(0,20).addBox(-4.35F,-.1F,-2.35F,8.7F,8.3F,4.7F,shell),PartPose.ZERO);
        body.addOrReplaceChild("pectoral",CubeListBuilder.create().texOffs(32,20).addBox(-3.8F,.6F,-3F,7.6F,2.3F,.9F),PartPose.ZERO);
        body.addOrReplaceChild("sun",CubeListBuilder.create().texOffs(64,16).addBox(-1.35F,2.1F,-3.35F,2.7F,2.7F,.8F),PartPose.ZERO);
        body.addOrReplaceChild("skirt",CubeListBuilder.create().texOffs(0,34).addBox(-4.55F,7.5F,-2.6F,9.1F,4.5F,5.2F,new CubeDeformation(.15F)),PartPose.ZERO);
        arm(root,"right_arm",-5F,true,50);arm(root,"left_arm",5F,false,72);
        leg(root,"right_leg",-1.9F,0);leg(root,"left_leg",1.9F,24);
        wing(root,"wing_left",4F,true);wing(root,"wing_right",-4F,false);
        return LayerDefinition.create(mesh,128,128);
    }

    private static void arm(PartDefinition root,String name,float x,boolean right,int u){
        PartDefinition arm=root.addOrReplaceChild(name,CubeListBuilder.create(),PartPose.offset(x,2,0));float ax=right?-3.1F:-1.1F;
        arm.addOrReplaceChild("pauldron",CubeListBuilder.create().texOffs(u,24).addBox(ax,-2.6F,-2.75F,4.2F,4F,5.5F,new CubeDeformation(.18F)),PartPose.ZERO);
        arm.addOrReplaceChild("vambrace",CubeListBuilder.create().texOffs(u,35).addBox(right?-2.8F:-1.2F,5.2F,-2.35F,4F,6.2F,4.7F,new CubeDeformation(.12F)),PartPose.ZERO);
    }
    private static void leg(PartDefinition root,String name,float x,int u){
        PartDefinition leg=root.addOrReplaceChild(name,CubeListBuilder.create(),PartPose.offset(x,12,0));
        leg.addOrReplaceChild("thigh",CubeListBuilder.create().texOffs(u,48).addBox(-2.05F,0,-2.35F,4.1F,5.5F,4.7F,new CubeDeformation(.14F)),PartPose.ZERO);
        leg.addOrReplaceChild("knee",CubeListBuilder.create().texOffs(u+16,48).addBox(-2.15F,4.6F,-3F,4.3F,2.4F,1.2F),PartPose.ZERO);
        leg.addOrReplaceChild("greave",CubeListBuilder.create().texOffs(u,60).addBox(-2.05F,6F,-2.5F,4.1F,5.8F,5F,new CubeDeformation(.12F)),PartPose.ZERO);
    }
    private static void wing(PartDefinition root,String name,float x,boolean left){
        float sign=left?1F:-1F;PartDefinition wing=root.addOrReplaceChild(name,CubeListBuilder.create(),PartPose.offset(x,3,2.7F));
        wing.addOrReplaceChild("spar",mirrorBox(left,72,52,0,-.55F,-.55F,9F,1.1F,1.1F),PartPose.rotation(0,0,sign*-.38F));
        for(int i=0;i<5;i++){
            float length=10.5F-i*.8F;float angle=sign*(.08F+i*.19F);
            wing.addOrReplaceChild("feather_"+i,mirrorBox(left,72,58+i*5,0,-.5F,-.25F,length,2.2F,.5F),PartPose.offsetAndRotation(sign*(1.2F+i*1.25F),1.1F+i*.35F,0,0,0,angle));
        }
        wing.addOrReplaceChild("coverts",mirrorBox(left,72,88,0,-.6F,-.3F,8.5F,3.2F,.6F),PartPose.offsetAndRotation(sign*.5F,-.4F,0,0,0,sign*.15F));
    }
    private static CubeListBuilder mirrorBox(boolean left,int u,int v,float x,float y,float z,float w,float h,float d){return CubeListBuilder.create().texOffs(u,v).addBox(left?x:-(x+w),y,z,w,h,d);}
    public void setLegParts(boolean leggings,boolean boots){for(ModelPart leg:new ModelPart[]{rightLeg,leftLeg}){leg.getChild("thigh").visible=leggings;leg.getChild("knee").visible=leggings;leg.getChild("greave").visible=leggings||boots;}}
    @Override public void setupAnim(T entity,float limbSwing,float limbAmount,float age,float yaw,float pitch){}
    @Override public void renderToBuffer(PoseStack pose,VertexConsumer out,int light,int overlay,float r,float g,float b,float a){root.render(pose,out,light,overlay,r,g,b,a);}
}

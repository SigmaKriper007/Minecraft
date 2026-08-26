package com.opus.ember.client.model;

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

/**
 * Ember chestplate «Сердце Углей»: костяные рёбра, тлеющее сердце и ОДНА пара
 * больших мембранных крыльев павшего серафима.
 *
 * Крыло — плоский скелет (без yaw): плечо (humerus) с когтем-шипом наверху,
 * предплечье (forearm) с шипом на запястье, четыре двухзвенных пальца
 * (finger_i → finger_i_tip, кончики сужены) веером и три полотнища-мембраны
 * с рваным нижним краем + надплечевая перепонка (arm_web).
 *
 * База позы — РАСКРЫТОЕ крыло; слой складывает его поворотом пальцев.
 */
public final class EmberPlateModel<T extends LivingEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("opusvsexe", "ember_plate"), "main");
    private final ModelPart root, body, belt, rightArm, leftArm, rightLeg, leftLeg;
    private final ModelPart wingL, wingR;

    public EmberPlateModel(ModelPart root) {
        this.root=root; body=root.getChild("body"); belt=root.getChild("belt");
        rightArm=root.getChild("rightArm"); leftArm=root.getChild("leftArm");
        rightLeg=root.getChild("rightLeg"); leftLeg=root.getChild("leftLeg");
        wingL=root.getChild("wing_l"); wingR=root.getChild("wing_r");
    }
    public ModelPart body(){return body;} public ModelPart belt(){return belt;}
    public ModelPart rightArm(){return rightArm;} public ModelPart leftArm(){return leftArm;}
    public ModelPart rightLeg(){return rightLeg;} public ModelPart leftLeg(){return leftLeg;}
    public ModelPart wingLeft(){return wingL;} public ModelPart wingRight(){return wingR;}
    public ModelPart finger(ModelPart wing, int i){
        return wing.getChild("humerus").getChild("forearm").getChild("finger_" + i);
    }
    public ModelPart fingerTip(ModelPart wing, int i){return finger(wing, i).getChild("finger_" + i + "_tip");}
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

        // Пепельная основа + костяные рёбра-оссуарий
        body.addOrReplaceChild("cuirass",CubeListBuilder.create().texOffs(0,0).addBox(-4.4f,0,-2.4f,8.8f,8.0f,4.8f,plate),PartPose.ZERO);
        for (int i = 0; i < 3; i++) {
            body.addOrReplaceChild("rib_" + i, CubeListBuilder.create().texOffs(0, 16 + i * 3)
                .addBox(-3.8f, 1.2f + i * 2.0f, -2.9f, 7.6f, 1.0f, 0.9f), PartPose.ZERO);
        }
        body.addOrReplaceChild("sternum",CubeListBuilder.create().texOffs(30,0).addBox(-0.9f,1.0f,-3.2f,1.8f,6.5f,0.8f),PartPose.ZERO);
        body.addOrReplaceChild("heart",CubeListBuilder.create().texOffs(96,0).addBox(-1.6f,2.2f,-3.6f,3.2f,3.2f,0.9f),PartPose.ZERO);
        body.addOrReplaceChild("back_frame",CubeListBuilder.create().texOffs(52,0).addBox(-3.6f,1.2f,2.3f,7.2f,6.0f,1.0f),PartPose.ZERO);
        belt.addOrReplaceChild("belt_plate",CubeListBuilder.create().texOffs(0,26).addBox(-4.5f,8.8f,-2.6f,9.0f,3.2f,5.2f,plate),PartPose.ZERO);
        belt.addOrReplaceChild("buckle",CubeListBuilder.create().texOffs(96,16).addBox(-1.2f,9.4f,-3.3f,2.4f,1.8f,0.8f),PartPose.ZERO);

        armorArm(rightArm,true,0); armorArm(leftArm,false,32);
        armorLeg(rightLeg,true,0); armorLeg(leftLeg,false,32);
        // Одна пара мембранных крыльев
        wingBuild(root,"wing_l",true, 3.8f, 3.2f);
        wingBuild(root,"wing_r",false,-3.8f, 3.2f);
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

    /**
     * Строит одно крыло (плоское, в плоскости XY за спиной). Параметры сверены
     * с превью (preview_ember_wings.py): кости связны, плоскость ровная.
     * left=true → левое крыло (s=+1), false → правое (зеркало).
     */
    private static void wingBuild(PartDefinition root,String name,boolean left,float x,float y) {
        float s = left ? 1f : -1f;
        PartDefinition wing = root.addOrReplaceChild(name,CubeListBuilder.create(),PartPose.offset(x,y,2.6f));

        float[] fL = {6.5f, 6.8f, 6.6f, 5.9f};   // длина пальца
        float[] fA = {25f, 45f, 65f, 85f};       // наклон пальцев вниз (world)
        float[] fK = {6f, 6f, 5f, 5f};           // докрут кончика

        // Ведущая кость (humerus): горизонталь с лёгким подъёмом вверх
        PartDefinition hum = wing.addOrReplaceChild("humerus",
            wbox(left, 0, -1.0f, -1.0f, 6.5f, 2.0f, 2.0f, 8, 64),
            PartPose.rotation(0,0,rad(s*-8f)));
        // Коготь-крюк на внешнем конце (вверх)
        hum.addOrReplaceChild("claw",
            wbox(left, 0, -0.5f, -0.5f, 2.2f, 1.0f, 1.0f, 0, 64),
            rotAt(s, -62f, 6.5f));
        // Предплечье: продолжение горизонтали к запястью
        PartDefinition fore = hum.addOrReplaceChild("forearm",
            wbox(left, 0, -0.8f, -0.8f, 6.5f, 1.6f, 1.6f, 16, 64),
            rotAt(s, 8f, 6.5f));
        // Шип на запястье (вниз-наружу)
        fore.addOrReplaceChild("tip_spike",
            wbox(left, 0, -0.5f, -0.5f, 1.5f, 1.0f, 1.0f, 24, 64),
            rotAt(s, 40f, 6.5f));
        // Пальцы веером вниз (тонкие): база + докрученный кончик
        for (int i = 0; i < 4; i++) {
            PartDefinition fg = fore.addOrReplaceChild("finger_" + i,
                wbox(left, 0, -0.45f, -0.45f, fL[i], 0.9f, 0.9f, 27, 64 + i*2),
                rotAt(s, fA[i], 6.5f));
            fg.addOrReplaceChild("finger_" + i + "_tip",
                wbox(left, 0, -0.35f, -0.35f, fL[i]*0.75f, 0.7f, 0.7f, 35, 64 + i*2),
                rotAt(s, fK[i], fL[i]));
        }
        // Мембраны ВИСЯТ НИЖЕ кости (origin y>0), веером между пальцами
        fore.addOrReplaceChild("membrane_0",
            wbox(left, 0, 0.35f, -0.15f, 6.6f, 5.6f, 0.3f, 0, 76),
            rotAt(s, 14f, 6.5f));
        fore.addOrReplaceChild("membrane_1",
            wbox(left, 0, 0.35f, -0.15f, 6.8f, 6.6f, 0.3f, 10, 76),
            rotAt(s, 32f, 6.5f));
        fore.addOrReplaceChild("membrane_2",
            wbox(left, 0, 0.35f, -0.15f, 6.4f, 6.8f, 0.3f, 20, 76),
            rotAt(s, 62f, 6.5f));
        // Внутренняя перепонка под ведущей костью (от локтя)
        hum.addOrReplaceChild("arm_web",
            wbox(left, 0, 0.4f, -0.12f, 6.4f, 4.6f, 0.3f, 30, 76),
            rotAt(s, 20f, 6.5f));
    }

    /** Зеркальная коробка: у правого крыла origin.x = −(x+w). */
    private static CubeListBuilder wbox(boolean left,float x,float y,float z,float w,float h,float d,int u,int v){
        return CubeListBuilder.create().texOffs(u,v).addBox(left?x:-(x+w),y,z,w,h,d);
    }

    private static PartPose rotAt(float s, float zDeg, float px){
        return PartPose.offsetAndRotation(s*px,0,0, 0,0,rad(s*zDeg));
    }
    private static float rad(float d){return (float)Math.toRadians(d);}

    @Override public void setupAnim(T entity,float a,float b,float c,float d,float e) { }
    @Override public void renderToBuffer(PoseStack pose,VertexConsumer out,int light,int overlay,float r,float g,float b,float a){root.render(pose,out,light,overlay,r,g,b,a);}
}

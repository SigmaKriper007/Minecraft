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

public final class FireHelmetModel<T extends LivingEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("opusvsexe", "fire_helmet_rebuilt"), "main");
    private final ModelPart root;
    private final ModelPart head;

    public FireHelmetModel(ModelPart root) { this.root = root; this.head = root.getChild("head"); }
    public ModelPart head() { return head; }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        CubeDeformation shell = new CubeDeformation(0.35f);
        head.addOrReplaceChild("crown", CubeListBuilder.create().texOffs(0,0).addBox(-4,-8,-4,8,5,8,shell), PartPose.ZERO);
        head.addOrReplaceChild("mask", CubeListBuilder.create().texOffs(0,18).addBox(-4,-4.5f,-4.4f,8,4,2,shell), PartPose.ZERO);
        head.addOrReplaceChild("visor", CubeListBuilder.create().texOffs(24,18).addBox(-3.2f,-4.9f,-4.9f,6.4f,1.2f,0.7f), PartPose.ZERO);
        head.addOrReplaceChild("cheek_l", CubeListBuilder.create().texOffs(0,25).addBox(3.2f,-3.8f,-4.2f,1.5f,4.0f,3.5f), PartPose.ZERO);
        head.addOrReplaceChild("cheek_r", CubeListBuilder.create().texOffs(11,25).addBox(-4.7f,-3.8f,-4.2f,1.5f,4.0f,3.5f), PartPose.ZERO);
        head.addOrReplaceChild("neck_guard", CubeListBuilder.create().texOffs(24,22).addBox(-4.4f,-2.4f,3.2f,8.8f,3.0f,1.5f), PartPose.ZERO);
        head.addOrReplaceChild("crust_ridge", CubeListBuilder.create().texOffs(48,0)
            .addBox(-1.0f,-9.1f,-3.2f,2.0f,1.4f,6.4f), PartPose.ZERO);
        head.addOrReplaceChild("brow_l", CubeListBuilder.create().texOffs(48,9)
            .addBox(0.2f,-5.7f,-5.0f,3.8f,1.2f,1.2f), PartPose.rotation(0,0,-0.12f));
        head.addOrReplaceChild("brow_r", CubeListBuilder.create().texOffs(48,13)
            .addBox(-4.0f,-5.7f,-5.0f,3.8f,1.2f,1.2f), PartPose.rotation(0,0,0.12f));
        head.addOrReplaceChild("temple_l", CubeListBuilder.create().texOffs(66,0)
            .addBox(3.9f,-6.8f,-2.8f,1.2f,5.2f,5.6f), PartPose.ZERO);
        head.addOrReplaceChild("temple_r", CubeListBuilder.create().texOffs(80,0)
            .addBox(-5.1f,-6.8f,-2.8f,1.2f,5.2f,5.6f), PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override public void setupAnim(T entity,float limbSwing,float limbSwingAmount,float age,float yaw,float pitch) { }
    @Override public void renderToBuffer(PoseStack pose, VertexConsumer out,int light,int overlay,float r,float g,float b,float a) {
        root.render(pose,out,light,overlay,r,g,b,a);
    }
}

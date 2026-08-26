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
 * Ember helmet «Лик Падшего»: костяная маска, вуаль-повязка, светящиеся глаза,
 * сломанный нимб и два изогнутых рога. Полностью самостоятельный концепт
 * «пепельного серафима».
 */
public final class EmberHelmetModel<T extends LivingEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("opusvsexe", "ember_helmet"), "main");
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart halo;
    private final ModelPart hornL, hornR;

    public EmberHelmetModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.halo = head.getChild("halo");
        this.hornL = head.getChild("horn_l");
        this.hornR = head.getChild("horn_r");
    }
    public ModelPart head() { return head; }
    public ModelPart halo() { return halo; }
    public ModelPart hornL() { return hornL; }
    public ModelPart hornR() { return hornR; }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        CubeDeformation shell = new CubeDeformation(0.35f);
        // Костяная маска
        head.addOrReplaceChild("mask", CubeListBuilder.create().texOffs(0, 0)
            .addBox(-4, -8, -4, 8, 5, 8, shell).addBox(-3.5f, -4.5f, -4.5f, 7, 4.5f, 2, shell), PartPose.ZERO);
        // Вуаль-повязка поперёк глаз (тёмная)
        head.addOrReplaceChild("veil", CubeListBuilder.create().texOffs(80, 0)
            .addBox(-4.4f, -5.6f, -5.1f, 8.8f, 1.8f, 0.8f), PartPose.ZERO);
        // Светящиеся глаза (оранжевая прорезь под вуалью)
        head.addOrReplaceChild("eyes", CubeListBuilder.create().texOffs(96, 8)
            .addBox(-3.0f, -4.8f, -5.2f, 6.0f, 0.9f, 0.6f), PartPose.ZERO);
        // Затылочная пластина
        head.addOrReplaceChild("neck_guard", CubeListBuilder.create().texOffs(0, 16)
            .addBox(-4.2f, -2.6f, 3.0f, 8.4f, 3.0f, 1.6f, shell), PartPose.ZERO);
        // Изогнутые рога (назад-вверх)
        horn(head, "horn_l", 4.3f, false, 48);
        horn(head, "horn_r", -4.3f, true, 72);
        // Сломанный нимб (кольцо с разрывом), вращается в слое
        PartDefinition halo = head.addOrReplaceChild("halo", CubeListBuilder.create(), PartPose.offsetAndRotation(0, -8.6f, 0, -0.16f, 0, 0));
        halo.addOrReplaceChild("seg_front", CubeListBuilder.create().texOffs(96, 0).addBox(-3, -0.2f, -3.4f, 6, 0.4f, 0.4f), PartPose.ZERO);
        halo.addOrReplaceChild("seg_back", CubeListBuilder.create().texOffs(96, 0).addBox(-3, -0.2f, 3.0f, 6, 0.4f, 0.4f), PartPose.ZERO);
        halo.addOrReplaceChild("seg_left", CubeListBuilder.create().texOffs(96, 0).addBox(-3.4f, -0.2f, -3.0f, 0.4f, 0.4f, 6), PartPose.ZERO);
        halo.addOrReplaceChild("seg_right", CubeListBuilder.create().texOffs(96, 0).addBox(3.0f, -0.2f, -3.0f, 0.4f, 0.4f, 2.4f), PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 64);
    }

    private static void horn(PartDefinition head, String name, float x, boolean mirror, int u) {
        PartDefinition base = head.addOrReplaceChild(name, CubeListBuilder.create().mirror(mirror)
            .texOffs(u, 0).addBox(mirror ? -2.2f : 0.0f, -2.2f, -1.2f, 2.2f, 4.4f, 2.4f),
            PartPose.offsetAndRotation(x, -7.0f, 0.6f, -0.28f, 0.0f, mirror ? 0.52f : -0.52f));
        PartDefinition mid = base.addOrReplaceChild(name + "_mid", CubeListBuilder.create().mirror(mirror)
            .texOffs(u, 8).addBox(mirror ? -1.7f : 0.0f, -4.6f, -0.9f, 1.7f, 4.8f, 1.8f),
            PartPose.offsetAndRotation(mirror ? -1.7f : 1.7f, -2.0f, 0.0f, -0.4f, 0.0f, mirror ? 0.38f : -0.38f));
        mid.addOrReplaceChild(name + "_tip", CubeListBuilder.create().mirror(mirror)
            .texOffs(96, 16).addBox(mirror ? -1.0f : 0.0f, -4.4f, -0.6f, 1.0f, 4.4f, 1.2f),
            PartPose.offsetAndRotation(mirror ? -1.2f : 1.2f, -4.2f, 0.0f, -0.5f, 0.0f, mirror ? 0.26f : -0.26f));
    }

    @Override public void setupAnim(T entity,float limbSwing,float limbSwingAmount,float age,float yaw,float pitch) { }
    @Override public void renderToBuffer(PoseStack pose, VertexConsumer out,int light,int overlay,float r,float g,float b,float a) {
        root.render(pose,out,light,overlay,r,g,b,a);
    }
}

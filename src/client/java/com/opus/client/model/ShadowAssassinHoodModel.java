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

public class ShadowAssassinHoodModel<T extends LivingEntity> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation("opusvsexe", "shadow_assassin_hood"), "main");

    private final ModelPart root;
    private final ModelPart head;

    public ShadowAssassinHoodModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
    }

    public ModelPart head() {
        return this.head;
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition inner_hood = head.addOrReplaceChild("inner_hood", CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition hood = head.addOrReplaceChild("hood", CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition hood_top = head.addOrReplaceChild("hood_top", CubeListBuilder.create(), PartPose.ZERO);

        inner_hood.addOrReplaceChild("inner_cap_top", CubeListBuilder.create()
        .texOffs(42, 19).addBox(-4.7F, -9.2F, -4.7F, 9.4F, 2.3F, 9.4F), PartPose.ZERO);
        inner_hood.addOrReplaceChild("inner_cap_mid", CubeListBuilder.create()
        .texOffs(0, 19).addBox(-5.2F, -6.9F, -5.2F, 10.4F, 3.1F, 10.4F), PartPose.ZERO);
        inner_hood.addOrReplaceChild("inner_cap_back", CubeListBuilder.create()
        .texOffs(0, 33).addBox(-4.7F, -6.9F, 2.9F, 9.4F, 6.9F, 2.3F), PartPose.ZERO);
        inner_hood.addOrReplaceChild("inner_cap_left", CubeListBuilder.create()
        .texOffs(46, 0).addBox(-5.2F, -6.9F, -5.2F, 1.0F, 6.9F, 10.4F), PartPose.ZERO);
        inner_hood.addOrReplaceChild("inner_cap_right", CubeListBuilder.create()
        .texOffs(69, 0).addBox(4.2F, -6.9F, -5.2F, 1.0F, 6.9F, 10.4F), PartPose.ZERO);
        inner_hood.addOrReplaceChild("inner_cap_front", CubeListBuilder.create()
        .texOffs(65, 33).addBox(-4.7F, -6.9F, -5.25F, 9.4F, 2.9F, 0.5F), PartPose.ZERO);
        hood_top.addOrReplaceChild("hood_top_1", CubeListBuilder.create()
        .texOffs(81, 19).addBox(-3.7F, -12.0F, -3.7F, 7.4F, 2.5F, 7.4F), PartPose.ZERO);
        hood_top.addOrReplaceChild("hood_top_2", CubeListBuilder.create()
        .texOffs(24, 33).addBox(-2.7F, -14.5F, -2.7F, 5.4F, 2.5F, 5.4F), PartPose.rotation(-0.06F, 0.0F, 0.0F));
        hood_top.addOrReplaceChild("hood_tip", CubeListBuilder.create()
        .texOffs(111, 19).addBox(-1.5F, -16.0F, -1.1F, 3.0F, 1.5F, 3.0F), PartPose.rotation(-0.12F, 0.0F, 0.0F));
        hood.addOrReplaceChild("hood_crest", CubeListBuilder.create()
        .texOffs(119, 0).addBox(-0.7F, -14.0F, -3.6F, 1.4F, 6.0F, 0.7F), PartPose.ZERO);
        hood.addOrReplaceChild("hood_left", CubeListBuilder.create()
        .texOffs(0, 0).addBox(-6.2F, -8.5F, -3.9F, 2.0F, 10.5F, 9.0F), PartPose.ZERO);
        hood.addOrReplaceChild("hood_right", CubeListBuilder.create()
        .texOffs(23, 0).addBox(4.2F, -8.5F, -3.9F, 2.0F, 10.5F, 9.0F), PartPose.ZERO);
        hood.addOrReplaceChild("hood_back", CubeListBuilder.create()
        .texOffs(93, 0).addBox(-5.2F, -9.0F, 3.1F, 10.4F, 12.0F, 2.0F), PartPose.ZERO);
        hood.addOrReplaceChild("hood_front", CubeListBuilder.create()
        .texOffs(85, 33).addBox(-4.5F, -6.2F, -4.9F, 9.0F, 2.4F, 0.6F), PartPose.ZERO);
        hood.addOrReplaceChild("hood_front_tip", CubeListBuilder.create()
        .texOffs(106, 33).addBox(-3.4F, -3.1F, -5.3F, 6.8F, 1.0F, 0.5F), PartPose.ZERO);
        hood.addOrReplaceChild("hood_red_trim", CubeListBuilder.create()
        .texOffs(0, 42).addBox(-4.3F, -2.8F, -4.9F, 8.6F, 0.6F, 0.5F), PartPose.ZERO);
        hood.addOrReplaceChild("hood_neck", CubeListBuilder.create()
        .texOffs(47, 33).addBox(-4.0F, -0.4F, -4.9F, 8.0F, 3.4F, 0.5F), PartPose.ZERO);

        return LayerDefinition.create(meshDefinition, 128, 128);
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

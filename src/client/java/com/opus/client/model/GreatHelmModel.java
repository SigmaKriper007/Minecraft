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

public class GreatHelmModel<T extends LivingEntity> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation("opusvsexe", "great_helm"), "main");

    private final ModelPart root;
    private final ModelPart head;

    public GreatHelmModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
    }

    public ModelPart head() {
        return this.head;
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        head.addOrReplaceChild("dome_top", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.0F, -14.0F, -3.0F, 6.0F, 2.0F, 6.0F), PartPose.ZERO);
        head.addOrReplaceChild("dome_mid", CubeListBuilder.create()
                .texOffs(26, 0).addBox(-4.5F, -12.0F, -4.5F, 9.0F, 2.0F, 9.0F), PartPose.ZERO);
        head.addOrReplaceChild("dome_base", CubeListBuilder.create()
                .texOffs(0, 14).addBox(-5.5F, -10.0F, -5.5F, 11.0F, 2.0F, 11.0F), PartPose.ZERO);

        head.addOrReplaceChild("wall_left", CubeListBuilder.create()
                .texOffs(23, 47).addBox(-6.0F, -8.0F, -4.5F, 1.0F, 8.0F, 9.0F), PartPose.ZERO);
        head.addOrReplaceChild("wall_right", CubeListBuilder.create()
                .texOffs(44, 47).addBox(5.0F, -8.0F, -4.5F, 1.0F, 8.0F, 9.0F), PartPose.ZERO);
        head.addOrReplaceChild("wall_back", CubeListBuilder.create()
                .texOffs(0, 30).addBox(-5.0F, -8.0F, 4.5F, 10.0F, 8.0F, 1.0F), PartPose.ZERO);

        head.addOrReplaceChild("front_left", CubeListBuilder.create()
                .texOffs(23, 30).addBox(-5.0F, -8.0F, -5.5F, 4.0F, 8.0F, 1.0F), PartPose.ZERO);
        head.addOrReplaceChild("front_right", CubeListBuilder.create()
                .texOffs(34, 30).addBox(1.0F, -8.0F, -5.5F, 4.0F, 8.0F, 1.0F), PartPose.ZERO);

        head.addOrReplaceChild("visor_vertical", CubeListBuilder.create()
                .texOffs(45, 30).addBox(-1.5F, -14.0F, -6.5F, 3.0F, 8.0F, 1.0F), PartPose.ZERO);
        head.addOrReplaceChild("visor_horizontal", CubeListBuilder.create()
                .texOffs(0, 47).addBox(-5.0F, -8.0F, -6.75F, 10.0F, 2.0F, 1.0F), PartPose.ZERO);

        return LayerDefinition.create(meshDefinition, 64, 64);
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
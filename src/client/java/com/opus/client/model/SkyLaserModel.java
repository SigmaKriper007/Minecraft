package com.opus.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.opus.entity.SkyLaserEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class SkyLaserModel extends EntityModel<SkyLaserEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation("opusvsexe", "sky_laser"), "main");

    private static final float UNITS_PER_BLOCK = 16.0F;
    private static final float BEAM_BASE_HEIGHT = 26.0F;
    private static final float BEAM_BASE_WIDTH = 24.0F;
    private static final float MAX_SCALE_Y = SkyLaserEntity.MAX_HEIGHT_BLOCKS * UNITS_PER_BLOCK / BEAM_BASE_HEIGHT;
    private static final float MAX_SCALE_XZ = SkyLaserEntity.MAX_DIAMETER_BLOCKS * UNITS_PER_BLOCK / BEAM_BASE_WIDTH;
    private static final float BASE_SPIN = (float) Math.toRadians(15.0D);
    private static final float SPIN_AMOUNT = (float) Math.toRadians(95.0D);

    private static final float QUARTER_TURN = (float) Math.toRadians(90.0D);
    private static final float EIGHTH_TURN = (float) Math.toRadians(45.0D);
    private static final float THREE_EIGHTHS = (float) Math.toRadians(135.0D);

    private final ModelPart bone;

    public SkyLaserModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();
        PartDefinition bone = root.addOrReplaceChild("bone",
            CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        // Layer 1 — outer glow (purple, widest): 4 blades, 45° apart
        CubeListBuilder outer = CubeListBuilder.create()
            .texOffs(0, 0).addBox(-12.0F, 0.0F, -1.0F, 24.0F, 26.0F, 2.0F);
        bone.addOrReplaceChild("outer_0", outer, PartPose.ZERO);
        bone.addOrReplaceChild("outer_45", outer, PartPose.rotation(0.0F, EIGHTH_TURN, 0.0F));
        bone.addOrReplaceChild("outer_90", outer, PartPose.rotation(0.0F, QUARTER_TURN, 0.0F));
        bone.addOrReplaceChild("outer_135", outer, PartPose.rotation(0.0F, THREE_EIGHTHS, 0.0F));

        // Layer 2 — main beam (cyan, medium): 4 blades, 45° apart
        CubeListBuilder main = CubeListBuilder.create()
            .texOffs(0, 28).addBox(-8.0F, 0.0F, -1.0F, 16.0F, 26.0F, 2.0F);
        bone.addOrReplaceChild("main_0", main, PartPose.ZERO);
        bone.addOrReplaceChild("main_45", main, PartPose.rotation(0.0F, EIGHTH_TURN, 0.0F));
        bone.addOrReplaceChild("main_90", main, PartPose.rotation(0.0F, QUARTER_TURN, 0.0F));
        bone.addOrReplaceChild("main_135", main, PartPose.rotation(0.0F, THREE_EIGHTHS, 0.0F));

        // Layer 3 — white-hot core (narrowest): 4 blades, 45° apart
        CubeListBuilder core = CubeListBuilder.create()
            .texOffs(0, 56).addBox(-4.0F, 0.0F, -1.0F, 8.0F, 26.0F, 2.0F);
        bone.addOrReplaceChild("core_0", core, PartPose.ZERO);
        bone.addOrReplaceChild("core_45", core, PartPose.rotation(0.0F, EIGHTH_TURN, 0.0F));
        bone.addOrReplaceChild("core_90", core, PartPose.rotation(0.0F, QUARTER_TURN, 0.0F));
        bone.addOrReplaceChild("core_135", core, PartPose.rotation(0.0F, THREE_EIGHTHS, 0.0F));

        return LayerDefinition.create(meshDefinition, 128, 128);
    }

    @Override
    public void setupAnim(SkyLaserEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        float duration = SkyLaserEntity.LIFETIME_TICKS;
        float t = Math.min(ageInTicks / duration, 1.0F);

        // Strike: width flares from a thin thread to full in the first 20% of the lifetime
        float flare = Math.min(ageInTicks / (duration * 0.2F), 1.0F);
        float flareEase = 1.0F - (float) Math.pow(1.0F - flare, 3.0D);

        // Close: beam narrows during the last quarter of the lifetime
        float close = Math.max((t - 0.75F) / 0.25F, 0.0F);
        float closeEase = close * close * (3.0F - 2.0F * close);

        // Subtle energy pulse while the beam holds
        float pulse = 1.0F + 0.04F * (float) Math.sin(ageInTicks * 0.6D);

        float width = (0.12F + 0.88F * flareEase) * pulse * (1.0F - closeEase);

        this.bone.yScale = MAX_SCALE_Y;
        this.bone.xScale = width * MAX_SCALE_XZ;
        this.bone.zScale = width * MAX_SCALE_XZ;
        this.bone.yRot = BASE_SPIN + SPIN_AMOUNT * t;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

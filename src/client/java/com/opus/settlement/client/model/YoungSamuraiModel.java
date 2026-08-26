package com.opus.settlement.client.model;

import com.opus.settlement.SettlementLine;
import com.opus.settlement.entity.YoungSamuraiEntity;
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

/** Reference-led kimono, floral hairpiece, cords, scabbards, and authored combat poses. */
public final class YoungSamuraiModel extends PlayerModel<YoungSamuraiEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(SettlementLine.id("young_samurai"), "main");

    public YoungSamuraiModel(ModelPart root) { super(root, false); }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = PlayerModel.createMesh(CubeDeformation.NONE, false);
        var root = mesh.getRoot();
        var head = root.getChild("head");
        head.addOrReplaceChild("hair_cap", CubeListBuilder.create().texOffs(32, 0)
            .addBox(-4.5F, -8.6F, -4.5F, 9F, 4F, 9F, new CubeDeformation(.15F)), PartPose.ZERO);
        head.addOrReplaceChild("hair_bun", CubeListBuilder.create().texOffs(0, 40)
            .addBox(-3F, -11.5F, 1.5F, 6F, 4F, 4F, new CubeDeformation(.1F)), PartPose.ZERO);
        head.addOrReplaceChild("red_flower", CubeListBuilder.create().texOffs(20, 40)
            .addBox(-5.7F, -11.2F, -.6F, 4F, 4F, 1F).addBox(-4.7F, -12.2F, -.5F, 2F, 6F, 1F), PartPose.ZERO);
        head.addOrReplaceChild("purple_plume", CubeListBuilder.create().texOffs(32, 40)
            .addBox(2.5F, -12.5F, 1F, 1F, 5F, 3F).addBox(3.5F, -11.5F, 2F, 1F, 4F, 2F), PartPose.ZERO);
        head.addOrReplaceChild("forehead_cords", CubeListBuilder.create().texOffs(48, 40)
            .addBox(-3F, -8.1F, -4.8F, 6F, 1F, 1F).addBox(-2.5F, -7F, -4.7F, 1F, 3F, 1F)
            .addBox(.5F, -7F, -4.7F, 1F, 3F, 1F), PartPose.ZERO);

        var body = root.getChild("body");
        body.addOrReplaceChild("kimono_collar", CubeListBuilder.create().texOffs(0, 48)
            .addBox(-4.4F, -.4F, -2.4F, 9F, 4F, 5F, new CubeDeformation(.12F)), PartPose.ZERO);
        body.addOrReplaceChild("obi", CubeListBuilder.create().texOffs(28, 48)
            .addBox(-4.6F, 7F, -2.5F, 9F, 4F, 5F, new CubeDeformation(.16F)), PartPose.ZERO);
        body.addOrReplaceChild("front_skirt", CubeListBuilder.create().texOffs(0, 57)
            .addBox(-4.4F, 10F, -2.5F, 9F, 8F, 1F, new CubeDeformation(.08F)), PartPose.ZERO);
        body.addOrReplaceChild("back_skirt", CubeListBuilder.create().texOffs(20, 57)
            .addBox(-4.4F, 10F, 1.5F, 9F, 8F, 1F, new CubeDeformation(.08F)), PartPose.ZERO);
        body.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 57)
            .addBox(-9F, 1F, -2.8F, 5F, 10F, 5F, new CubeDeformation(.12F)), PartPose.ZERO);
        body.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(60, 57)
            .addBox(4F, 1F, -2.8F, 5F, 10F, 5F, new CubeDeformation(.12F)), PartPose.ZERO);
        body.addOrReplaceChild("upper_scabbard", CubeListBuilder.create().texOffs(0, 72)
            .addBox(-7F, 9F, 2.6F, 14F, 2F, 2F, new CubeDeformation(.05F)), PartPose.rotation(0F, 0F, -.18F));
        body.addOrReplaceChild("lower_scabbard", CubeListBuilder.create().texOffs(32, 72)
            .addBox(-7F, 12F, 2.7F, 14F, 2F, 2F, new CubeDeformation(.05F)), PartPose.rotation(0F, 0F, .22F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override public void setupAnim(YoungSamuraiEntity entity, float limbSwing, float limbAmount, float age, float yaw, float pitch) {
        rightArmPose = HumanoidModel.ArmPose.ITEM;
        super.setupAnim(entity, limbSwing, limbAmount, age, yaw, pitch);
        YoungSamuraiEntity.Action action = entity.getAction();
        float tick = entity.getActionTick();
        switch (action) {
            case PHASE -> {
                float lift = Mth.sin(tick * .28F) * .18F;
                rightArm.xRot = -1.2F - lift; leftArm.xRot = -1.2F + lift;
                rightArm.zRot = .65F; leftArm.zRot = -.65F;
            }
            case CRIMSON_DRAW -> {
                body.yRot = -.45F; rightArm.xRot = -2.05F; rightArm.yRot = -.7F;
                leftArm.xRot = -1.15F; leftArm.yRot = .6F; rightLeg.xRot = -.55F; leftLeg.xRot = .55F;
            }
            case CRESCENT_SWEEP -> {
                body.yRot = Mth.sin(tick * .18F) * .75F; rightArm.xRot = -1.55F; rightArm.yRot = -1.05F;
                leftArm.xRot = -1.25F; leftArm.yRot = .5F;
            }
            case RISING_KNEE -> {
                rightLeg.xRot = -1.45F; leftLeg.xRot = .4F; rightArm.xRot = -1.6F; leftArm.xRot = .25F;
                body.xRot = -.22F;
            }
            case LOTUS_BARRAGE -> {
                float cross = Mth.sin(tick * .8F);
                rightArm.xRot = -1.55F + cross * .7F; leftArm.xRot = -1.4F - cross * .6F;
                rightArm.yRot = -.6F * cross; body.yRot = .35F * cross;
            }
            case FLASH_STEP -> {
                body.xRot = .36F; rightArm.xRot = -2.45F; rightArm.yRot = -.35F;
                leftArm.xRot = .55F; rightLeg.xRot = -.8F; leftLeg.xRot = .65F;
            }
            default -> {
                if (entity.hasAura()) {
                    rightArm.zRot += Mth.sin(age * .18F) * .08F;
                    leftArm.zRot -= Mth.sin(age * .18F) * .08F;
                }
            }
        }
    }
}

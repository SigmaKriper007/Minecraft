package com.opus.darkforest.client.model;

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

public final class DarkForestArmorModel<T extends LivingEntity> extends EntityModel<T> {
    public static final ModelLayerLocation BRIAR_LAYER = new ModelLayerLocation(new ResourceLocation("opusvsexe", "briarweave_armor"), "main");
    public static final ModelLayerLocation VESTMENTS_LAYER = new ModelLayerLocation(new ResourceLocation("opusvsexe", "dark_forest_vestments"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public DarkForestArmorModel(ModelPart root) {
        this.root = root;
        head = root.getChild("head");
        body = root.getChild("body");
        rightArm = root.getChild("right_arm");
        leftArm = root.getChild("left_arm");
        rightLeg = root.getChild("right_leg");
        leftLeg = root.getChild("left_leg");
    }

    public ModelPart head() { return head; }
    public ModelPart body() { return body; }
    public ModelPart rightArm() { return rightArm; }
    public ModelPart leftArm() { return leftArm; }
    public ModelPart rightLeg() { return rightLeg; }
    public ModelPart leftLeg() { return leftLeg; }

    public void setPieces(boolean helmet, boolean chest, boolean leggings, boolean boots) {
        head.visible = helmet;
        body.visible = chest || leggings;
        body.getChild("chest").visible = chest;
        body.getChild("waist").visible = chest || leggings;
        body.getChild("heart").visible = chest;
        rightArm.visible = chest;
        leftArm.visible = chest;
        rightLeg.visible = leggings || boots;
        leftLeg.visible = leggings || boots;
        for (ModelPart leg : new ModelPart[]{rightLeg, leftLeg}) {
            leg.getChild("leg").visible = leggings;
            leg.getChild("boot").visible = boots;
        }
    }

    public static LayerDefinition createBriarLayer() { return create(false); }
    public static LayerDefinition createVestmentsLayer() { return create(true); }

    private static LayerDefinition create(boolean vestments) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        CubeDeformation shell = new CubeDeformation(vestments ? .32F : .24F);
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        head.addOrReplaceChild("hood", CubeListBuilder.create().texOffs(0, 0).addBox(-4.25F, -8.35F, -4.25F, 8.5F, 8.5F, 8.5F, shell), PartPose.ZERO);
        head.addOrReplaceChild("crown", CubeListBuilder.create().texOffs(36, 0).addBox(-4.5F, -9.3F, -1.5F, 9F, 1.4F, 3F), PartPose.ZERO);
        if (vestments) {
            head.addOrReplaceChild("antler_left", CubeListBuilder.create().texOffs(58, 0).addBox(0, -5F, -.5F, 1F, 6F, 1F), PartPose.offsetAndRotation(3F, -8F, 0, 0, 0, -.38F));
            head.addOrReplaceChild("antler_right", CubeListBuilder.create().texOffs(62, 0).addBox(-1F, -5F, -.5F, 1F, 6F, 1F), PartPose.offsetAndRotation(-3F, -8F, 0, 0, 0, .38F));
        }

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 20).addBox(-4.3F, -.15F, -2.35F, 8.6F, 8.4F, 4.7F, shell), PartPose.ZERO);
        body.addOrReplaceChild("waist", CubeListBuilder.create().texOffs(0, 34).addBox(-4.5F, 7.4F, -2.55F, 9F, 4.5F, 5.1F, new CubeDeformation(.12F)), PartPose.ZERO);
        body.addOrReplaceChild("heart", vestments ? CubeListBuilder.create().texOffs(34, 22).addBox(-1.5F, 1.4F, -3.2F, 3F, 3F, .8F) : CubeListBuilder.create(), PartPose.ZERO);
        arm(root, "right_arm", -5F, true, vestments ? 70 : 46);
        arm(root, "left_arm", 5F, false, vestments ? 88 : 62);
        leg(root, "right_leg", -1.9F, vestments ? 48 : 0);
        leg(root, "left_leg", 1.9F, vestments ? 72 : 24);
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static void arm(PartDefinition root, String name, float x, boolean right, int u) {
        PartDefinition arm = root.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.offset(x, 2, 0));
        arm.addOrReplaceChild("sleeve", CubeListBuilder.create().texOffs(u, 18).addBox(right ? -3.1F : -1.1F, -2.4F, -2.55F, 4.2F, 13F, 5.1F, new CubeDeformation(.12F)), PartPose.ZERO);
    }

    private static void leg(PartDefinition root, String name, float x, int u) {
        PartDefinition leg = root.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.offset(x, 12, 0));
        leg.addOrReplaceChild("leg", CubeListBuilder.create().texOffs(u, 48).addBox(-2.05F, 0, -2.3F, 4.1F, 7.2F, 4.6F, new CubeDeformation(.1F)), PartPose.ZERO);
        leg.addOrReplaceChild("boot", CubeListBuilder.create().texOffs(u, 62).addBox(-2.1F, 6.4F, -2.75F, 4.2F, 5.5F, 5.5F, new CubeDeformation(.1F)), PartPose.ZERO);
    }

    @Override public void setupAnim(T entity, float limbSwing, float limbAmount, float age, float yaw, float pitch) { }
    @Override public void renderToBuffer(PoseStack pose, VertexConsumer out, int light, int overlay, float r, float g, float b, float a) { root.render(pose, out, light, overlay, r, g, b, a); }
}

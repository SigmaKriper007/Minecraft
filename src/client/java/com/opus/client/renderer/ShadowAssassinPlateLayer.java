package com.opus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.opus.client.model.ShadowAssassinModel;
import com.opus.registry.ModItems;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class ShadowAssassinPlateLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

    private static final ResourceLocation PLATE_TEXTURE = new ResourceLocation("opusvsexe:textures/entity/shadow_assassin_plate.png");

    private final ShadowAssassinModel<T> plateModel;

    public ShadowAssassinPlateLayer(RenderLayerParent<T, M> parent, ShadowAssassinModel<T> plateModel) {
        super(parent);
        this.plateModel = plateModel;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        boolean chest = entity.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.SHADOW_CHESTPLATE);
        boolean legs = entity.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.SHADOW_LEGGINGS);
        boolean boots = entity.getItemBySlot(EquipmentSlot.FEET).is(ModItems.SHADOW_BOOTS);
        if (!chest && !legs && !boots) {
            return;
        }

        this.plateModel.body().copyFrom(this.getParentModel().body);
        this.plateModel.rightArm().copyFrom(this.getParentModel().rightArm);
        this.plateModel.leftArm().copyFrom(this.getParentModel().leftArm);
        this.plateModel.rightLeg().copyFrom(this.getParentModel().rightLeg);
        this.plateModel.leftLeg().copyFrom(this.getParentModel().leftLeg);

        this.plateModel.body().visible = chest || legs;
        this.plateModel.rightArm().visible = chest;
        this.plateModel.leftArm().visible = chest;
        this.plateModel.rightLeg().visible = legs || boots;
        this.plateModel.leftLeg().visible = legs || boots;

        if (chest || legs) {
            this.animateCloak(entity, limbSwing, limbSwingAmount, ageInTicks);
        }

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(PLATE_TEXTURE));
        this.plateModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void animateCloak(T entity, float limbSwing, float limbSwingAmount, float ageInTicks) {
        ModelPart body = this.plateModel.body();

        float walkSway = Mth.sin(limbSwing * 1.15F) * limbSwingAmount * 0.38F;
        float idleSway = Mth.sin(ageInTicks * 0.07F) * 0.035F;
        float sway = (walkSway + idleSway) * 0.5F;

        float fallLift = 0.0F;
        if (!entity.onGround()) {
            float vy = (float) entity.getDeltaMovement().y;
            if (vy < 0.0F) {
                fallLift = Math.min(-vy * 0.55F, 0.85F);
            }
        }
        float lift = fallLift;

        ModelPart base = body.getChild("cloak_base");
        base.xRot = lift * 0.5F;
        base.zRot = sway * 0.85F;
        body.getChild("cloak_outer_top").zRot = sway * 0.8F;
        body.getChild("cloak_outer_mid").zRot = sway * 0.65F;
        body.getChild("cloak_outer_bot").zRot = sway * 0.5F;
        body.getChild("cloak_hem_red").zRot = sway * 0.55F;
        body.getChild("cloak_inner_red_top").zRot = sway * 0.75F;
        body.getChild("cloak_inner_red_mid").zRot = sway * 0.6F;
        body.getChild("cloak_inner_red_bot").zRot = sway * 0.45F;

        ModelPart clothL = body.getChild("front_cloth_left");
        ModelPart clothR = body.getChild("front_cloth_right");
        clothL.zRot = sway * 0.5F;
        clothR.zRot = -sway * 0.5F;
        clothL.xRot = lift * 0.25F;
        clothR.xRot = lift * 0.25F;
    }
}
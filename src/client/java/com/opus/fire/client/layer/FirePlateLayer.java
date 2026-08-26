package com.opus.fire.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.opus.fire.client.model.FirePlateModel;
import com.opus.fire.registry.FireItems;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class FirePlateLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("opusvsexe:textures/fire/armor/fire_plate.png");
    private final FirePlateModel<T> model;

    public FirePlateLayer(RenderLayerParent<T,M> parent, FirePlateModel<T> model) { super(parent); this.model=model; }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffers, int light, T entity, float limbSwing,
                       float limbAmount, float partialTick, float age, float yaw, float pitch) {
        boolean chest=entity.getItemBySlot(EquipmentSlot.CHEST).is(FireItems.FIRE_CHESTPLATE);
        boolean legs=entity.getItemBySlot(EquipmentSlot.LEGS).is(FireItems.FIRE_LEGGINGS);
        boolean boots=entity.getItemBySlot(EquipmentSlot.FEET).is(FireItems.FIRE_BOOTS);
        if (!chest&&!legs&&!boots) return;

        model.body().copyFrom(getParentModel().body);
        model.belt().copyFrom(getParentModel().body);
        model.rightArm().copyFrom(getParentModel().rightArm);
        model.leftArm().copyFrom(getParentModel().leftArm);
        model.rightLeg().copyFrom(getParentModel().rightLeg);
        model.leftLeg().copyFrom(getParentModel().leftLeg);
        model.body().visible=chest; model.belt().visible=chest||legs;
        model.rightArm().visible=chest; model.leftArm().visible=chest;
        model.rightLeg().visible=legs||boots; model.leftLeg().visible=legs||boots;
        model.setLegVisibility(legs,boots);
        model.wingLeft().visible=false;
        model.wingRight().visible=false;

        VertexConsumer out=buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(pose,out,light,OverlayTexture.NO_OVERLAY,1,1,1,1);
    }

    private void animateWings(T entity, boolean visible, float time) {
        model.wingLeft().visible=visible; model.wingRight().visible=visible;
        if (!visible) return;
        var body=getParentModel().body;
        model.wingLeft().x=body.x+3.8f; model.wingRight().x=body.x-3.8f;
        model.wingLeft().y=model.wingRight().y=body.y+4.0f;
        model.wingLeft().z=model.wingRight().z=body.z+2.6f;
        boolean flying=entity instanceof Player player&&player.getAbilities().flying;
        boolean glide=entity.isFallFlying();
        float beat=flying?(float)Math.sin(time*0.72f):glide?(float)Math.sin(time*0.18f)*0.08f:0.0f;
        float targetLeft=flying?-0.74f+beat*0.55f:glide?-1.18f:-0.20f;
        float targetRight=-targetLeft;
        model.wingLeft().zRot=Mth.lerp(0.18f,model.wingLeft().zRot,targetLeft);
        model.wingRight().zRot=Mth.lerp(0.18f,model.wingRight().zRot,targetRight);
        float targetX=flying?0.12f:glide?0.02f:0.58f;
        model.wingLeft().xRot=Mth.lerp(0.16f,model.wingLeft().xRot,targetX+body.xRot);
        model.wingRight().xRot=Mth.lerp(0.16f,model.wingRight().xRot,targetX+body.xRot);
        float fold=flying?0.35f:glide?0.10f:0.88f;
        model.wingLeftForearm().zRot=Mth.lerp(0.2f,model.wingLeftForearm().zRot,-fold);
        model.wingRightForearm().zRot=Mth.lerp(0.2f,model.wingRightForearm().zRot,fold);
    }
}

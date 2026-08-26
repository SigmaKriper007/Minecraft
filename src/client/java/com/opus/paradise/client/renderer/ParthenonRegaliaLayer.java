package com.opus.paradise.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.opus.paradise.client.model.ParthenonRegaliaModel;
import com.opus.paradise.registry.ParadiseItems;
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
import net.minecraft.world.entity.player.Player;

public final class ParthenonRegaliaLayer<T extends LivingEntity,M extends HumanoidModel<T>> extends RenderLayer<T,M>{
    private static final ResourceLocation TEXTURE=new ResourceLocation("opusvsexe:textures/paradise/armor/parthenon_regalia.png");
    private final ParthenonRegaliaModel<T> model;
    public ParthenonRegaliaLayer(RenderLayerParent<T,M> parent,ParthenonRegaliaModel<T> model){super(parent);this.model=model;}
    @Override public void render(PoseStack pose,MultiBufferSource buffers,int light,T entity,float limbSwing,float limbAmount,float partialTick,float age,float yaw,float pitch){
        boolean helm=entity.getItemBySlot(EquipmentSlot.HEAD).is(ParadiseItems.PARTHENON_HELMET),chest=entity.getItemBySlot(EquipmentSlot.CHEST).is(ParadiseItems.PARTHENON_CHESTPLATE),legs=entity.getItemBySlot(EquipmentSlot.LEGS).is(ParadiseItems.PARTHENON_LEGGINGS),boots=entity.getItemBySlot(EquipmentSlot.FEET).is(ParadiseItems.PARTHENON_BOOTS);if(!helm&&!chest&&!legs&&!boots)return;
        model.head().copyFrom(getParentModel().head);model.body().copyFrom(getParentModel().body);model.rightArm().copyFrom(getParentModel().rightArm);model.leftArm().copyFrom(getParentModel().leftArm);model.rightLeg().copyFrom(getParentModel().rightLeg);model.leftLeg().copyFrom(getParentModel().leftLeg);
        model.head().visible=helm;model.body().visible=chest||legs;model.setTorsoParts(chest,legs);model.rightArm().visible=chest;model.leftArm().visible=chest;model.rightLeg().visible=legs||boots;model.leftLeg().visible=legs||boots;model.setLegParts(legs,boots);animate(entity,chest,age+partialTick);
        VertexConsumer out=buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));model.renderToBuffer(pose,out,light,OverlayTexture.NO_OVERLAY,1,1,1,1);
    }
    private void animate(T entity,boolean visible,float time){
        model.halo().yRot=time*.025F;ModelPart body=getParentModel().body;align(model.wingLeft(),body,4F);align(model.wingRight(),body,-4F);model.wingLeft().visible=visible;model.wingRight().visible=visible;if(!visible)return;
        boolean flying=entity instanceof Player player&&player.getAbilities().flying;boolean glide=entity.isFallFlying()||(!entity.onGround()&&entity.getDeltaMovement().y<-.12);float pulse=(float)Math.sin(time*(flying?.72F:.16F));float spread=glide?-.78F:flying?-.42F+pulse*.38F:.18F+pulse*.025F;
        model.wingLeft().zRot=Mth.lerp(.22F,model.wingLeft().zRot,spread);model.wingRight().zRot=Mth.lerp(.22F,model.wingRight().zRot,-spread);model.wingLeft().xRot=model.wingRight().xRot=glide?-.12F:(float)Math.sin(time*.11F)*.025F;
    }
    private static void align(ModelPart wing,ModelPart body,float x){wing.x=body.x+x;wing.y=body.y+3F;wing.z=body.z+2.7F;}
}

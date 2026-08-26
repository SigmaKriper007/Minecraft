package com.opus.ember.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.opus.ember.client.model.EmberPlateModel;
import com.opus.ember.registry.EmberItems;
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

/**
 * Одна пара пепельных мембранных крыльев на кирасе «Сердце Углей».
 * Оба крыла машут как ЕДИНОЕ целое (зеркальный симметричный мах).
 *
 * Управление:
 *  - wing.zRot   — общий мах (+вниз, −вверх);
 *  - finger_i.zRot      — раскрытие/смыкание веера (0 = раскрыто, + = сомкнуто);
 *  - finger_i_tip.zRot  — волна трепета кончиков;
 *  - лёгкое дыхание через wing.xRot.
 */
public final class EmberPlateLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("opusvsexe:textures/ember/armor/ember_plate.png");
    // сомкнутые пальцы (rad): на земле пальцы подбираются вверх к кости
    private static final float[] FOLD_D = {-0.15f, -0.35f, -0.55f, -0.70f};
    private final EmberPlateModel<T> model;

    public EmberPlateLayer(RenderLayerParent<T,M> parent, EmberPlateModel<T> model) { super(parent); this.model=model; }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffers, int light, T entity, float limbSwing,
                       float limbAmount, float partialTick, float age, float yaw, float pitch) {
        boolean chest=entity.getItemBySlot(EquipmentSlot.CHEST).is(EmberItems.EMBER_CHESTPLATE);
        boolean legs=entity.getItemBySlot(EquipmentSlot.LEGS).is(EmberItems.EMBER_LEGGINGS);
        boolean boots=entity.getItemBySlot(EquipmentSlot.FEET).is(EmberItems.EMBER_BOOTS);
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
        animateWings(entity,chest,age+partialTick,limbAmount);

        VertexConsumer out=buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(pose,out,light,OverlayTexture.NO_OVERLAY,1,1,1,1);
    }

    private void animateWings(T entity, boolean visible, float time, float limbAmount) {
        model.wingLeft().visible=visible; model.wingRight().visible=visible;
        if (!visible) return;
        var body=getParentModel().body;

        align(model.wingLeft(), model.wingRight(), body);

        boolean flying=entity instanceof Player player&&player.getAbilities().flying;
        boolean glide=entity.isFallFlying();
        boolean moving=!flying&&!glide&&(limbAmount>0.15f);

        // Единый симметричный мах (без сдвига фазы между крыльями)
        float beat=flying?(float)Math.sin(time*0.75f)
                :glide?(float)Math.sin(time*0.16f)*0.10f
                :moving?(float)Math.sin(time*0.5f)*0.06f
                :(float)Math.sin(time*0.7f)*0.02f;

        flap(flying,glide,moving,beat,time);
        fan(flying,glide,moving,time,beat);
    }

    private void align(ModelPart left, ModelPart right, ModelPart body) {
        left.x=body.x+3.8f; right.x=body.x-3.8f;
        left.y=right.y=body.y+3.2f;
        left.z=right.z=body.z+2.6f;
    }

    private void flap(boolean flying, boolean glide, boolean moving, float beat, float time) {
        float target;
        if (flying)      target=-0.35f+beat*0.5f;      // мах вверх-вниз
        else if (glide)  target=-0.62f+beat;            // широко раскрыто
        else if (moving) target=+0.05f+beat;
        else             target=+0.15f+beat;            // расслабленное свисание
        model.wingLeft().zRot=Mth.lerp(0.18f,model.wingLeft().zRot,target);
        model.wingRight().zRot=Mth.lerp(0.18f,model.wingRight().zRot,-target);
        // лёгкое дыхание
        model.wingLeft().xRot=Mth.lerp(0.08f,model.wingLeft().xRot,(float)Math.sin(time*0.9f)*0.02f);
        model.wingRight().xRot=Mth.lerp(0.08f,model.wingRight().xRot,(float)Math.sin(time*0.9f)*0.02f);
    }

    private void fan(boolean flying, boolean glide, boolean moving, float time, float beat) {
        for (int i = 0; i < 4; i++) {
            var fL=model.finger(model.wingLeft(),i);
            var fR=model.finger(model.wingRight(),i);
            var tL=model.fingerTip(model.wingLeft(),i);
            var tR=model.fingerTip(model.wingRight(),i);

            // волна трепета по пальцам (отставание от корня к кончику)
            float flutter=flying?(float)Math.sin(time*1.6f - i*0.5f)*0.06f:0.0f;
            float base;
            if (flying)      base=0.03f;
            else if (glide)  base=0.06f;
            else             base=FOLD_D[i];
            float target;
            if (flying||glide) target=base+flutter;
            else if (moving)   target=base*(0.4f+0.1f*i)+flutter;
            else               target=base+flutter*0.2f;

            fL.zRot=Mth.lerp(0.16f,fL.zRot,target);
            fR.zRot=Mth.lerp(0.16f,fR.zRot,-target);

            float tT=flying?(float)Math.sin(time*1.6f - i*0.5f - 0.6f)*0.05f
                    :glide?(float)Math.sin(time*0.9f - i*0.6f)*0.02f
                    :0.0f;
            tL.zRot=Mth.lerp(0.18f,tL.zRot,tT);
            tR.zRot=Mth.lerp(0.18f,tR.zRot,-tT);
        }
    }
}

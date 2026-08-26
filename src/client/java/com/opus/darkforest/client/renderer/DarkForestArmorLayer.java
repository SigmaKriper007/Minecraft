package com.opus.darkforest.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.opus.darkforest.client.model.DarkForestArmorModel;
import com.opus.darkforest.registry.DarkForestItems;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

public final class DarkForestArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private final DarkForestArmorModel<T> model;
    private final boolean vestments;
    private final ResourceLocation texture;

    public DarkForestArmorLayer(RenderLayerParent<T, M> parent, DarkForestArmorModel<T> model, boolean vestments) {
        super(parent);
        this.model = model;
        this.vestments = vestments;
        texture = new ResourceLocation("opusvsexe", vestments ? "textures/dark_forest/armor/dark_forest_vestments.png" : "textures/dark_forest/armor/briarweave.png");
    }

    @Override public void render(PoseStack pose, MultiBufferSource buffers, int light, T entity, float limbSwing, float limbAmount, float partialTick, float age, float yaw, float pitch) {
        Item helmet = vestments ? DarkForestItems.DARK_FOREST_HELMET : DarkForestItems.BRIARWEAVE_HELMET;
        Item chestplate = vestments ? DarkForestItems.DARK_FOREST_CHESTPLATE : DarkForestItems.BRIARWEAVE_CHESTPLATE;
        Item leggings = vestments ? DarkForestItems.DARK_FOREST_LEGGINGS : DarkForestItems.BRIARWEAVE_LEGGINGS;
        Item boots = vestments ? DarkForestItems.DARK_FOREST_BOOTS : DarkForestItems.BRIARWEAVE_BOOTS;
        boolean head = entity.getItemBySlot(EquipmentSlot.HEAD).is(helmet);
        boolean chest = entity.getItemBySlot(EquipmentSlot.CHEST).is(chestplate);
        boolean legs = entity.getItemBySlot(EquipmentSlot.LEGS).is(leggings);
        boolean feet = entity.getItemBySlot(EquipmentSlot.FEET).is(boots);
        if (!head && !chest && !legs && !feet) return;
        model.head().copyFrom(getParentModel().head);
        model.body().copyFrom(getParentModel().body);
        model.rightArm().copyFrom(getParentModel().rightArm);
        model.leftArm().copyFrom(getParentModel().leftArm);
        model.rightLeg().copyFrom(getParentModel().rightLeg);
        model.leftLeg().copyFrom(getParentModel().leftLeg);
        model.setPieces(head, chest, legs, feet);
        VertexConsumer out = buffers.getBuffer(RenderType.entityCutoutNoCull(texture));
        model.renderToBuffer(pose, out, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
    }
}

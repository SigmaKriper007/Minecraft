package com.opus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class PlaceholderMobRenderer<T extends Mob> extends MobRenderer<T, HumanoidModel<T>> {
    private static final ResourceLocation HAIKU_TEXTURE = new ResourceLocation("opusvsexe:textures/entity/haiku_ai.png");
    private static final ResourceLocation EXO_TEXTURE = new ResourceLocation("opusvsexe:textures/entity/exo_suit.png");
    private static final float BASE_WIDTH = 0.6f;
    private static final float BASE_HEIGHT = 1.8f;

    public PlaceholderMobRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.7f);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entity instanceof ExosuitEntity ? EXO_TEXTURE : HAIKU_TEXTURE;
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTickTime) {
        float s = entity.getBbHeight() / BASE_HEIGHT;
        poseStack.scale(s, s, s);
    }
}
package com.opus.darkforest.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.opus.darkforest.client.model.MoonwingPulseModel;
import com.opus.darkforest.entity.MoonwingPulseEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class MoonwingPulseRenderer extends GeoEntityRenderer<MoonwingPulseEntity> {
    public MoonwingPulseRenderer(EntityRendererProvider.Context context) { super(context, new MoonwingPulseModel()); shadowRadius = 0; }
    @Override public void render(MoonwingPulseEntity entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        float scale = (float)(entity.radius() * 2); pose.scale(scale, .8F, scale); super.render(entity, yaw, partialTick, pose, buffers, light);
    }
}

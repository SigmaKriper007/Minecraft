package com.opus.darkforest.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.concurrent.ConcurrentHashMap;

/** Renders a pre-baked <code>_emissive.png</code> mask full-bright over the model. Unlike GeckoLib's
 * AutoGlowingGeoLayer it never mutates the base texture at runtime and never registers dynamic textures,
 * so a missing/broken mask can only skip the glow, never black out the model. */
public final class EmissiveGlowGeoLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {
    private static final String SUFFIX = "_emissive";
    private static final ConcurrentHashMap<ResourceLocation, Boolean> PRESENT = new ConcurrentHashMap<>();

    public EmissiveGlowGeoLayer(GeoRenderer<T> renderer) {super(renderer);}

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        ResourceLocation base = getTextureResource(animatable);
        ResourceLocation emissive = new ResourceLocation(base.getNamespace(), base.getPath().replace(".png", SUFFIX + ".png"));
        if (!PRESENT.computeIfAbsent(emissive, id -> Minecraft.getInstance().getResourceManager().getResource(id).isPresent())) return;
        RenderType glowType = RenderType.entityTranslucentEmissive(emissive);
        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, glowType, bufferSource.getBuffer(glowType), partialTick, LightTexture.FULL_BRIGHT, packedOverlay, 1, 1, 1, 1);
    }
}

package com.opus.darkforest.client.model;

import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.entity.GloomWebEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class GloomWebModel extends GeoModel<GloomWebEntity> {
    @Override public ResourceLocation getModelResource(GloomWebEntity entity) { return DarkForestLine.id("geo/dark_forest/gloom_web.geo.json"); }
    @Override public ResourceLocation getTextureResource(GloomWebEntity entity) { return DarkForestLine.id("textures/dark_forest/entity/gloom_web.png"); }
    @Override public ResourceLocation getAnimationResource(GloomWebEntity entity) { return DarkForestLine.id("animations/dark_forest/effects.animation.json"); }
    @Override public RenderType getRenderType(GloomWebEntity entity, ResourceLocation texture) { return RenderType.entityTranslucent(texture); }
    @Override public boolean crashIfBoneMissing() { return true; }
}

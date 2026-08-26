package com.opus.darkforest.client.model;

import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.entity.MoonwingPulseEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class MoonwingPulseModel extends GeoModel<MoonwingPulseEntity> {
    @Override public ResourceLocation getModelResource(MoonwingPulseEntity entity) { return DarkForestLine.id("geo/dark_forest/moonwing_pulse.geo.json"); }
    @Override public ResourceLocation getTextureResource(MoonwingPulseEntity entity) { return DarkForestLine.id("textures/dark_forest/entity/moonwing_pulse.png"); }
    @Override public ResourceLocation getAnimationResource(MoonwingPulseEntity entity) { return DarkForestLine.id("animations/dark_forest/effects.animation.json"); }
    @Override public RenderType getRenderType(MoonwingPulseEntity entity, ResourceLocation texture) { return RenderType.entityTranslucentEmissive(texture); }
    @Override public boolean crashIfBoneMissing() { return true; }
}

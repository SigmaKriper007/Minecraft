package com.opus.darkforest.client.model;

import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.entity.GloomBroodmotherEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class GloomBroodmotherModel extends GeoModel<GloomBroodmotherEntity> {
    @Override public ResourceLocation getModelResource(GloomBroodmotherEntity entity) { return DarkForestLine.id("geo/dark_forest/gloom_broodmother.geo.json"); }
    @Override public ResourceLocation getTextureResource(GloomBroodmotherEntity entity) { return DarkForestLine.id("textures/dark_forest/entity/gloom_broodmother.png"); }
    @Override public ResourceLocation getAnimationResource(GloomBroodmotherEntity entity) { return DarkForestLine.id("animations/dark_forest/gloom_broodmother.animation.json"); }
    @Override public boolean crashIfBoneMissing() { return true; }
}

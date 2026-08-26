package com.opus.darkforest.client.model;

import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.entity.ShadeSpiderlingEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class ShadeSpiderlingModel extends GeoModel<ShadeSpiderlingEntity> {
    @Override public ResourceLocation getModelResource(ShadeSpiderlingEntity entity) { return DarkForestLine.id("geo/dark_forest/shade_spiderling.geo.json"); }
    @Override public ResourceLocation getTextureResource(ShadeSpiderlingEntity entity) { return DarkForestLine.id("textures/dark_forest/entity/shade_spiderling.png"); }
    @Override public ResourceLocation getAnimationResource(ShadeSpiderlingEntity entity) { return DarkForestLine.id("animations/dark_forest/shade_spiderling.animation.json"); }
    @Override public boolean crashIfBoneMissing() { return true; }
}

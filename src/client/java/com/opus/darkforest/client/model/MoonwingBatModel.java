package com.opus.darkforest.client.model;

import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.entity.MoonwingBatEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class MoonwingBatModel extends GeoModel<MoonwingBatEntity> {
    @Override public ResourceLocation getModelResource(MoonwingBatEntity entity) { return DarkForestLine.id("geo/dark_forest/moonwing_bat.geo.json"); }
    @Override public ResourceLocation getTextureResource(MoonwingBatEntity entity) { return DarkForestLine.id("textures/dark_forest/entity/moonwing_bat.png"); }
    @Override public ResourceLocation getAnimationResource(MoonwingBatEntity entity) { return DarkForestLine.id("animations/dark_forest/moonwing_bat.animation.json"); }
    @Override public boolean crashIfBoneMissing() { return true; }
}

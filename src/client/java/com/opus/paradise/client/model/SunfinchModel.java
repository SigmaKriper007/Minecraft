package com.opus.paradise.client.model;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.entity.SunfinchEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class SunfinchModel extends GeoModel<SunfinchEntity> {
    @Override public ResourceLocation getModelResource(SunfinchEntity entity) { return ParadiseLine.id("geo/paradise/sunfinch.geo.json"); }
    @Override public ResourceLocation getTextureResource(SunfinchEntity entity) { return ParadiseLine.id("textures/paradise/entity/sunfinch.png"); }
    @Override public ResourceLocation getAnimationResource(SunfinchEntity entity) { return ParadiseLine.id("animations/paradise/sunfinch.animation.json"); }
    @Override public boolean crashIfBoneMissing() { return true; }
}

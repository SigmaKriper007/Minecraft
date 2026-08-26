package com.opus.paradise.client.model;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.entity.CloudGrazerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class CloudGrazerModel extends GeoModel<CloudGrazerEntity> {
    @Override public ResourceLocation getModelResource(CloudGrazerEntity entity) { return ParadiseLine.id("geo/paradise/cloud_grazer.geo.json"); }
    @Override public ResourceLocation getTextureResource(CloudGrazerEntity entity) { return ParadiseLine.id("textures/paradise/entity/cloud_grazer.png"); }
    @Override public ResourceLocation getAnimationResource(CloudGrazerEntity entity) { return ParadiseLine.id("animations/paradise/cloud_grazer.animation.json"); }
    @Override public boolean crashIfBoneMissing() { return true; }
}

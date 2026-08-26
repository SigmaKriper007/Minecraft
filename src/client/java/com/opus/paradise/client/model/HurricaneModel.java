package com.opus.paradise.client.model;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.entity.HurricaneEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class HurricaneModel extends GeoModel<HurricaneEntity> {
    @Override public ResourceLocation getModelResource(HurricaneEntity entity) { return ParadiseLine.id("geo/paradise/hurricane.geo.json"); }
    @Override public ResourceLocation getTextureResource(HurricaneEntity entity) { return ParadiseLine.id("textures/paradise/entity/hurricane.png"); }
    @Override public ResourceLocation getAnimationResource(HurricaneEntity entity) { return ParadiseLine.id("animations/paradise/hurricane.animation.json"); }
    @Override public RenderType getRenderType(HurricaneEntity entity, ResourceLocation texture) { return RenderType.entityTranslucentEmissive(texture); }
    @Override public boolean crashIfBoneMissing() { return true; }
}

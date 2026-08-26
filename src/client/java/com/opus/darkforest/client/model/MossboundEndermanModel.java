package com.opus.darkforest.client.model;

import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.entity.MossboundEndermanEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class MossboundEndermanModel extends GeoModel<MossboundEndermanEntity> {
    @Override public ResourceLocation getModelResource(MossboundEndermanEntity entity){return DarkForestLine.id("geo/dark_forest/mossbound_enderman.geo.json");}
    @Override public ResourceLocation getTextureResource(MossboundEndermanEntity entity){return DarkForestLine.id("textures/dark_forest/entity/mossbound_enderman.png");}
    @Override public ResourceLocation getAnimationResource(MossboundEndermanEntity entity){return DarkForestLine.id("animations/dark_forest/mossbound_enderman.animation.json");}
    @Override public boolean crashIfBoneMissing(){return true;}
}

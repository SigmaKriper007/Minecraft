package com.opus.paradise.client.model;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.entity.AngelBoyEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class AngelBoyModel extends GeoModel<AngelBoyEntity> {
    @Override public ResourceLocation getModelResource(AngelBoyEntity e){return ParadiseLine.id("geo/paradise/angel_boy.geo.json");}
    @Override public ResourceLocation getTextureResource(AngelBoyEntity e){return ParadiseLine.id("textures/paradise/entity/angel_boy.png");}
    @Override public ResourceLocation getAnimationResource(AngelBoyEntity e){return ParadiseLine.id("animations/paradise/angel_boy.animation.json");}
    @Override public boolean crashIfBoneMissing(){return true;}
}

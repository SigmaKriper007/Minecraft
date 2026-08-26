package com.opus.client.model;

import com.opus.entity.haiku.HaikuMob;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HaikuModel extends GeoModel<HaikuMob> {

    @Override
    public ResourceLocation getModelResource(HaikuMob animatable) {
        return new ResourceLocation("opusvsexe", "geo/entity/haiku.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HaikuMob animatable) {
        return new ResourceLocation("opusvsexe", "textures/entity/haiku_ai.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HaikuMob animatable) {
        return new ResourceLocation("opusvsexe", "animations/entity/haiku.animation.json");
    }
}
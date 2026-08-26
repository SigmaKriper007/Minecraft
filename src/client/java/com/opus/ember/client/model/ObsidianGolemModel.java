package com.opus.ember.client.model;

import com.opus.ember.entity.ObsidianGolemEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ObsidianGolemModel extends GeoModel<ObsidianGolemEntity> {

    @Override
    public ResourceLocation getModelResource(ObsidianGolemEntity animatable) {
        return new ResourceLocation("opusvsexe", "geo/ember/golem.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ObsidianGolemEntity animatable) {
        return new ResourceLocation("opusvsexe", "textures/ember/entity/golem.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ObsidianGolemEntity animatable) {
        return new ResourceLocation("opusvsexe", "animations/ember/golem.animation.json");
    }
}

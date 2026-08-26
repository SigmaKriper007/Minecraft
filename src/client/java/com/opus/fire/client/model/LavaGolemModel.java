package com.opus.fire.client.model;

import com.opus.fire.entity.LavaGolemEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LavaGolemModel extends GeoModel<LavaGolemEntity> {

    @Override
    public ResourceLocation getModelResource(LavaGolemEntity animatable) {
        return new ResourceLocation("opusvsexe", "geo/fire/golem.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LavaGolemEntity animatable) {
        return new ResourceLocation("opusvsexe", "textures/fire/entity/golem.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LavaGolemEntity animatable) {
        return new ResourceLocation("opusvsexe", "animations/fire/golem.animation.json");
    }
}
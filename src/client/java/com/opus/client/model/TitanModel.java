package com.opus.client.model;

import com.opus.entity.haiku.Haiku5Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * Гео-модель Haiku-5 Titan Frame — мини-босс, колосс-мех.
 */
public class TitanModel extends GeoModel<Haiku5Entity> {

    @Override
    public ResourceLocation getModelResource(Haiku5Entity animatable) {
        return new ResourceLocation("opusvsexe", "geo/entity/titan.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Haiku5Entity animatable) {
        return new ResourceLocation("opusvsexe", "textures/entity/titan.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Haiku5Entity animatable) {
        return new ResourceLocation("opusvsexe", "animations/entity/titan.animation.json");
    }
}

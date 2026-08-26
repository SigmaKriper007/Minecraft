package com.opus.client.model;

import com.opus.entity.haiku.Haiku4Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * Гео-модель Haiku-4 Elite Warden — колосс-страж (щитоносец).
 */
public class WardenModel extends GeoModel<Haiku4Entity> {

    @Override
    public ResourceLocation getModelResource(Haiku4Entity animatable) {
        return new ResourceLocation("opusvsexe", "geo/entity/warden.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Haiku4Entity animatable) {
        return new ResourceLocation("opusvsexe", "textures/entity/warden.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Haiku4Entity animatable) {
        return new ResourceLocation("opusvsexe", "animations/entity/warden.animation.json");
    }
}

package com.opus.client.model;

import com.opus.entity.haiku.Haiku2Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * Гео-модель Haiku-2 Scout — колёсный разведчик (WALL-E-язык семейства Haiku).
 */
public class ScoutModel extends GeoModel<Haiku2Entity> {

    @Override
    public ResourceLocation getModelResource(Haiku2Entity animatable) {
        return new ResourceLocation("opusvsexe", "geo/entity/scout.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Haiku2Entity animatable) {
        return new ResourceLocation("opusvsexe", "textures/entity/scout.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Haiku2Entity animatable) {
        return new ResourceLocation("opusvsexe", "animations/entity/scout.animation.json");
    }
}
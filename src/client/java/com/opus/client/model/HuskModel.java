package com.opus.client.model;

import com.opus.entity.haiku.Haiku15Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * Гео-модель Haiku 1.5 Husk (человекоподобный пехотинец).
 * Собственный силуэт: голова-шлем с гребнем, кираса, излучатели, наколенники.
 */
public class HuskModel extends GeoModel<Haiku15Entity> {

    @Override
    public ResourceLocation getModelResource(Haiku15Entity animatable) {
        return new ResourceLocation("opusvsexe", "geo/entity/husk.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Haiku15Entity animatable) {
        return new ResourceLocation("opusvsexe", "textures/entity/husk.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Haiku15Entity animatable) {
        return new ResourceLocation("opusvsexe", "animations/entity/husk.animation.json");
    }
}
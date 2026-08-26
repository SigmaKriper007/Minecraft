package com.opus.client.model;

import com.opus.entity.haiku.Haiku3Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * Гео-модель Haiku-3 Enforcer — тяжёлый двуногий «исполнитель» с пушкой.
 */
public class EnforcerModel extends GeoModel<Haiku3Entity> {

    @Override
    public ResourceLocation getModelResource(Haiku3Entity animatable) {
        return new ResourceLocation("opusvsexe", "geo/entity/enforcer.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Haiku3Entity animatable) {
        return new ResourceLocation("opusvsexe", "textures/entity/enforcer.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Haiku3Entity animatable) {
        return new ResourceLocation("opusvsexe", "animations/entity/enforcer.animation.json");
    }
}

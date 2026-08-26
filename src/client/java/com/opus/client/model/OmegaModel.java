package com.opus.client.model;

import com.opus.entity.haiku.HaikuOmegaEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * Гео-модель Haiku-Ω Omega — финальный босс, ДРЕВНИЙ робот.
 */
public class OmegaModel extends GeoModel<HaikuOmegaEntity> {

    @Override
    public ResourceLocation getModelResource(HaikuOmegaEntity animatable) {
        return new ResourceLocation("opusvsexe", "geo/entity/omega.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HaikuOmegaEntity animatable) {
        return new ResourceLocation("opusvsexe", "textures/entity/omega.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HaikuOmegaEntity animatable) {
        return new ResourceLocation("opusvsexe", "animations/entity/omega.animation.json");
    }
}

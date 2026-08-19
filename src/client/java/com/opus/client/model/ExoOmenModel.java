package com.opus.client.model;

import com.opusvsexe.entity.custom.ExosuitEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ExoOmenModel extends GeoModel<ExosuitEntity> {

    @Override
    public ResourceLocation getModelResource(ExosuitEntity animatable) {
        return new ResourceLocation("opusvsexe", "geo/entity/exo.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ExosuitEntity animatable) {
        return new ResourceLocation("opusvsexe", "textures/entity/exo_omen.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ExosuitEntity animatable) {
        return new ResourceLocation("opusvsexe", "animations/entity/exo.animation.json");
    }
}

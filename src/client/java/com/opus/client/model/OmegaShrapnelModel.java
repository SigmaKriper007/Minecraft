package com.opus.client.model;

import com.opus.entity.omega.OmegaShrapnelEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class OmegaShrapnelModel extends GeoModel<OmegaShrapnelEntity> {

    @Override
    public ResourceLocation getModelResource(OmegaShrapnelEntity animatable) {
        return new ResourceLocation("opusvsexe", "geo/entity/omega_shrapnel.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(OmegaShrapnelEntity animatable) {
        return new ResourceLocation("opusvsexe", "textures/entity/omega_shrapnel.png");
    }

    @Override
    public ResourceLocation getAnimationResource(OmegaShrapnelEntity animatable) {
        return new ResourceLocation("opusvsexe", "animations/entity/omega_shrapnel.animation.json");
    }

    @Override
    public RenderType getRenderType(OmegaShrapnelEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}

package com.opus.client.model;

import com.opus.entity.omega.OmegaSlashEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class OmegaSlashModel extends GeoModel<OmegaSlashEntity> {

    @Override
    public ResourceLocation getModelResource(OmegaSlashEntity animatable) {
        return new ResourceLocation("opusvsexe", "geo/entity/omega_slash.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(OmegaSlashEntity animatable) {
        return new ResourceLocation("opusvsexe", "textures/entity/omega_slash.png");
    }

    @Override
    public ResourceLocation getAnimationResource(OmegaSlashEntity animatable) {
        return new ResourceLocation("opusvsexe", "animations/entity/omega_slash.animation.json");
    }

    @Override
    public RenderType getRenderType(OmegaSlashEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}

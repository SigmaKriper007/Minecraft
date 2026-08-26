package com.opus.client.model;

import com.opus.entity.omega.OmegaSkyLaserEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class OmegaSkyLaserModel extends GeoModel<OmegaSkyLaserEntity> {

    @Override
    public ResourceLocation getModelResource(OmegaSkyLaserEntity animatable) {
        return new ResourceLocation("opusvsexe", "geo/entity/omega_sky_laser.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(OmegaSkyLaserEntity animatable) {
        return new ResourceLocation("opusvsexe", "textures/entity/omega_sky_laser.png");
    }

    @Override
    public ResourceLocation getAnimationResource(OmegaSkyLaserEntity animatable) {
        return new ResourceLocation("opusvsexe", "animations/entity/omega_sky_laser.animation.json");
    }

    @Override
    public RenderType getRenderType(OmegaSkyLaserEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}

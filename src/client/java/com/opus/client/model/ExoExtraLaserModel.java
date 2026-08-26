package com.opus.client.model;

import com.opus.entity.ExtraLaserBeamEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ExoExtraLaserModel extends GeoModel<ExtraLaserBeamEntity> {

    @Override
    public ResourceLocation getModelResource(ExtraLaserBeamEntity animatable) {
        return new ResourceLocation("opusvsexe", "geo/entity/heavy_blaster_beam.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ExtraLaserBeamEntity animatable) {
        return new ResourceLocation("opusvsexe", "textures/entity/extra_laser_beam.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ExtraLaserBeamEntity animatable) {
        return new ResourceLocation("opusvsexe", "animations/entity/heavy_blaster_beam.animation.json");
    }

    @Override
    public RenderType getRenderType(ExtraLaserBeamEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}

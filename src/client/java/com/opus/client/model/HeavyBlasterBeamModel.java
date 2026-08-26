package com.opus.client.model;

import com.opus.entity.HeavyLaserBeamEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HeavyBlasterBeamModel extends GeoModel<HeavyLaserBeamEntity> {

    @Override
    public ResourceLocation getModelResource(HeavyLaserBeamEntity animatable) {
        return new ResourceLocation("opusvsexe", "geo/entity/heavy_blaster_beam.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HeavyLaserBeamEntity animatable) {
        return new ResourceLocation("opusvsexe", "textures/entity/heavy_blaster_beam.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HeavyLaserBeamEntity animatable) {
        return new ResourceLocation("opusvsexe", "animations/entity/heavy_blaster_beam.animation.json");
    }

    @Override
    public RenderType getRenderType(HeavyLaserBeamEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}
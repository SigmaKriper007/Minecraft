package com.opus.client.model;

import com.opus.entity.BlasterBeamEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlasterBeamModel extends GeoModel<BlasterBeamEntity> {

    @Override
    public ResourceLocation getModelResource(BlasterBeamEntity animatable) {
        return new ResourceLocation("opusvsexe", "geo/entity/blaster_beam.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlasterBeamEntity animatable) {
        return new ResourceLocation("opusvsexe", "textures/entity/blaster_beam.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlasterBeamEntity animatable) {
        return new ResourceLocation("opusvsexe", "animations/entity/blaster_beam.animation.json");
    }

    @Override
    public RenderType getRenderType(BlasterBeamEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}
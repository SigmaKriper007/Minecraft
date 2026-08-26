package com.opus.client.model;

import com.opus.entity.omega.OmegaRingWaveEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class OmegaRingWaveModel extends GeoModel<OmegaRingWaveEntity> {

    @Override
    public ResourceLocation getModelResource(OmegaRingWaveEntity animatable) {
        return new ResourceLocation("opusvsexe", "geo/entity/omega_ring.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(OmegaRingWaveEntity animatable) {
        return new ResourceLocation("opusvsexe", "textures/entity/omega_ring.png");
    }

    @Override
    public ResourceLocation getAnimationResource(OmegaRingWaveEntity animatable) {
        return new ResourceLocation("opusvsexe", "animations/entity/omega_ring.animation.json");
    }

    @Override
    public RenderType getRenderType(OmegaRingWaveEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}

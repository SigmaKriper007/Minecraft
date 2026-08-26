package com.opus.ember.client.model;

import com.opus.ember.entity.projectile.EmberAuraWaveEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EmberAuraWaveModel extends GeoModel<EmberAuraWaveEntity> {

    @Override
    public ResourceLocation getModelResource(EmberAuraWaveEntity animatable) {
        return new ResourceLocation("opusvsexe", "geo/ember/aura_wave.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EmberAuraWaveEntity animatable) {
        return new ResourceLocation("opusvsexe", "textures/ember/entity/aura_wave.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EmberAuraWaveEntity animatable) {
        return new ResourceLocation("opusvsexe", "animations/ember/aura_wave.animation.json");
    }

    @Override
    public RenderType getRenderType(EmberAuraWaveEntity animatable, ResourceLocation texture) { return RenderType.entityTranslucentEmissive(texture); }
}

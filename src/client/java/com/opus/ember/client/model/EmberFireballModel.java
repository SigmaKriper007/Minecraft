package com.opus.ember.client.model;

import com.opus.ember.entity.projectile.EmberFireballProjectile;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EmberFireballModel extends GeoModel<EmberFireballProjectile> {

    @Override
    public ResourceLocation getModelResource(EmberFireballProjectile animatable) {
        return new ResourceLocation("opusvsexe", "geo/ember/fireball.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EmberFireballProjectile animatable) {
        return new ResourceLocation("opusvsexe", "textures/ember/entity/fireball.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EmberFireballProjectile animatable) {
        return new ResourceLocation("opusvsexe", "animations/ember/fireball.animation.json");
    }

    @Override
    public RenderType getRenderType(EmberFireballProjectile animatable, ResourceLocation texture) { return RenderType.entityTranslucentEmissive(texture); }
}

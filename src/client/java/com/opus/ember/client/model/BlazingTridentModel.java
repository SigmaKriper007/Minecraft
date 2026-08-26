package com.opus.ember.client.model;

import com.opus.ember.entity.projectile.BlazingTridentEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlazingTridentModel extends GeoModel<BlazingTridentEntity> {

    @Override
    public ResourceLocation getModelResource(BlazingTridentEntity animatable) {
        return new ResourceLocation("opusvsexe", "geo/ember/trident.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlazingTridentEntity animatable) {
        return new ResourceLocation("opusvsexe", "textures/ember/entity/trident.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlazingTridentEntity animatable) {
        return new ResourceLocation("opusvsexe", "animations/ember/trident.animation.json");
    }

    @Override
    public RenderType getRenderType(BlazingTridentEntity animatable, ResourceLocation texture) { return RenderType.entityTranslucentEmissive(texture); }
}

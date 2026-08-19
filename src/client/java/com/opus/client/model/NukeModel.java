package com.opus.client.model;

import com.opus.entity.ExplosionEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NukeModel extends GeoModel<ExplosionEntity> {

    @Override
    public ResourceLocation getModelResource(ExplosionEntity animatable) {
        return new ResourceLocation("opusvsexe", "geo/entity/nuke.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ExplosionEntity animatable) {
        return new ResourceLocation("opusvsexe", "textures/entity/nuke.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ExplosionEntity animatable) {
        return new ResourceLocation("opusvsexe", "animations/entity/nuke.animation.json");
    }

    @Override
    public RenderType getRenderType(ExplosionEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}
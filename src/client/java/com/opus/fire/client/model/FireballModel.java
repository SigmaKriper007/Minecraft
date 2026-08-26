package com.opus.fire.client.model;

import com.opus.fire.entity.projectile.FireballProjectile;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FireballModel extends GeoModel<FireballProjectile> {

    @Override
    public ResourceLocation getModelResource(FireballProjectile animatable) {
        return new ResourceLocation("opusvsexe", "geo/fire/fireball.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FireballProjectile animatable) {
        return new ResourceLocation("opusvsexe", "textures/fire/entity/fireball.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FireballProjectile animatable) {
        return new ResourceLocation("opusvsexe", "animations/fire/fireball.animation.json");
    }
    
    
    public RenderType getRenderType(FireballProjectile animatable, ResourceLocation texture) { return RenderType.entityTranslucentEmissive(texture); }
}
package com.opus.fire.client.model;

import com.opus.fire.entity.projectile.FireAuraWaveEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FireAuraWaveModel extends GeoModel<FireAuraWaveEntity> {

    @Override
    public ResourceLocation getModelResource(FireAuraWaveEntity animatable) {
        return new ResourceLocation("opusvsexe", "geo/fire/aura_wave.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FireAuraWaveEntity animatable) {
        return new ResourceLocation("opusvsexe", "textures/fire/entity/aura_wave.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FireAuraWaveEntity animatable) {
        return new ResourceLocation("opusvsexe", "animations/fire/aura_wave.animation.json");
    }
    
    
    public RenderType getRenderType(FireAuraWaveEntity animatable, ResourceLocation texture) { return RenderType.entityTranslucentEmissive(texture); }
}
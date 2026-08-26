package com.opus.fire.client.model;

import com.opus.fire.entity.projectile.DemonicTridentEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DemonicTridentModel extends GeoModel<DemonicTridentEntity> {
    public static final ResourceLocation GEO = new ResourceLocation("opusvsexe", "geo/fire/trident.geo.json");
    public static final ResourceLocation TEXTURE = new ResourceLocation("opusvsexe", "textures/fire/entity/trident.png");
    public static final ResourceLocation ANIMATION = new ResourceLocation("opusvsexe", "animations/fire/trident.animation.json");

    @Override
    public ResourceLocation getModelResource(DemonicTridentEntity animatable) {
        return GEO;
    }

    @Override
    public ResourceLocation getTextureResource(DemonicTridentEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(DemonicTridentEntity animatable) {
        return ANIMATION;
    }
    
    
    public RenderType getRenderType(DemonicTridentEntity animatable, ResourceLocation texture) { return RenderType.entityTranslucentEmissive(texture); }
}

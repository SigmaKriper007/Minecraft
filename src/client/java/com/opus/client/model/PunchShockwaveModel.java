package com.opus.client.model;

import com.opus.entity.PunchShockwaveEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PunchShockwaveModel extends GeoModel<PunchShockwaveEntity> {
    private static final ResourceLocation MODEL =
            new ResourceLocation("opusvsexe", "geo/entity/punch_shockwave.geo.json");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("opusvsexe", "textures/entity/punch_shockwave.png");
    private static final ResourceLocation ANIMATION =
            new ResourceLocation("opusvsexe", "animations/entity/punch_shockwave.animation.json");

    @Override public ResourceLocation getModelResource(PunchShockwaveEntity entity) { return MODEL; }
    @Override public ResourceLocation getTextureResource(PunchShockwaveEntity entity) { return TEXTURE; }
    @Override public ResourceLocation getAnimationResource(PunchShockwaveEntity entity) { return ANIMATION; }

    @Override
    public RenderType getRenderType(PunchShockwaveEntity entity, ResourceLocation texture) {
        return RenderType.entityTranslucentEmissive(texture);
    }
}

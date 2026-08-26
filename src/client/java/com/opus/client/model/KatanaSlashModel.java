package com.opus.client.model;

import com.opus.entity.KatanaSlashEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KatanaSlashModel extends GeoModel<KatanaSlashEntity> {
    private static final ResourceLocation MODEL =
            new ResourceLocation("opusvsexe", "geo/entity/katana_slash.geo.json");
    private static final ResourceLocation ANIMATION =
            new ResourceLocation("opusvsexe", "animations/entity/katana_slash.animation.json");
    private static final ResourceLocation OPUS_TEXTURE =
            new ResourceLocation("opusvsexe", "textures/entity/katana_slash_op.png");
    private static final ResourceLocation REFINED_TEXTURE =
            new ResourceLocation("opusvsexe", "textures/entity/katana_slash_refined.png");
    private static final ResourceLocation GOLD_TEXTURE =
            new ResourceLocation("opusvsexe", "textures/entity/katana_slash_gold.png");

    @Override
    public ResourceLocation getModelResource(KatanaSlashEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(KatanaSlashEntity entity) {
        return entity.getVariant() == KatanaSlashEntity.GOLD ? GOLD_TEXTURE
                : entity.getVariant() == KatanaSlashEntity.REFINED ? REFINED_TEXTURE : OPUS_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(KatanaSlashEntity entity) {
        return ANIMATION;
    }

    @Override
    public RenderType getRenderType(KatanaSlashEntity entity, ResourceLocation texture) {
        return RenderType.entityTranslucentEmissive(texture);
    }
}

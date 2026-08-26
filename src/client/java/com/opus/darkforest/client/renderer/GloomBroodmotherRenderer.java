package com.opus.darkforest.client.renderer;

import com.opus.darkforest.client.model.GloomBroodmotherModel;
import com.opus.darkforest.entity.GloomBroodmotherEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class GloomBroodmotherRenderer extends GeoEntityRenderer<GloomBroodmotherEntity> {
    public GloomBroodmotherRenderer(EntityRendererProvider.Context context) {
        super(context, new GloomBroodmotherModel());
        shadowRadius = 1F;
        addRenderLayer(new EmissiveGlowGeoLayer<>(this));
    }
}

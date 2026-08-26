package com.opus.darkforest.client.renderer;

import com.opus.darkforest.client.model.ShadeSpiderlingModel;
import com.opus.darkforest.entity.ShadeSpiderlingEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class ShadeSpiderlingRenderer extends GeoEntityRenderer<ShadeSpiderlingEntity> {
    public ShadeSpiderlingRenderer(EntityRendererProvider.Context context) {
        super(context, new ShadeSpiderlingModel());
        shadowRadius = .28F;
        addRenderLayer(new EmissiveGlowGeoLayer<>(this));
    }
}

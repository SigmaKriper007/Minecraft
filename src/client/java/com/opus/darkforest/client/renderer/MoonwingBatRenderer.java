package com.opus.darkforest.client.renderer;

import com.opus.darkforest.client.model.MoonwingBatModel;
import com.opus.darkforest.entity.MoonwingBatEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class MoonwingBatRenderer extends GeoEntityRenderer<MoonwingBatEntity> {
    public MoonwingBatRenderer(EntityRendererProvider.Context context) {
        super(context, new MoonwingBatModel());
        shadowRadius = .62F;
        addRenderLayer(new EmissiveGlowGeoLayer<>(this));
    }
}

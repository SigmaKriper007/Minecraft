package com.opus.paradise.client.renderer;

import com.opus.paradise.client.model.WindCoreModel;
import com.opus.paradise.entity.WindCoreEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class WindCoreRenderer extends GeoEntityRenderer<WindCoreEntity> {
    public WindCoreRenderer(EntityRendererProvider.Context context) {
        super(context, new WindCoreModel());
        shadowRadius = 0.0F;
    }
}

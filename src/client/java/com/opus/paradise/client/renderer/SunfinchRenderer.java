package com.opus.paradise.client.renderer;

import com.opus.paradise.client.model.SunfinchModel;
import com.opus.paradise.entity.SunfinchEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class SunfinchRenderer extends GeoEntityRenderer<SunfinchEntity> {
    public SunfinchRenderer(EntityRendererProvider.Context context) {
        super(context, new SunfinchModel());
        shadowRadius = 0.18F;
    }
}

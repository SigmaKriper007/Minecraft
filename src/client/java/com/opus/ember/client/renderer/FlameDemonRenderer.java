package com.opus.ember.client.renderer;

import com.opus.ember.client.model.FlameDemonModel;
import com.opus.ember.entity.FlameDemonEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FlameDemonRenderer extends GeoEntityRenderer<FlameDemonEntity> {

    public FlameDemonRenderer(EntityRendererProvider.Context context) {
        super(context, new FlameDemonModel());
    }
}

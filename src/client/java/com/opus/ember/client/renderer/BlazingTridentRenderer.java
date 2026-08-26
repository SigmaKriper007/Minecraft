package com.opus.ember.client.renderer;

import com.opus.ember.client.model.BlazingTridentModel;
import com.opus.ember.entity.projectile.BlazingTridentEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BlazingTridentRenderer extends GeoEntityRenderer<BlazingTridentEntity> {

    public BlazingTridentRenderer(EntityRendererProvider.Context context) {
        super(context, new BlazingTridentModel());
    }
}

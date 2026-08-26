package com.opus.ember.client.renderer;

import com.opus.ember.client.model.EmberAuraWaveModel;
import com.opus.ember.entity.projectile.EmberAuraWaveEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EmberAuraWaveRenderer extends GeoEntityRenderer<EmberAuraWaveEntity> {

    public EmberAuraWaveRenderer(EntityRendererProvider.Context context) {
        super(context, new EmberAuraWaveModel());
    }
}

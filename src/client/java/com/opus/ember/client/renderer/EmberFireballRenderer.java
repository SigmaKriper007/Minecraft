package com.opus.ember.client.renderer;

import com.opus.ember.client.model.EmberFireballModel;
import com.opus.ember.entity.projectile.EmberFireballProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EmberFireballRenderer extends GeoEntityRenderer<EmberFireballProjectile> {

    public EmberFireballRenderer(EntityRendererProvider.Context context) {
        super(context, new EmberFireballModel());
    }
}

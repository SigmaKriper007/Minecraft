package com.opus.fire.client.renderer;

import com.opus.fire.client.model.FireballModel;
import com.opus.fire.entity.projectile.FireballProjectile;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class FireballRenderer extends GeoEntityRenderer<FireballProjectile> {

    public FireballRenderer(EntityRendererProvider.Context context) {
        super(context, new FireballModel());
    }
}
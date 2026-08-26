package com.opus.fire.client.renderer;

import com.opus.fire.client.model.FireAuraWaveModel;
import com.opus.fire.entity.projectile.FireAuraWaveEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class FireAuraWaveRenderer extends GeoEntityRenderer<FireAuraWaveEntity> {

    public FireAuraWaveRenderer(EntityRendererProvider.Context context) {
        super(context, new FireAuraWaveModel());
    }
}
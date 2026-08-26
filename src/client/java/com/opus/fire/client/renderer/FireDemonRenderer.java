package com.opus.fire.client.renderer;

import com.opus.fire.client.model.FireDemonModel;
import com.opus.fire.entity.FireDemonEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The combat hull follows the unscaled body: arms span 20.6/16 ~= 1.29 blocks
 * and the horns reach 44/16 = 2.75 blocks. Wings stay decorative so their
 * four-block visual span does not create invisible side collisions.
 */
public class FireDemonRenderer extends GeoEntityRenderer<FireDemonEntity> {

    public FireDemonRenderer(EntityRendererProvider.Context context) {
        super(context, new FireDemonModel());
    }
}

package com.opus.fire.client.renderer;

import com.opus.fire.client.model.LavaGolemModel;
import com.opus.fire.entity.LavaGolemEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class LavaGolemRenderer extends GeoEntityRenderer<LavaGolemEntity> {

    public LavaGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new LavaGolemModel());
    }
}
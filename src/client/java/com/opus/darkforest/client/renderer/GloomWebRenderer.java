package com.opus.darkforest.client.renderer;

import com.opus.darkforest.client.model.GloomWebModel;
import com.opus.darkforest.entity.GloomWebEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class GloomWebRenderer extends GeoEntityRenderer<GloomWebEntity> {
    public GloomWebRenderer(EntityRendererProvider.Context context) { super(context, new GloomWebModel()); shadowRadius = 0; }
}

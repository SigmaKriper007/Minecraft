package com.opus.ember.client.renderer;

import com.opus.ember.client.model.ObsidianGolemModel;
import com.opus.ember.entity.ObsidianGolemEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ObsidianGolemRenderer extends GeoEntityRenderer<ObsidianGolemEntity> {

    public ObsidianGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new ObsidianGolemModel());
    }
}

package com.opus.paradise.client.renderer;

import com.opus.paradise.client.model.CloudGrazerModel;
import com.opus.paradise.entity.CloudGrazerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class CloudGrazerRenderer extends GeoEntityRenderer<CloudGrazerEntity> {
    public CloudGrazerRenderer(EntityRendererProvider.Context context) {
        super(context, new CloudGrazerModel());
        shadowRadius = 0.65F;
    }
}

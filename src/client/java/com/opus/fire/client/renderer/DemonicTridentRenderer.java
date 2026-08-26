package com.opus.fire.client.renderer;

import com.opus.fire.client.model.DemonicTridentModel;
import com.opus.fire.entity.projectile.DemonicTridentEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class DemonicTridentRenderer extends GeoEntityRenderer<DemonicTridentEntity> {

    public DemonicTridentRenderer(EntityRendererProvider.Context context) {
        super(context, new DemonicTridentModel());
    }
}
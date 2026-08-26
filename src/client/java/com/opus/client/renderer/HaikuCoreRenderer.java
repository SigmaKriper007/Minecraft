package com.opus.client.renderer;

import com.opus.client.model.HaikuCoreModel;
import com.opus.item.HaikuCoreItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public final class HaikuCoreRenderer extends GeoItemRenderer<HaikuCoreItem> {
    public HaikuCoreRenderer() {
        super(new HaikuCoreModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        withScale(0.82F);
    }
}
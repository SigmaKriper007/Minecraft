package com.opus.fire.client.renderer;

import com.opus.fire.client.model.DemonicTridentItemModel;
import com.opus.fire.item.DemonicTridentItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public final class DemonicTridentItemRenderer extends GeoItemRenderer<DemonicTridentItem> {
    public DemonicTridentItemRenderer() {
        super(new DemonicTridentItemModel());
    }
}

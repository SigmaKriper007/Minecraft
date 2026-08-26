package com.opus.fire.client.model;

import com.opus.fire.item.DemonicTridentItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/** Item-side adapter for the exact same geometry and material as the projectile. */
public final class DemonicTridentItemModel extends GeoModel<DemonicTridentItem> {
    @Override public ResourceLocation getModelResource(DemonicTridentItem item) { return DemonicTridentModel.GEO; }
    @Override public ResourceLocation getTextureResource(DemonicTridentItem item) { return DemonicTridentModel.TEXTURE; }
    @Override public ResourceLocation getAnimationResource(DemonicTridentItem item) { return DemonicTridentModel.ANIMATION; }
    @Override public RenderType getRenderType(DemonicTridentItem item, ResourceLocation texture) {
        return RenderType.entityTranslucentEmissive(texture);
    }
}

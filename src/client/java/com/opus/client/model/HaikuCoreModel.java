package com.opus.client.model;

import com.opus.OpusVsExe;
import com.opus.item.HaikuCoreItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class HaikuCoreModel extends GeoModel<HaikuCoreItem> {
    @Override public ResourceLocation getModelResource(HaikuCoreItem item) {
        return OpusVsExe.id("geo/item/haiku_core.geo.json");
    }
    @Override public ResourceLocation getTextureResource(HaikuCoreItem item) {
        return OpusVsExe.id("textures/item/haiku_core_3d.png");
    }
    @Override public ResourceLocation getAnimationResource(HaikuCoreItem item) {
        return OpusVsExe.id("animations/item/haiku_core.animation.json");
    }
    @Override public RenderType getRenderType(HaikuCoreItem item, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}
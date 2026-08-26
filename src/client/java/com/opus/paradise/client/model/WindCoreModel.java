package com.opus.paradise.client.model;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.entity.WindCoreEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class WindCoreModel extends GeoModel<WindCoreEntity> {
    @Override public ResourceLocation getModelResource(WindCoreEntity entity) { return ParadiseLine.id("geo/paradise/wind_core.geo.json"); }
    @Override public ResourceLocation getTextureResource(WindCoreEntity entity) { return ParadiseLine.id("textures/paradise/entity/wind_core.png"); }
    @Override public ResourceLocation getAnimationResource(WindCoreEntity entity) { return ParadiseLine.id("animations/paradise/wind_core.animation.json"); }
    @Override public RenderType getRenderType(WindCoreEntity entity, ResourceLocation texture) { return RenderType.entityTranslucentEmissive(texture); }
    @Override public boolean crashIfBoneMissing() { return true; }
}

package com.opus.paradise.client.model;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.entity.ParadiseWyvernEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public final class ParadiseWyvernModel extends GeoModel<ParadiseWyvernEntity> {
    @Override
    public void setCustomAnimations(ParadiseWyvernEntity entity, long instanceId,
                                    AnimationState<ParadiseWyvernEntity> state) {
        getBone("saddle").ifPresent(saddle -> saddle.setHidden(!entity.isSaddled()));
    }

    @Override public ResourceLocation getModelResource(ParadiseWyvernEntity entity) { return ParadiseLine.id("geo/paradise/paradise_wyvern.geo.json"); }
    @Override public ResourceLocation getTextureResource(ParadiseWyvernEntity entity) { return ParadiseLine.id("textures/paradise/entity/paradise_wyvern.png"); }
    @Override public ResourceLocation getAnimationResource(ParadiseWyvernEntity entity) { return ParadiseLine.id("animations/paradise/paradise_wyvern.animation.json"); }
    @Override public boolean crashIfBoneMissing() { return true; }
}

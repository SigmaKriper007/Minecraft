package com.opus.ember.client.model;

import com.opus.ember.entity.FlameDemonEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public final class FlameDemonModel extends GeoModel<FlameDemonEntity> {
    private static final String[] ICE = {"ice_front","ice_back","ice_left","ice_right","ice_top","ice_floor"};
    @Override public ResourceLocation getModelResource(FlameDemonEntity entity){return new ResourceLocation("opusvsexe","geo/ember/demon.geo.json");}
    @Override public ResourceLocation getTextureResource(FlameDemonEntity entity){return new ResourceLocation("opusvsexe","textures/ember/entity/demon.png");}
    @Override public ResourceLocation getAnimationResource(FlameDemonEntity entity){return new ResourceLocation("opusvsexe","animations/ember/demon.animation.json");}
    @Override public RenderType getRenderType(FlameDemonEntity entity,ResourceLocation texture){return RenderType.entityTranslucent(texture);}
    @Override public boolean crashIfBoneMissing(){return true;}

    @Override
    public void setCustomAnimations(FlameDemonEntity entity,long instanceId,AnimationState<FlameDemonEntity> state){
        boolean hidden=entity.isAwakened();
        for(String name:ICE)getBone(name).ifPresent(bone->bone.setHidden(hidden));
        getBone("trident").ifPresent(bone->bone.setHidden(entity.getAction()==FlameDemonEntity.Action.TRIDENT&&entity.getActionTick()>17));
        getBone("veil").ifPresent(bone->bone.setHidden(entity.getAction()==FlameDemonEntity.Action.AWAKEN && entity.getActionTick() < 20));
    }
}

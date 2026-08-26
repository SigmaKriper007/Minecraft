package com.opus.fire.client.model;

import com.opus.fire.entity.FireDemonEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public final class FireDemonModel extends GeoModel<FireDemonEntity> {
    private static final String[] ICE = {"ice_front","ice_back","ice_left","ice_right","ice_top","ice_floor"};
    @Override public ResourceLocation getModelResource(FireDemonEntity entity){return new ResourceLocation("opusvsexe","geo/fire/demon.geo.json");}
    @Override public ResourceLocation getTextureResource(FireDemonEntity entity){return new ResourceLocation("opusvsexe","textures/fire/entity/demon.png");}
    @Override public ResourceLocation getAnimationResource(FireDemonEntity entity){return new ResourceLocation("opusvsexe","animations/fire/demon.animation.json");}
    @Override public RenderType getRenderType(FireDemonEntity entity,ResourceLocation texture){return RenderType.entityTranslucent(texture);}
    @Override public boolean crashIfBoneMissing(){return true;}

    @Override
    public void setCustomAnimations(FireDemonEntity entity,long instanceId,AnimationState<FireDemonEntity> state){
        boolean hidden=entity.isAwakened();
        for(String name:ICE)getBone(name).ifPresent(bone->bone.setHidden(hidden));
        // Трезубец «призывается» только на замахе TRIDENT (у демона — когти).
        getBone("trident").ifPresent(bone->bone.setHidden(
            !(entity.getAction()==FireDemonEntity.Action.TRIDENT && entity.getActionTick()<=17)));
    }
}

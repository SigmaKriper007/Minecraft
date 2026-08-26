package com.opus.darkforest.client.model;

import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.entity.MossboundAttackEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class MossboundAttackModel extends GeoModel<MossboundAttackEntity> {
    private String id(MossboundAttackEntity entity){return entity.kind().name().toLowerCase(java.util.Locale.ROOT);}
    @Override public ResourceLocation getModelResource(MossboundAttackEntity entity){return DarkForestLine.id("geo/dark_forest/attacks/"+id(entity)+".geo.json");}
    @Override public ResourceLocation getTextureResource(MossboundAttackEntity entity){return DarkForestLine.id("textures/dark_forest/attacks/"+id(entity)+".png");}
    @Override public ResourceLocation getAnimationResource(MossboundAttackEntity entity){return DarkForestLine.id("animations/dark_forest/mossbound_attacks.animation.json");}
    @Override public RenderType getRenderType(MossboundAttackEntity entity,ResourceLocation texture){return RenderType.entityTranslucent(texture);}
    @Override public boolean crashIfBoneMissing(){return true;}
}

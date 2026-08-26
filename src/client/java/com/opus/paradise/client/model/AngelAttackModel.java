package com.opus.paradise.client.model;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.entity.AngelAttackEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class AngelAttackModel extends GeoModel<AngelAttackEntity> {
    private static String path(AngelAttackEntity e){return switch(e.kind()){case LANCE->"halo_lance";case CROSSWIND->"seraphic_crosswind";case FEATHER->"seraphic_feather";case RING->"wingbeat_ring";case ASCENSION->"angel_ascension";case DESCENT->"ruby_descent";};}
    @Override public ResourceLocation getModelResource(AngelAttackEntity e){return ParadiseLine.id("geo/paradise/"+path(e)+".geo.json");}
    @Override public ResourceLocation getTextureResource(AngelAttackEntity e){return ParadiseLine.id("textures/paradise/entity/"+path(e)+".png");}
    @Override public ResourceLocation getAnimationResource(AngelAttackEntity e){return ParadiseLine.id("animations/paradise/angel_attacks.animation.json");}
    @Override public RenderType getRenderType(AngelAttackEntity e,ResourceLocation texture){return RenderType.entityTranslucentEmissive(texture);}
    @Override public boolean crashIfBoneMissing(){return true;}
}

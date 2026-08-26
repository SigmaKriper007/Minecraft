package com.opus.paradise.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.opus.paradise.client.model.AngelAttackModel;
import com.opus.paradise.entity.AngelAttackEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class AngelAttackRenderer extends GeoEntityRenderer<AngelAttackEntity>{
    public AngelAttackRenderer(EntityRendererProvider.Context c){super(c,new AngelAttackModel());shadowRadius=0;}
    @Override public void render(AngelAttackEntity e,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){
        float scale=switch(e.kind()){
            case LANCE->9F;
            case CROSSWIND->16F;
            case FEATHER->1F;
            case RING->Math.max(.7F,(float)(.35+(e.tickCount+partial)*.43)*2F);
            case ASCENSION->16F;
            case DESCENT->14F;
        };
        pose.scale(scale,scale,scale);super.render(e,yaw,partial,pose,buffers,light);
    }
}

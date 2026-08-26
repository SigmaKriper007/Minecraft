package com.opus.paradise.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.opus.darkforest.client.renderer.EmissiveGlowGeoLayer;
import com.opus.paradise.client.model.AngelBoyModel;
import com.opus.paradise.entity.AngelBoyEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class AngelBoyRenderer extends GeoEntityRenderer<AngelBoyEntity>{
    public AngelBoyRenderer(EntityRendererProvider.Context c){super(c,new AngelBoyModel());shadowRadius=.55F;addRenderLayer(new EmissiveGlowGeoLayer<>(this));}
    @Override public void render(AngelBoyEntity e,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){pose.scale(1.25F,1.25F,1.25F);super.render(e,yaw,partial,pose,buffers,light);}
}

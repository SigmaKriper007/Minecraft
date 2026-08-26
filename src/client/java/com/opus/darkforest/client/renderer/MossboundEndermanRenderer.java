package com.opus.darkforest.client.renderer;

import com.opus.darkforest.client.model.MossboundEndermanModel;
import com.opus.darkforest.entity.MossboundEndermanEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class MossboundEndermanRenderer extends GeoEntityRenderer<MossboundEndermanEntity> {
    public MossboundEndermanRenderer(EntityRendererProvider.Context context){super(context,new MossboundEndermanModel());addRenderLayer(new EmissiveGlowGeoLayer<>(this));shadowRadius=1.35F;}
}

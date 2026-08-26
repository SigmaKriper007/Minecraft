package com.opus.darkforest.client.renderer;

import com.opus.darkforest.client.model.MossboundAttackModel;
import com.opus.darkforest.entity.MossboundAttackEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class MossboundAttackRenderer extends GeoEntityRenderer<MossboundAttackEntity> {
    public MossboundAttackRenderer(EntityRendererProvider.Context context){super(context,new MossboundAttackModel());addRenderLayer(new EmissiveGlowGeoLayer<>(this));shadowRadius=0;}
}

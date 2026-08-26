package com.opus.settlement.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.opus.settlement.SettlementLine;
import com.opus.settlement.client.model.SamuraiModel;
import com.opus.settlement.entity.SamuraiEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public final class SamuraiRenderer extends HumanoidMobRenderer<SamuraiEntity,SamuraiModel>{
    private static final ResourceLocation TEXTURE=SettlementLine.id("textures/entity/japanese/samurai.png");
    public SamuraiRenderer(EntityRendererProvider.Context context){super(context,new SamuraiModel(context.bakeLayer(SamuraiModel.LAYER)),.6F);addLayer(new ItemInHandLayer<>(this,context.getItemInHandRenderer()));}
    @Override protected void scale(SamuraiEntity entity,PoseStack pose,float partialTick){pose.scale(1.3F,1.3F,1.3F);}
    @Override public ResourceLocation getTextureLocation(SamuraiEntity entity){return TEXTURE;}
}

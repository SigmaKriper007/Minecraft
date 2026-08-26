package com.opus.settlement.client.renderer;

import com.opus.settlement.SettlementLine;
import com.opus.settlement.client.model.BlackNinjaModel;
import com.opus.settlement.entity.BlackNinjaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public final class BlackNinjaRenderer extends HumanoidMobRenderer<BlackNinjaEntity,BlackNinjaModel>{
    private static final ResourceLocation TEXTURE=SettlementLine.id("textures/entity/japanese/black_ninja.png");
    public BlackNinjaRenderer(EntityRendererProvider.Context context){super(context,new BlackNinjaModel(context.bakeLayer(BlackNinjaModel.LAYER)),.45F);addLayer(new ItemInHandLayer<>(this,context.getItemInHandRenderer()));}
    @Override public ResourceLocation getTextureLocation(BlackNinjaEntity entity){return TEXTURE;}
}

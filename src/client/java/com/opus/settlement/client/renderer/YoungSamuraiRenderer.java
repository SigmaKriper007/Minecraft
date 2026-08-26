package com.opus.settlement.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.opus.settlement.SettlementLine;
import com.opus.settlement.client.model.YoungSamuraiModel;
import com.opus.settlement.entity.YoungSamuraiEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public final class YoungSamuraiRenderer extends HumanoidMobRenderer<YoungSamuraiEntity, YoungSamuraiModel> {
    private static final ResourceLocation TEXTURE = SettlementLine.id("textures/entity/japanese/young_samurai.png");
    public YoungSamuraiRenderer(EntityRendererProvider.Context context) {
        super(context, new YoungSamuraiModel(context.bakeLayer(YoungSamuraiModel.LAYER)), .55F);
        addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }
    @Override protected void scale(YoungSamuraiEntity entity, PoseStack pose, float partialTick) { pose.scale(1.1F, 1.1F, 1.1F); }
    @Override public ResourceLocation getTextureLocation(YoungSamuraiEntity entity) { return TEXTURE; }
}

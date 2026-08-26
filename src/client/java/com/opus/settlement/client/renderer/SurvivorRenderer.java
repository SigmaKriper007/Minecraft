package com.opus.settlement.client.renderer;

import com.opus.settlement.SettlementLine;
import com.opus.settlement.client.model.SurvivorModel;
import com.opus.settlement.entity.SurvivorEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public final class SurvivorRenderer extends HumanoidMobRenderer<SurvivorEntity, SurvivorModel> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[SurvivorEntity.SKIN_VARIANTS];
    static {
        for (int i = 0; i < TEXTURES.length; i++) TEXTURES[i] = SettlementLine.id("textures/entity/survivor/survivor_" + i + ".png");
    }

    public SurvivorRenderer(EntityRendererProvider.Context context) {
        super(context, new SurvivorModel(context.bakeLayer(SurvivorModel.LAYER)), .5F);
        addLayer(new HumanoidArmorLayer<>(this,
            new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
            new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
        addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override public ResourceLocation getTextureLocation(SurvivorEntity entity) { return TEXTURES[entity.getVariant()]; }
}

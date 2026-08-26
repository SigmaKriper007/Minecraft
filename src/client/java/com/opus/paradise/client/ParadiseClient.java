package com.opus.paradise.client;

import com.opus.paradise.registry.ParadiseBlocks;
import com.opus.paradise.registry.ParadiseEntities;
import com.opus.paradise.client.renderer.CloudGrazerRenderer;
import com.opus.paradise.client.renderer.SunfinchRenderer;
import com.opus.paradise.client.renderer.HurricaneRenderer;
import com.opus.paradise.client.renderer.ParadiseWyvernRenderer;
import com.opus.paradise.client.renderer.WindCoreRenderer;
import com.opus.paradise.client.renderer.AngelBoyRenderer;
import com.opus.paradise.client.renderer.AngelAttackRenderer;
import com.opus.paradise.client.screen.ParthenonForgeScreen;
import com.opus.paradise.client.model.ParthenonRegaliaModel;
import com.opus.paradise.registry.ParadiseMenus;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.renderer.RenderType;

public final class ParadiseClient {
    private ParadiseClient() { }

    public static void init() {
        BlockRenderLayerMap.INSTANCE.putBlock(ParadiseBlocks.PARADISE_LEAVES, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ParadiseBlocks.PARADISE_SAPLING, RenderType.cutout());
        EntityRendererRegistry.register(ParadiseEntities.SUNFINCH, SunfinchRenderer::new);
        EntityRendererRegistry.register(ParadiseEntities.CLOUD_GRAZER, CloudGrazerRenderer::new);
        EntityRendererRegistry.register(ParadiseEntities.PARADISE_WYVERN, ParadiseWyvernRenderer::new);
        EntityRendererRegistry.register(ParadiseEntities.WIND_CORE, WindCoreRenderer::new);
        EntityRendererRegistry.register(ParadiseEntities.HURRICANE, HurricaneRenderer::new);
        EntityRendererRegistry.register(ParadiseEntities.ANGEL_BOY, AngelBoyRenderer::new);
        EntityRendererRegistry.register(ParadiseEntities.HALO_LANCE, AngelAttackRenderer::new);
        EntityRendererRegistry.register(ParadiseEntities.SERAPHIC_CROSSWIND, AngelAttackRenderer::new);
        EntityRendererRegistry.register(ParadiseEntities.SERAPHIC_FEATHER, AngelAttackRenderer::new);
        EntityRendererRegistry.register(ParadiseEntities.WINGBEAT_RING, AngelAttackRenderer::new);
        EntityRendererRegistry.register(ParadiseEntities.ANGEL_ASCENSION, AngelAttackRenderer::new);
        EntityRendererRegistry.register(ParadiseEntities.RUBY_DESCENT, AngelAttackRenderer::new);
        ScreenRegistry.register(ParadiseMenus.PARTHENON_FORGE,ParthenonForgeScreen::new);
        EntityModelLayerRegistry.registerModelLayer(ParthenonRegaliaModel.LAYER_LOCATION,ParthenonRegaliaModel::createLayerDefinition);
        ParadiseWyvernInput.init();
    }
}

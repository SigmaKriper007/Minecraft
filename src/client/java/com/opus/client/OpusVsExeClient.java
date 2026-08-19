package com.opus.client;

import com.opus.client.model.GreatHelmModel;
import com.opus.client.model.LaserModel;
import com.opus.client.model.OpusPlateModel;
import com.opus.client.model.ShadowAssassinHoodModel;
import com.opus.client.model.ShadowAssassinModel;
import com.opus.client.renderer.ExoOmenRenderer;
import com.opus.client.renderer.LaserRenderer;
import com.opus.client.renderer.NukeRenderer;
import com.opus.client.renderer.PlaceholderMobRenderer;
import com.opus.client.screen.ExoInventoryScreen;
import com.opus.client.screen.RewardVaultScreen;
import com.opus.registry.ModEntities;
import com.opus.registry.ModMenus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

public class OpusVsExeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ExosuitRiderEffects.init();
		ExoAbilityKeybind.init();
		ScreenRegistry.register(ModMenus.EXO_INVENTORY, ExoInventoryScreen::new);
		ScreenRegistry.register(ModMenus.REWARD_VAULT, RewardVaultScreen::new);
        EntityModelLayerRegistry.registerModelLayer(GreatHelmModel.LAYER_LOCATION, GreatHelmModel::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(OpusPlateModel.LAYER_LOCATION, OpusPlateModel::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(ShadowAssassinHoodModel.LAYER_LOCATION, ShadowAssassinHoodModel::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(ShadowAssassinModel.LAYER_LOCATION, ShadowAssassinModel::createLayerDefinition);
		EntityModelLayerRegistry.registerModelLayer(LaserModel.LAYER_LOCATION, LaserModel::createLayerDefinition);
		EntityRendererRegistry.register(ModEntities.LASER, LaserRenderer::new);
		EntityRendererRegistry.register(ModEntities.EXPLOSION, NukeRenderer::new);
		EntityRendererRegistry.register(ModEntities.HAIKU_1_5, PlaceholderMobRenderer::new);
		EntityRendererRegistry.register(ModEntities.HAIKU_2, PlaceholderMobRenderer::new);
		EntityRendererRegistry.register(ModEntities.HAIKU_3, PlaceholderMobRenderer::new);
		EntityRendererRegistry.register(ModEntities.HAIKU_4, PlaceholderMobRenderer::new);
		EntityRendererRegistry.register(ModEntities.HAIKU_5, PlaceholderMobRenderer::new);
		EntityRendererRegistry.register(ModEntities.HAIKU_OMEGA, PlaceholderMobRenderer::new);
		EntityRendererRegistry.register(ModEntities.EXO_1_SENTINEL, ExoOmenRenderer::new);
		EntityRendererRegistry.register(ModEntities.EXO_2_HUNTER, ExoOmenRenderer::new);
		EntityRendererRegistry.register(ModEntities.EXO_3_VANGUARD, ExoOmenRenderer::new);
		EntityRendererRegistry.register(ModEntities.EXO_4_TITAN, ExoOmenRenderer::new);
		EntityRendererRegistry.register(ModEntities.EXO_5_VENGEANCE, ExoOmenRenderer::new);
	}
}
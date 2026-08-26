package com.opus.client;

import com.opus.client.hud.ExoHud;
import com.opus.client.model.GreatHelmModel;
import com.opus.client.model.LaserModel;
import com.opus.client.model.OpusPlateModel;
import com.opus.client.model.ShadowAssassinHoodModel;
import com.opus.client.model.ShadowAssassinModel;
import com.opus.client.model.SkyLaserModel;
import com.opus.client.renderer.BlasterBeamRenderer;
import com.opus.client.renderer.EnforcerRenderer;
import com.opus.client.renderer.ExoOmenRenderer;
import com.opus.client.renderer.ExoPlusRenderer;
import com.opus.client.renderer.ExoExtraLaserRenderer;
import com.opus.client.renderer.WardenRenderer;
import com.opus.client.renderer.TitanRenderer;
import com.opus.client.renderer.HeavyBlasterBeamRenderer;
import com.opus.client.renderer.HaikuRenderer;
import com.opus.client.renderer.HaikuCoreRenderer;
import com.opus.client.renderer.HuskRenderer;
import com.opus.client.renderer.LaserRenderer;
import com.opus.client.renderer.KatanaSlashRenderer;
import com.opus.client.renderer.PunchShockwaveRenderer;
import com.opus.client.renderer.SkyLaserRenderer;
import com.opus.client.renderer.NukeRenderer;
import com.opus.client.renderer.OmegaRenderer;
import com.opus.client.renderer.OmegaRingWaveRenderer;
import com.opus.client.renderer.OmegaShrapnelRenderer;
import com.opus.client.renderer.OmegaSkyLaserRenderer;
import com.opus.client.renderer.OmegaSlashRenderer;
import com.opus.client.renderer.PlaceholderMobRenderer;
import com.opus.client.renderer.ScoutRenderer;
import com.opus.client.renderer.AltarHeartBlockEntityRenderer;
import com.opus.client.screen.ExoInventoryScreen;
import com.opus.client.screen.RewardVaultScreen;
import com.opus.fire.client.FireClient;
import com.opus.ember.client.EmberClient;
import com.opus.paradise.client.ParadiseClient;
import com.opus.darkforest.client.DarkForestClient;
import com.opus.settlement.client.SettlementClient;
import com.opus.registry.ModBlocks;
import com.opus.blockentity.ModBlockEntities;
import com.opus.registry.ModEntities;
import com.opus.registry.ModMenus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.renderer.RenderType;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

public class OpusVsExeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// EXO control layer
		ExoKeybinds.init();
		ExoInputHandler.init();
		ExoClientNetwork.init();
		OmegaClientNetwork.init();
		ExoHud.init();

		// Анимированная 3D-модель Haiku Core — янтарное ядро в гироскопической оправе
		HaikuCoreRenderer haikuCoreRenderer = new HaikuCoreRenderer();
		BuiltinItemRendererRegistry.INSTANCE.register(com.opus.registry.ModItems.HAIKU_CORE,
				(stack, mode, matrices, consumers, light, overlay) ->
					haikuCoreRenderer.renderByItem(stack, mode, matrices, consumers, light, overlay));

		// «Кровавая луна» и эмбиент Сердца Алтаря (задача 2026-08-22)
		CrimsonMoonClient.init();
		WorldRenderEvents.BEFORE_DEBUG_RENDER.register(ctx ->
				CrimsonMoonClient.renderFallingStars(ctx.matrixStack(), ctx.tickDelta()));
		AltarHeartAmbience.init();

		// Левитирующий кристалл Сердца Алтаря (задача 16)
		BlockEntityRendererRegistry.register(ModBlockEntities.ALTAR_HEART, AltarHeartBlockEntityRenderer::new);

		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ENERGY_BARRIER, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ENERGY_BARRIER_RED, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ENERGY_BARRIER_BLUE, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ENERGY_BEAM, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PHASED_BARRIER, RenderType.translucent());
		// Прозрачность блоков Колизея (задача 19)
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.LAB_FLOOR_GRATE, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OPUS_CONTAINMENT_GLASS, RenderType.translucent());

		ScreenRegistry.register(ModMenus.EXO_INVENTORY, ExoInventoryScreen::new);
		ScreenRegistry.register(ModMenus.REWARD_VAULT, RewardVaultScreen::new);
		EntityModelLayerRegistry.registerModelLayer(GreatHelmModel.LAYER_LOCATION, GreatHelmModel::createLayerDefinition);
		EntityModelLayerRegistry.registerModelLayer(OpusPlateModel.LAYER_LOCATION, OpusPlateModel::createLayerDefinition);
		EntityModelLayerRegistry.registerModelLayer(ShadowAssassinHoodModel.LAYER_LOCATION, ShadowAssassinHoodModel::createLayerDefinition);
		EntityModelLayerRegistry.registerModelLayer(ShadowAssassinModel.LAYER_LOCATION, ShadowAssassinModel::createLayerDefinition);
		EntityModelLayerRegistry.registerModelLayer(LaserModel.LAYER_LOCATION, LaserModel::createLayerDefinition);
		EntityRendererRegistry.register(ModEntities.LASER, LaserRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(SkyLaserModel.LAYER_LOCATION, SkyLaserModel::createLayerDefinition);
		EntityRendererRegistry.register(ModEntities.SKY_LASER, SkyLaserRenderer::new);
		EntityRendererRegistry.register(ModEntities.EXPLOSION, NukeRenderer::new);
		EntityRendererRegistry.register(ModEntities.BLASTER_BEAM, BlasterBeamRenderer::new);
		EntityRendererRegistry.register(ModEntities.HEAVY_BLASTER_BEAM, HeavyBlasterBeamRenderer::new);
		EntityRendererRegistry.register(ModEntities.EXTRA_LASER_BEAM, ExoExtraLaserRenderer::new);
		EntityRendererRegistry.register(ModEntities.KATANA_SLASH, KatanaSlashRenderer::new);
		EntityRendererRegistry.register(ModEntities.PUNCH_SHOCKWAVE, PunchShockwaveRenderer::new);
		EntityRendererRegistry.register(ModEntities.HAIKU_1_5, HuskRenderer::new);
		EntityRendererRegistry.register(ModEntities.HAIKU_2, ScoutRenderer::new);
		EntityRendererRegistry.register(ModEntities.HAIKU_3, EnforcerRenderer::new);
		EntityRendererRegistry.register(ModEntities.HAIKU_4, WardenRenderer::new);
		EntityRendererRegistry.register(ModEntities.HAIKU_5, TitanRenderer::new);
		EntityRendererRegistry.register(ModEntities.HAIKU_OMEGA, OmegaRenderer::new);
		EntityRendererRegistry.register(ModEntities.OMEGA_SHRAPNEL, OmegaShrapnelRenderer::new);
		EntityRendererRegistry.register(ModEntities.OMEGA_RING_WAVE, OmegaRingWaveRenderer::new);
		EntityRendererRegistry.register(ModEntities.OMEGA_SLASH, OmegaSlashRenderer::new);
		EntityRendererRegistry.register(ModEntities.OMEGA_SKY_LASER, OmegaSkyLaserRenderer::new);
		EntityRendererRegistry.register(ModEntities.HAIKU_DRONE, HaikuRenderer::new);
		EntityRendererRegistry.register(ModEntities.HAIKU_DRONE_PLUS, HaikuRenderer::new);
		EntityRendererRegistry.register(ModEntities.EXO_1_SENTINEL, ExoOmenRenderer::new);
		EntityRendererRegistry.register(ModEntities.EXO_2_HUNTER, ExoOmenRenderer::new);
		EntityRendererRegistry.register(ModEntities.EXO_3_VANGUARD, ExoOmenRenderer::new);
		EntityRendererRegistry.register(ModEntities.EXO_4_TITAN, ExoOmenRenderer::new);
		EntityRendererRegistry.register(ModEntities.EXO_5_VENGEANCE, ExoPlusRenderer::new);

        // Fire Biom — параллельная линия (клиент)
        new FireClient().onInitializeClient();
        // Ember — параллельная линия-дубликат (клиент)
        new EmberClient().onInitializeClient();
        ParadiseClient.init();
        DarkForestClient.init();
        SettlementClient.init();
    }
}

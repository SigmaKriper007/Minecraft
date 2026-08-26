package com.opus.fire.client;

import com.opus.fire.client.renderer.*;
import com.opus.fire.client.particle.AshParticle;
import com.opus.fire.client.particle.EmberParticle;
import com.opus.fire.registry.FireEntities;
import com.opus.fire.registry.FireParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import com.opus.fire.registry.FireBlocks;
import com.opus.fire.registry.FireItems;
import com.opus.fire.client.model.FireHelmetModel;
import com.opus.fire.client.model.FirePlateModel;
import net.minecraft.client.renderer.RenderType;

public class FireClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FireClientNetwork.init();
        FireHud.init();
        FireBiomAudio.init();

        // Particles
        ParticleFactoryRegistry.getInstance().register(FireParticles.EMBER, EmberParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(FireParticles.ASH, AshParticle.Provider::new);

        // Renderers
        EntityRendererRegistry.register(FireEntities.FIRE_SLIME, FireSlimeRenderer::new);
        EntityRendererRegistry.register(FireEntities.LAVA_GOLEM, LavaGolemRenderer::new);
        EntityRendererRegistry.register(FireEntities.FIRE_DEMON, FireDemonRenderer::new);
        EntityRendererRegistry.register(FireEntities.FIREBALL, FireballRenderer::new);
        EntityRendererRegistry.register(FireEntities.DEMONIC_TRIDENT_ENTITY, DemonicTridentRenderer::new);
        EntityRendererRegistry.register(FireEntities.FIRE_AURA_WAVE, FireAuraWaveRenderer::new);
        DemonicTridentItemRenderer tridentItemRenderer = new DemonicTridentItemRenderer();
        BuiltinItemRendererRegistry.INSTANCE.register(FireItems.DEMONIC_TRIDENT, tridentItemRenderer::renderByItem);

        // Armor model layers (3D)
        EntityModelLayerRegistry.registerModelLayer(FireHelmetModel.LAYER_LOCATION, FireHelmetModel::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(FirePlateModel.LAYER_LOCATION, FirePlateModel::createLayerDefinition);

        // Translucent blocks
        BlockRenderLayerMap.INSTANCE.putBlock(FireBlocks.CRIMSON_ICE, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(FireBlocks.FIRE_VINE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(FireBlocks.FIRE_BEAN, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(FireBlocks.EMBER_LEAVES, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(FireBlocks.EMBER_SAPLING, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(FireBlocks.FIRE_PORTAL, RenderType.translucent());
    }
}

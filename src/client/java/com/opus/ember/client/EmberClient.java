package com.opus.ember.client;

import com.opus.ember.client.model.EmberHelmetModel;
import com.opus.ember.client.model.EmberPlateModel;
import com.opus.ember.client.particle.EmberAshParticle;
import com.opus.ember.client.particle.EmberSparkParticle;
import com.opus.ember.client.renderer.BlazingTridentRenderer;
import com.opus.ember.client.renderer.EmberAuraWaveRenderer;
import com.opus.ember.client.renderer.EmberFireballRenderer;
import com.opus.ember.client.renderer.EmberSlimeRenderer;
import com.opus.ember.client.renderer.FlameDemonRenderer;
import com.opus.ember.client.renderer.ObsidianGolemRenderer;
import com.opus.ember.registry.EmberBlocks;
import com.opus.ember.registry.EmberEntities;
import com.opus.ember.registry.EmberParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.RenderType;

public class EmberClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EmberClientNetwork.init();
        EmberHud.init();

        ParticleFactoryRegistry.getInstance().register(EmberParticles.EMBER_SPARK, EmberSparkParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(EmberParticles.EMBER_ASH, EmberAshParticle.Provider::new);

        EntityRendererRegistry.register(EmberEntities.EMBER_SLIME, EmberSlimeRenderer::new);
        EntityRendererRegistry.register(EmberEntities.OBSIDIAN_GOLEM, ObsidianGolemRenderer::new);
        EntityRendererRegistry.register(EmberEntities.FLAME_DEMON, FlameDemonRenderer::new);
        EntityRendererRegistry.register(EmberEntities.EMBER_FIREBALL, EmberFireballRenderer::new);
        EntityRendererRegistry.register(EmberEntities.BLAZING_TRIDENT, BlazingTridentRenderer::new);
        EntityRendererRegistry.register(EmberEntities.EMBER_AURA_WAVE, EmberAuraWaveRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(EmberHelmetModel.LAYER_LOCATION, EmberHelmetModel::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(EmberPlateModel.LAYER_LOCATION, EmberPlateModel::createLayerDefinition);

        BlockRenderLayerMap.INSTANCE.putBlock(EmberBlocks.CINDER_SEAL, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(EmberBlocks.CINDER_VINE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(EmberBlocks.CINDER_BEAN, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(EmberBlocks.CINDER_LEAVES, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(EmberBlocks.CINDER_PORTAL, RenderType.translucent());
    }
}

package com.opus.settlement.client;

import com.opus.settlement.client.model.SurvivorModel;
import com.opus.settlement.client.model.BlackNinjaModel;
import com.opus.settlement.client.model.SamuraiModel;
import com.opus.settlement.client.model.YoungSamuraiModel;
import com.opus.settlement.client.renderer.SurvivorRenderer;
import com.opus.settlement.client.renderer.BlackNinjaRenderer;
import com.opus.settlement.client.renderer.SamuraiRenderer;
import com.opus.settlement.client.renderer.YoungSamuraiRenderer;
import com.opus.settlement.registry.SettlementEntities;
import com.opus.settlement.registry.SettlementItems;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CompassItem;

public final class SettlementClient {
    private SettlementClient() { }
    public static void init() {
        EntityModelLayerRegistry.registerModelLayer(SurvivorModel.LAYER, SurvivorModel::createLayer);
        EntityRendererRegistry.register(SettlementEntities.SURVIVOR, SurvivorRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(BlackNinjaModel.LAYER, BlackNinjaModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(SamuraiModel.LAYER, SamuraiModel::createLayer);
        EntityRendererRegistry.register(SettlementEntities.BLACK_NINJA, BlackNinjaRenderer::new);
        EntityRendererRegistry.register(SettlementEntities.SAMURAI, SamuraiRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(YoungSamuraiModel.LAYER, YoungSamuraiModel::createLayer);
        EntityRendererRegistry.register(SettlementEntities.YOUNG_SAMURAI, YoungSamuraiRenderer::new);
        for (var item : java.util.List.of(SettlementItems.OPUS_RUINS_COMPASS, SettlementItems.PARADISE_COMPASS,
            SettlementItems.DARK_FOREST_COMPASS, SettlementItems.MOON_FOUNTAIN_COMPASS)) {
            FabricModelPredicateProviderRegistry.register(item, new ResourceLocation("angle"),
                new CompassItemPropertyFunction((level, stack, entity) -> CompassItem.isLodestoneCompass(stack)
                    ? CompassItem.getLodestonePosition(stack.getTag()) : CompassItem.getSpawnPosition(level)));
        }
    }
}

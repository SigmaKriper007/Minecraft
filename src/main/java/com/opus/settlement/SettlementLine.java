package com.opus.settlement;

import com.opus.OpusVsExe;
import com.opus.settlement.entity.SurvivorEntity;
import com.opus.settlement.entity.BlackNinjaEntity;
import com.opus.settlement.entity.SamuraiEntity;
import com.opus.settlement.entity.YoungSamuraiEntity;
import com.opus.settlement.qa.SurvivorSettlementQa;
import com.opus.settlement.qa.JapaneseSettlementQa;
import com.opus.settlement.qa.YoungSamuraiQa;
import com.opus.settlement.registry.SettlementCreativeTab;
import com.opus.settlement.registry.SettlementEntities;
import com.opus.settlement.registry.SettlementItems;
import com.opus.settlement.sound.SettlementSounds;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.resources.ResourceLocation;

public final class SettlementLine {
    private SettlementLine() { }

    public static void init() {
        SettlementEntities.init();
        SettlementItems.init();
        SettlementCreativeTab.init();
        SettlementSounds.init();
        FabricDefaultAttributeRegistry.register(SettlementEntities.SURVIVOR, SurvivorEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SettlementEntities.BLACK_NINJA, BlackNinjaEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SettlementEntities.SAMURAI, SamuraiEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SettlementEntities.YOUNG_SAMURAI, YoungSamuraiEntity.createAttributes());
        SurvivorSettlementQa.init();
        JapaneseSettlementQa.init();
        YoungSamuraiQa.init();
    }

    public static ResourceLocation id(String path) { return OpusVsExe.id(path); }
}

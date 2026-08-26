package com.opus.ember;

import com.opus.ember.entity.EmberSlimeEntity;
import com.opus.ember.entity.FlameDemonEntity;
import com.opus.ember.entity.ObsidianGolemEntity;
import com.opus.ember.item.EmberArmorBonus;
import com.opus.ember.network.EmberNetwork;
import com.opus.ember.registry.EmberBlocks;
import com.opus.ember.registry.EmberEntities;
import com.opus.ember.registry.EmberItems;
import com.opus.ember.registry.EmberParticles;
import com.opus.ember.sound.EmberSounds;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.resources.ResourceLocation;

public final class EmberLine {
    public static final String MOD_ID = "opusvsexe";
    private EmberLine() { }

    public static void init() {
        EmberBlocks.init();
        EmberItems.init();
        EmberEntities.init();
        EmberParticles.init();
        EmberSounds.init();
        EmberNetwork.init();
        EmberArmorBonus.init();
        FabricDefaultAttributeRegistry.register(EmberEntities.EMBER_SLIME, EmberSlimeEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(EmberEntities.OBSIDIAN_GOLEM, ObsidianGolemEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(EmberEntities.FLAME_DEMON, FlameDemonEntity.createAttributes());
    }

    public static ResourceLocation id(String path) { return new ResourceLocation(MOD_ID, path); }
}

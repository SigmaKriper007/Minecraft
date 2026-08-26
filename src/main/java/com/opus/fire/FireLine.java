package com.opus.fire;

import com.opus.fire.entity.FireDemonEntity;
import com.opus.fire.entity.FireSlimeEntity;
import com.opus.fire.entity.LavaGolemEntity;
import com.opus.fire.item.FireArmorBonus;
import com.opus.fire.item.FireToolEvents;
import com.opus.fire.network.FireNetwork;
import com.opus.fire.registry.FireBlocks;
import com.opus.fire.registry.FireCreativeTab;
import com.opus.fire.registry.FireEntities;
import com.opus.fire.registry.FireItems;
import com.opus.fire.registry.FireParticles;
import com.opus.fire.sound.FireSounds;
import com.opus.fire.world.FireWorldgen;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.resources.ResourceLocation;

public final class FireLine {
    public static final String MOD_ID = "opusvsexe";
    private FireLine() { }

    public static void init() {
        FireBlocks.init();
        FireItems.init();
        FireEntities.init();
        FireParticles.init();
        FireSounds.init();
        FireCreativeTab.init();
        FireNetwork.init();
        FireArmorBonus.init();
        FireToolEvents.init();
        FireWorldgen.init();
        FabricDefaultAttributeRegistry.register(FireEntities.FIRE_SLIME, FireSlimeEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(FireEntities.LAVA_GOLEM, LavaGolemEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(FireEntities.FIRE_DEMON, FireDemonEntity.createAttributes());
    }

    public static ResourceLocation id(String path) { return new ResourceLocation(MOD_ID, path); }
}

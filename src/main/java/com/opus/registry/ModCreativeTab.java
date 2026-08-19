package com.opus.registry;

import com.opus.OpusVsExe;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTab {
    public static final ResourceKey<CreativeModeTab> OPUSVSEXE = ResourceKey.create(Registries.CREATIVE_MODE_TAB, OpusVsExe.id("opusvsexe"));

    public static final CreativeModeTab TAB = Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB, OpusVsExe.id("opusvsexe"),
        FabricItemGroup.builder()
            .title(Component.translatable("itemGroup.opusvsexe.opus_tab"))
            .icon(() -> new ItemStack(ModItems.CORE_OPUS))
            .build());

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(OPUSVSEXE).register(entries -> {
            entries.accept(ModItems.OPUS_ORE);
            entries.accept(ModItems.RAW_OPUS_BLOCK);
            entries.accept(ModItems.STABILIZED_OPUS_BLOCK);
            entries.accept(ModItems.RESONANT_OPUS_BLOCK);
            entries.accept(ModItems.CORE_OPUS_BLOCK);
            entries.accept(ModItems.RESONANCE_FORGE);
            entries.accept(ModItems.CRACKED_LAB_CONCRETE);
            entries.accept(ModItems.LAB_FLOOR_GRATE);
            entries.accept(ModItems.OPUS_CONTAINMENT_GLASS);
            entries.accept(ModItems.SCORCHED_CONCRETE);
            entries.accept(ModItems.OIL_STAIN);
            entries.accept(ModItems.FORTRESS_PLATING);
            entries.accept(ModItems.MEMORY_GLASS);
            entries.accept(ModItems.OMEGA_FRAME);
            entries.accept(ModItems.CITADEL_VEIN);
            entries.accept(ModItems.TANK_TRAP);
            entries.accept(ModItems.DATA_CONDUIT);
            entries.accept(ModItems.MEMORY_CABLE);
            entries.accept(ModItems.SIGNAL_PANEL);
            entries.accept(ModItems.PULSING_CORE);
            entries.accept(ModItems.BROKEN_EXO_HULL);
            entries.accept(ModItems.EXO_ASSEMBLY_FRAME);
            entries.accept(ModItems.WELDING_BENCH);
            entries.accept(ModItems.CORE_CRATE);
            entries.accept(ModItems.MEMORY_SLUDGE);
            entries.accept(ModItems.HAZARD_EMITTER);
            entries.accept(ModItems.MARSH_FILTER);
            entries.accept(ModItems.DEAD_TERMINAL);
            entries.accept(ModItems.FLICKERING_TERMINAL);
            entries.accept(ModItems.BLUEPRINT_TABLE);
            entries.accept(ModItems.BROKEN_RESONANCE_FORGE);
            entries.accept(ModItems.KATANA_STAND);
            entries.accept(ModItems.SCANNER_EYE);
            entries.accept(ModItems.PULSE_TURRET);
            entries.accept(ModItems.WAVE_TERMINAL);
            entries.accept(ModItems.COMMAND_TERMINAL);
            entries.accept(ModItems.MEMORY_CONSOLE);
            entries.accept(ModItems.ARENA_GATE);
            entries.accept(ModItems.PHASE_GATE);
            entries.accept(ModItems.SEALED_BULKHEAD);
            entries.accept(ModItems.SEALED_HATCH);
            entries.accept(ModItems.SHIELD_NODE);
            entries.accept(ModItems.GRAVITY_ANCHOR);
            entries.accept(ModItems.COMBAT_BEACON);
            entries.accept(ModItems.TRIAL_TRIGGER);
            entries.accept(ModItems.REWARD_VAULT);
            entries.accept(ModItems.DORMANT_SPAWNER);
            entries.accept(ModItems.RAW_OPUS);
            entries.accept(ModItems.STABILIZED_OPUS);
            entries.accept(ModItems.RESONANT_OPUS);
            entries.accept(ModItems.CORE_OPUS);
            entries.accept(ModItems.MEMORY_FRAGMENT_1);
            entries.accept(ModItems.MEMORY_FRAGMENT_2);
            entries.accept(ModItems.MEMORY_FRAGMENT_3);
            entries.accept(ModItems.MEMORY_FRAGMENT_4);
            entries.accept(ModItems.MEMORY_FRAGMENT_5);
            entries.accept(ModItems.MEMORY_FRAGMENT_6);
            entries.accept(ModItems.MEMORY_FRAGMENT_7);
            entries.accept(ModItems.MEMORY_FRAGMENT_8);
            entries.accept(ModItems.MEMORY_FRAGMENT_9);
            entries.accept(ModItems.MEMORY_FRAGMENT_10);
            entries.accept(ModItems.MEMORY_FRAGMENT_11);
            entries.accept(ModItems.MEMORY_FRAGMENT_12);
            entries.accept(ModItems.MEMORY_FRAGMENT_13);
            entries.accept(ModItems.MEMORY_FRAGMENT_14);
            entries.accept(ModItems.MEMORY_FRAGMENT_15);
            entries.accept(ModItems.KATANA_OP);
            entries.accept(ModItems.KATANA_GOLD);
            entries.accept(ModItems.KATANA_REFINED);
            entries.accept(ModItems.OPUS_WARHAMMER);
            entries.accept(ModItems.LASER_GUN);
            entries.accept(ModItems.RADIO);
            entries.accept(ModItems.DOOM_ETERNAL_DISC);
            entries.accept(ModItems.OPUS_HELMET);
            entries.accept(ModItems.OPUS_CHESTPLATE);
            entries.accept(ModItems.OPUS_LEGGINGS);
            entries.accept(ModItems.OPUS_BOOTS);
            entries.accept(ModItems.SHADOW_HELMET);
            entries.accept(ModItems.SHADOW_CHESTPLATE);
            entries.accept(ModItems.SHADOW_LEGGINGS);
            entries.accept(ModItems.SHADOW_BOOTS);
            entries.accept(ModItems.HAIKU_1_5_SPAWN_EGG);
            entries.accept(ModItems.HAIKU_2_SPAWN_EGG);
            entries.accept(ModItems.HAIKU_3_SPAWN_EGG);
            entries.accept(ModItems.HAIKU_4_SPAWN_EGG);
            entries.accept(ModItems.HAIKU_5_SPAWN_EGG);
            entries.accept(ModItems.HAIKU_OMEGA_SPAWN_EGG);
            entries.accept(ModItems.EXO_1_SPAWN_EGG);
            entries.accept(ModItems.EXO_2_SPAWN_EGG);
            entries.accept(ModItems.EXO_3_SPAWN_EGG);
            entries.accept(ModItems.EXO_4_SPAWN_EGG);
            entries.accept(ModItems.EXO_5_SPAWN_EGG);
        });
    }
}
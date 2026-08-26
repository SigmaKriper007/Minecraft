package com.opus.registry;

import com.opus.OpusVsExe;
import com.opus.item.HaikuCoreItem;
import com.opus.item.HeavyLaserGunItem;
import com.opus.item.KatanaItem;
import com.opus.item.LaserGunItem;
import com.opus.item.LightLaserGunItem;
import com.opus.item.MemoryFragmentItem;
import com.opus.item.ModTier;
import com.opus.item.OpusArmorMaterial;
import com.opus.item.RadioItem;
import com.opus.item.SkyLaserGunItem;
import com.opus.item.ShadowAssassinArmorItem;
import com.opus.item.ShadowAssassinArmorMaterial;
import com.opus.item.WarhammerItem;
import com.opus.sound.ModSounds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SpawnEggItem;

public class ModItems {
    // Block items
    public static final Item OPUS_ORE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("opus_ore"),
        new BlockItem(ModBlocks.OPUS_ORE, new Item.Properties()));
    public static final Item RAW_OPUS_BLOCK = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("raw_opus_block"),
        new BlockItem(ModBlocks.RAW_OPUS_BLOCK, new Item.Properties()));
    public static final Item STABILIZED_OPUS_BLOCK = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("stabilized_opus_block"),
        new BlockItem(ModBlocks.STABILIZED_OPUS_BLOCK, new Item.Properties()));
    public static final Item RESONANT_OPUS_BLOCK = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("resonant_opus_block"),
        new BlockItem(ModBlocks.RESONANT_OPUS_BLOCK, new Item.Properties()));
    public static final Item CORE_OPUS_BLOCK = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("core_opus_block"),
        new BlockItem(ModBlocks.CORE_OPUS_BLOCK, new Item.Properties()));
    public static final Item RESONANCE_FORGE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("resonance_forge"),
        new BlockItem(ModBlocks.RESONANCE_FORGE, new Item.Properties()));

    // New structure block items
    public static final Item CRACKED_LAB_CONCRETE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("cracked_lab_concrete"),
        new BlockItem(ModBlocks.CRACKED_LAB_CONCRETE, new Item.Properties()));
    public static final Item LAB_FLOOR_GRATE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("lab_floor_grate"),
        new BlockItem(ModBlocks.LAB_FLOOR_GRATE, new Item.Properties()));
    public static final Item OPUS_CONTAINMENT_GLASS = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("opus_containment_glass"),
        new BlockItem(ModBlocks.OPUS_CONTAINMENT_GLASS, new Item.Properties()));
    public static final Item SCORCHED_CONCRETE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("scorched_concrete"),
        new BlockItem(ModBlocks.SCORCHED_CONCRETE, new Item.Properties()));
    public static final Item OIL_STAIN = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("oil_stain"),
        new BlockItem(ModBlocks.OIL_STAIN, new Item.Properties()));
    public static final Item FORTRESS_PLATING = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("fortress_plating"),
        new BlockItem(ModBlocks.FORTRESS_PLATING, new Item.Properties()));
    public static final Item MEMORY_GLASS = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("memory_glass"),
        new BlockItem(ModBlocks.MEMORY_GLASS, new Item.Properties()));
    public static final Item OMEGA_FRAME = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("omega_frame"),
        new BlockItem(ModBlocks.OMEGA_FRAME, new Item.Properties()));
    public static final Item CITADEL_VEIN = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("citadel_vein"),
        new BlockItem(ModBlocks.CITADEL_VEIN, new Item.Properties()));
    public static final Item TANK_TRAP = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("tank_trap"),
        new BlockItem(ModBlocks.TANK_TRAP, new Item.Properties()));
    public static final Item DATA_CONDUIT = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("data_conduit"),
        new BlockItem(ModBlocks.DATA_CONDUIT, new Item.Properties()));
    public static final Item MEMORY_CABLE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("memory_cable"),
        new BlockItem(ModBlocks.MEMORY_CABLE, new Item.Properties()));
    public static final Item SIGNAL_PANEL = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("signal_panel"),
        new BlockItem(ModBlocks.SIGNAL_PANEL, new Item.Properties()));
    public static final Item PULSING_CORE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("pulsing_core"),
        new BlockItem(ModBlocks.PULSING_CORE, new Item.Properties()));
    public static final Item BROKEN_EXO_HULL = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("broken_exo_hull"),
        new BlockItem(ModBlocks.BROKEN_EXO_HULL, new Item.Properties()));
    public static final Item EXO_ASSEMBLY_FRAME = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("exo_assembly_frame"),
        new BlockItem(ModBlocks.EXO_ASSEMBLY_FRAME, new Item.Properties()));
    public static final Item WELDING_BENCH = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("welding_bench"),
        new BlockItem(ModBlocks.WELDING_BENCH, new Item.Properties()));
    public static final Item CORE_CRATE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("core_crate"),
        new BlockItem(ModBlocks.CORE_CRATE, new Item.Properties()));
    public static final Item MEMORY_SLUDGE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("memory_sludge"),
        new BlockItem(ModBlocks.MEMORY_SLUDGE, new Item.Properties()));
    public static final Item HAZARD_EMITTER = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("hazard_emitter"),
        new BlockItem(ModBlocks.HAZARD_EMITTER, new Item.Properties()));
    public static final Item MARSH_FILTER = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("marsh_filter"),
        new BlockItem(ModBlocks.MARSH_FILTER, new Item.Properties()));

    public static final Item DEAD_TERMINAL = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("dead_terminal"),
        new BlockItem(ModBlocks.DEAD_TERMINAL, new Item.Properties()));
    public static final Item FLICKERING_TERMINAL = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("flickering_terminal"),
        new BlockItem(ModBlocks.FLICKERING_TERMINAL, new Item.Properties()));
    public static final Item BLUEPRINT_TABLE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("blueprint_table"),
        new BlockItem(ModBlocks.BLUEPRINT_TABLE, new Item.Properties()));
    public static final Item BROKEN_RESONANCE_FORGE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("broken_resonance_forge"),
        new BlockItem(ModBlocks.BROKEN_RESONANCE_FORGE, new Item.Properties()));
    public static final Item KATANA_STAND = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("katana_stand"),
        new BlockItem(ModBlocks.KATANA_STAND, new Item.Properties()));
    public static final Item SCANNER_EYE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("scanner_eye"),
        new BlockItem(ModBlocks.SCANNER_EYE, new Item.Properties()));
    public static final Item PULSE_TURRET = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("pulse_turret"),
        new BlockItem(ModBlocks.PULSE_TURRET, new Item.Properties()));
    public static final Item WAVE_TERMINAL = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("wave_terminal"),
        new BlockItem(ModBlocks.WAVE_TERMINAL, new Item.Properties()));
    public static final Item COMMAND_TERMINAL = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("command_terminal"),
        new BlockItem(ModBlocks.COMMAND_TERMINAL, new Item.Properties()));
    public static final Item MEMORY_CONSOLE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("memory_console"),
        new BlockItem(ModBlocks.MEMORY_CONSOLE, new Item.Properties()));

    public static final Item ARENA_GATE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("arena_gate"),
        new BlockItem(ModBlocks.ARENA_GATE, new Item.Properties()));
    public static final Item PHASE_GATE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("phase_gate"),
        new BlockItem(ModBlocks.PHASE_GATE, new Item.Properties()));
    public static final Item SEALED_BULKHEAD = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("sealed_bulkhead"),
        new BlockItem(ModBlocks.SEALED_BULKHEAD, new Item.Properties()));
    public static final Item SEALED_HATCH = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("sealed_hatch"),
        new BlockItem(ModBlocks.SEALED_HATCH, new Item.Properties()));
    public static final Item SHIELD_NODE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("shield_node"),
        new BlockItem(ModBlocks.SHIELD_NODE, new Item.Properties()));
    public static final Item GRAVITY_ANCHOR = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("gravity_anchor"),
        new BlockItem(ModBlocks.GRAVITY_ANCHOR, new Item.Properties()));
    public static final Item COMBAT_BEACON = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("combat_beacon"),
        new BlockItem(ModBlocks.COMBAT_BEACON, new Item.Properties()));

    public static final Item TRIAL_TRIGGER = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("trial_trigger"),
        new BlockItem(ModBlocks.TRIAL_TRIGGER, new Item.Properties()));
    public static final Item REWARD_VAULT = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("reward_vault"),
        new BlockItem(ModBlocks.REWARD_VAULT, new Item.Properties()));
    public static final Item DORMANT_SPAWNER = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("dormant_spawner"),
        new BlockItem(ModBlocks.DORMANT_SPAWNER, new Item.Properties()));

    // Eternal Colosseum block items
    public static final Item REINFORCED_OPUS_BLOCK = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("reinforced_opus_block"),
        new BlockItem(ModBlocks.REINFORCED_OPUS_BLOCK, new Item.Properties()));
    public static final Item HAIKU_AMBER_BLOCK = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("haiku_amber_block"),
        new BlockItem(ModBlocks.HAIKU_AMBER_BLOCK, new Item.Properties()));
    public static final Item ALTAR_HEART = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("altar_heart"),
        new BlockItem(ModBlocks.ALTAR_HEART, new Item.Properties()));
    public static final Item COLOSSEUM_CONCRETE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("colosseum_concrete"),
        new BlockItem(ModBlocks.COLOSSEUM_CONCRETE, new Item.Properties()));
    public static final Item AMBER_PILLAR = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("amber_pillar"),
        new BlockItem(ModBlocks.AMBER_PILLAR, new Item.Properties()));
    public static final Item COLOSSEUM_WALL = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("colosseum_wall"),
        new BlockItem(ModBlocks.COLOSSEUM_WALL, new Item.Properties()));

    // Energy barriers
    public static final Item ENERGY_BARRIER = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("energy_barrier"),
        new BlockItem(ModBlocks.ENERGY_BARRIER, new Item.Properties()));
    public static final Item ENERGY_BARRIER_RED = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("energy_barrier_red"),
        new BlockItem(ModBlocks.ENERGY_BARRIER_RED, new Item.Properties()));
    public static final Item ENERGY_BARRIER_BLUE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("energy_barrier_blue"),
        new BlockItem(ModBlocks.ENERGY_BARRIER_BLUE, new Item.Properties()));
    public static final Item ENERGY_BEAM = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("energy_beam"),
        new BlockItem(ModBlocks.ENERGY_BEAM, new Item.Properties()));
    public static final Item FORCE_FIELD_PROJECTOR = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("force_field_projector"),
        new BlockItem(ModBlocks.FORCE_FIELD_PROJECTOR, new Item.Properties()));
    public static final Item PHASED_BARRIER = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("phased_barrier"),
        new BlockItem(ModBlocks.PHASED_BARRIER, new Item.Properties()));

    // Puzzle blocks (задача 19)
    public static final Item SEQUENCE_KEYPAD = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("sequence_keypad"),
        new BlockItem(ModBlocks.SEQUENCE_KEYPAD, new Item.Properties()));
    public static final Item ENERGY_RELAY = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("energy_relay"),
        new BlockItem(ModBlocks.ENERGY_RELAY, new Item.Properties()));

    // Opus materials
    public static final Item RAW_OPUS = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("raw_opus"), new Item(new Item.Properties()));
    public static final Item STABILIZED_OPUS = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("stabilized_opus"), new Item(new Item.Properties()));
    public static final Item RESONANT_OPUS = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("resonant_opus"), new Item(new Item.Properties()));
    public static final Item CORE_OPUS = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("core_opus"), new Item(new Item.Properties()));
    public static final Item TITAN_PLATE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("titan_plate"),
        new Item(new Item.Properties().fireResistant()));

    // Memory Fragments (lore items)
    public static final Item MEMORY_FRAGMENT_1 = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("memory_fragment_1"), new MemoryFragmentItem(1));
    public static final Item MEMORY_FRAGMENT_2 = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("memory_fragment_2"), new MemoryFragmentItem(2));
    public static final Item MEMORY_FRAGMENT_3 = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("memory_fragment_3"), new MemoryFragmentItem(3));
    public static final Item MEMORY_FRAGMENT_4 = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("memory_fragment_4"), new MemoryFragmentItem(4));
    public static final Item MEMORY_FRAGMENT_5 = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("memory_fragment_5"), new MemoryFragmentItem(5));
    public static final Item MEMORY_FRAGMENT_6 = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("memory_fragment_6"), new MemoryFragmentItem(6));
    public static final Item MEMORY_FRAGMENT_7 = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("memory_fragment_7"), new MemoryFragmentItem(7));
    public static final Item MEMORY_FRAGMENT_8 = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("memory_fragment_8"), new MemoryFragmentItem(8));
    public static final Item MEMORY_FRAGMENT_9 = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("memory_fragment_9"), new MemoryFragmentItem(9));
    public static final Item MEMORY_FRAGMENT_10 = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("memory_fragment_10"), new MemoryFragmentItem(10));
    public static final Item MEMORY_FRAGMENT_11 = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("memory_fragment_11"), new MemoryFragmentItem(11));
    public static final Item MEMORY_FRAGMENT_12 = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("memory_fragment_12"), new MemoryFragmentItem(12));
    public static final Item MEMORY_FRAGMENT_13 = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("memory_fragment_13"), new MemoryFragmentItem(13));
    public static final Item MEMORY_FRAGMENT_14 = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("memory_fragment_14"), new MemoryFragmentItem(14));
    public static final Item MEMORY_FRAGMENT_15 = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("memory_fragment_15"), new MemoryFragmentItem(15));

    // Weapons
    public static final Item KATANA_OP = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("katana_op"), new KatanaItem(ModTier.OPUS, 2, -2.0f, new Item.Properties().stacksTo(1)));
    public static final Item KATANA_GOLD = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("katana_gold"), new KatanaItem(ModTier.OPUS, 2, -2.0f, new Item.Properties().stacksTo(1)));
    public static final Item KATANA_REFINED = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("katana_refined"), new KatanaItem(ModTier.OPUS, 2, -2.0f, new Item.Properties().stacksTo(1)));
    public static final Item OPUS_WARHAMMER = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("opus_warhammer"), new WarhammerItem(ModTier.OPUS, 5, -3.2f, new Item.Properties().stacksTo(1)));
    public static final Item OPUS_PICKAXE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("opus_pickaxe"),
        new PickaxeItem(ModTier.OPUS, 1, -2.8F, new Item.Properties()));
    public static final Item OPUS_AXE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("opus_axe"),
        new AxeItem(ModTier.OPUS, 5.0F, -3.0F, new Item.Properties()));
    public static final Item OPUS_SHOVEL = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("opus_shovel"),
        new ShovelItem(ModTier.OPUS, 1.5F, -3.0F, new Item.Properties()));
    public static final Item OPUS_HOE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("opus_hoe"),
        new HoeItem(ModTier.OPUS, -4, 0.0F, new Item.Properties()));
    public static final Item LASER_GUN = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("laser_gun"),
        new LaserGunItem(new Item.Properties().stacksTo(1)));
    public static final Item LIGHT_LASER_GUN = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("light_laser_gun"),
        new LightLaserGunItem(new Item.Properties().stacksTo(1)));
    public static final Item HEAVY_LASER_GUN = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("heavy_laser_gun"),
        new HeavyLaserGunItem(new Item.Properties().stacksTo(1)));
    public static final Item SKY_LASER_GUN = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("sky_laser_gun"),
        new SkyLaserGunItem(new Item.Properties().stacksTo(1)));
    public static final Item RADIO = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("radio"),
        new RadioItem(new Item.Properties().stacksTo(1)));

    // Music disc
    public static final Item DOOM_ETERNAL_DISC = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("doom_eternal_disc"),
        new RecordItem(15, ModSounds.DOOM_ETERNAL_DISC, new Item.Properties().stacksTo(1), 414));

    // Opus armor
    private static final OpusArmorMaterial OPUS_ARMOR = new OpusArmorMaterial();
    public static final Item OPUS_HELMET = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("opus_helmet"),
        new ArmorItem(OPUS_ARMOR, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final Item OPUS_CHESTPLATE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("opus_chestplate"),
        new ArmorItem(OPUS_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final Item OPUS_LEGGINGS = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("opus_leggings"),
        new ArmorItem(OPUS_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final Item OPUS_BOOTS = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("opus_boots"),
        new ArmorItem(OPUS_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties()));

    // Shadow Assassin armor
    private static final ShadowAssassinArmorMaterial SHADOW_ARMOR = new ShadowAssassinArmorMaterial();
    public static final Item SHADOW_HELMET = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("shadow_helmet"),
        new ShadowAssassinArmorItem(SHADOW_ARMOR, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final Item SHADOW_CHESTPLATE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("shadow_chestplate"),
        new ShadowAssassinArmorItem(SHADOW_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final Item SHADOW_LEGGINGS = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("shadow_leggings"),
        new ShadowAssassinArmorItem(SHADOW_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final Item SHADOW_BOOTS = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("shadow_boots"),
        new ShadowAssassinArmorItem(SHADOW_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties()));

    // Eternal Colosseum items
    public static final Item HAIKU_CORE = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("haiku_core"),
        new HaikuCoreItem(new Item.Properties()));
    public static final Item OPUS_FRAGMENT = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("opus_fragment"),
        new Item(new Item.Properties()));
    public static final Item AI_TEAR = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("ai_tear"),
        new Item(new Item.Properties()));

    // Haiku spawn eggs
    public static final Item HAIKU_1_5_SPAWN_EGG = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("haiku_1_5_spawn_egg"),
        new SpawnEggItem(ModEntities.HAIKU_1_5, 0x444444, 0xa0a0a0, new Item.Properties()));
    public static final Item HAIKU_2_SPAWN_EGG = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("haiku_2_spawn_egg"),
        new SpawnEggItem(ModEntities.HAIKU_2, 0x2f6f7a, 0x63d9e8, new Item.Properties()));
    public static final Item HAIKU_3_SPAWN_EGG = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("haiku_3_spawn_egg"),
        new SpawnEggItem(ModEntities.HAIKU_3, 0x6b2f2f, 0xd96d4b, new Item.Properties()));
    public static final Item HAIKU_4_SPAWN_EGG = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("haiku_4_spawn_egg"),
        new SpawnEggItem(ModEntities.HAIKU_4, 0x7a4a1f, 0xff9a3c, new Item.Properties()));
    public static final Item HAIKU_5_SPAWN_EGG = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("haiku_5_spawn_egg"),
        new SpawnEggItem(ModEntities.HAIKU_5, 0x4a1515, 0xb03030, new Item.Properties()));
    public static final Item HAIKU_OMEGA_SPAWN_EGG = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("haiku_omega_spawn_egg"),
        new SpawnEggItem(ModEntities.HAIKU_OMEGA, 0x1f1740, 0x8f5ff0, new Item.Properties()));
    public static final Item HAIKU_DRONE_SPAWN_EGG = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("haiku_drone_spawn_egg"),
        new SpawnEggItem(ModEntities.HAIKU_DRONE, 0xE8E2D4, 0xFFB93E, new Item.Properties()));
    public static final Item HAIKU_DRONE_PLUS_SPAWN_EGG = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("haiku_drone_plus_spawn_egg"),
        new SpawnEggItem(ModEntities.HAIKU_DRONE_PLUS, 0xB8A88A, 0xFF7A1A, new Item.Properties()));

    // EXO spawn eggs
    public static final Item EXO_1_SPAWN_EGG = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("exo_1_sentinel_spawn_egg"),
        new SpawnEggItem(ModEntities.EXO_1_SENTINEL, 0x1f3a4d, 0x7ab8d9, new Item.Properties()));
    public static final Item EXO_2_SPAWN_EGG = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("exo_2_hunter_spawn_egg"),
        new SpawnEggItem(ModEntities.EXO_2_HUNTER, 0x17452e, 0x63d99b, new Item.Properties()));
    public static final Item EXO_3_SPAWN_EGG = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("exo_3_vanguard_spawn_egg"),
        new SpawnEggItem(ModEntities.EXO_3_VANGUARD, 0x4d3b1f, 0xd9b763, new Item.Properties()));
    public static final Item EXO_4_SPAWN_EGG = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("exo_4_titan_spawn_egg"),
        new SpawnEggItem(ModEntities.EXO_4_TITAN, 0x3a1f3a, 0xc763d9, new Item.Properties()));
    public static final Item EXO_5_SPAWN_EGG = Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id("exo_5_vengeance_spawn_egg"),
        new SpawnEggItem(ModEntities.EXO_5_VENGEANCE, 0x5A1420, 0xE8941E, new Item.Properties()));
    public static void init() {
    }
}

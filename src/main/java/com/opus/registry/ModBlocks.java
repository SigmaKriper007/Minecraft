package com.opus.registry;

import com.opus.OpusVsExe;
import com.opus.block.DormantSpawnerBlock;
import com.opus.block.MemoryConsoleBlock;
import com.opus.block.OpusHorizontalBlock;
import com.opus.block.PoweredHorizontalBlock;
import com.opus.block.PulsingCoreBlock;
import com.opus.block.AmberGlowBlock;
import com.opus.block.ResonanceForgeBlock;
import com.opus.block.RewardVaultBlock;
import com.opus.block.ToggleBlock;
import com.opus.block.TrialTriggerBlock;
import com.opus.block.AltarHeartBlock;
import com.opus.block.EnergyBarrierBlock;
import com.opus.block.EnergyBeamBlock;
import com.opus.block.EnergyRelayBlock;
import com.opus.block.ForceFieldProjectorBlock;
import com.opus.block.PhasedBarrierBlock;
import com.opus.block.SequenceKeypadBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class ModBlocks {
    public static final Block OPUS_ORE = register("opus_ore",
        new Block(BlockBehaviour.Properties.of().strength(4.0f, 3.0f).requiresCorrectToolForDrops()));

    public static final Block RAW_OPUS_BLOCK = register("raw_opus_block",
        new Block(BlockBehaviour.Properties.of().strength(5.0f, 4.0f).requiresCorrectToolForDrops()));

    public static final Block STABILIZED_OPUS_BLOCK = register("stabilized_opus_block",
        new Block(BlockBehaviour.Properties.of().strength(6.0f, 5.0f).requiresCorrectToolForDrops()));

    public static final Block RESONANT_OPUS_BLOCK = register("resonant_opus_block",
        new Block(BlockBehaviour.Properties.of().strength(7.0f, 6.0f).requiresCorrectToolForDrops()));

    public static final Block CORE_OPUS_BLOCK = register("core_opus_block",
        new Block(BlockBehaviour.Properties.of().strength(8.0f, 7.0f).requiresCorrectToolForDrops()));

    public static final Block RESONANCE_FORGE = register("resonance_forge",
        new ResonanceForgeBlock(BlockBehaviour.Properties.of().strength(3.5f, 4.0f)));

    // ===== Structural / decorative =====
    public static final Block CRACKED_LAB_CONCRETE = register("cracked_lab_concrete",
        new Block(BlockBehaviour.Properties.of().strength(3.0f, 6.0f).requiresCorrectToolForDrops()));
    public static final Block LAB_FLOOR_GRATE = register("lab_floor_grate",
        new Block(BlockBehaviour.Properties.of().strength(2.0f, 5.0f).noOcclusion().sound(SoundType.METAL)));
    public static final Block OPUS_CONTAINMENT_GLASS = register("opus_containment_glass",
        new Block(BlockBehaviour.Properties.of().strength(0.8f).noOcclusion().sound(SoundType.GLASS).lightLevel(s -> 4)));
    public static final Block SCORCHED_CONCRETE = register("scorched_concrete",
        new Block(BlockBehaviour.Properties.of().strength(2.8f, 5.0f).requiresCorrectToolForDrops()));
    public static final Block OIL_STAIN = register("oil_stain",
        new Block(BlockBehaviour.Properties.of().strength(2.0f, 4.0f)));
    public static final Block FORTRESS_PLATING = register("fortress_plating",
        new Block(BlockBehaviour.Properties.of().strength(5.0f, 8.0f).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final Block MEMORY_GLASS = register("memory_glass",
        new Block(BlockBehaviour.Properties.of().strength(0.8f).noOcclusion().sound(SoundType.GLASS).lightLevel(s -> 6)));
    public static final Block OMEGA_FRAME = register("omega_frame",
        new Block(BlockBehaviour.Properties.of().strength(4.0f, 7.0f).sound(SoundType.METAL).lightLevel(s -> 7)));
    public static final Block CITADEL_VEIN = register("citadel_vein",
        new Block(BlockBehaviour.Properties.of().strength(2.5f, 5.0f).noOcclusion().lightLevel(s -> 7)));
    public static final Block TANK_TRAP = register("tank_trap",
        new Block(BlockBehaviour.Properties.of().strength(3.0f, 5.0f).sound(SoundType.METAL)));
    public static final Block DATA_CONDUIT = register("data_conduit",
        new OpusHorizontalBlock(BlockBehaviour.Properties.of().strength(3.0f, 6.0f).sound(SoundType.METAL)));
    public static final Block MEMORY_CABLE = register("memory_cable",
        new OpusHorizontalBlock(BlockBehaviour.Properties.of().strength(1.5f, 3.0f).noOcclusion().lightLevel(s -> 5)));
    public static final Block SIGNAL_PANEL = register("signal_panel",
        new OpusHorizontalBlock(BlockBehaviour.Properties.of().strength(2.5f, 5.0f).sound(SoundType.METAL).lightLevel(s -> 6)));
    public static final Block PULSING_CORE = register("pulsing_core",
        new PulsingCoreBlock(BlockBehaviour.Properties.of().strength(3.0f, 6.0f).lightLevel(s -> 9)));
    public static final Block BROKEN_EXO_HULL = register("broken_exo_hull",
        new Block(BlockBehaviour.Properties.of().strength(4.0f, 7.0f).sound(SoundType.METAL)));
    public static final Block EXO_ASSEMBLY_FRAME = register("exo_assembly_frame",
        new Block(BlockBehaviour.Properties.of().strength(3.5f, 6.0f).sound(SoundType.METAL)));
    public static final Block WELDING_BENCH = register("welding_bench",
        new OpusHorizontalBlock(BlockBehaviour.Properties.of().strength(2.5f, 5.0f).sound(SoundType.METAL)));
    public static final Block CORE_CRATE = register("core_crate",
        new Block(BlockBehaviour.Properties.of().strength(2.0f, 3.0f).sound(SoundType.WOOD)));
    public static final Block MEMORY_SLUDGE = register("memory_sludge",
        new Block(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.SLIME_BLOCK).friction(0.8f)));
    public static final Block HAZARD_EMITTER = register("hazard_emitter",
        new OpusHorizontalBlock(BlockBehaviour.Properties.of().strength(2.5f, 5.0f).sound(SoundType.METAL).lightLevel(s -> 7)));
    public static final Block MARSH_FILTER = register("marsh_filter",
        new Block(BlockBehaviour.Properties.of().strength(1.5f, 3.0f)));

    // ===== Oriented / facing =====
    public static final Block DEAD_TERMINAL = register("dead_terminal",
        new OpusHorizontalBlock(BlockBehaviour.Properties.of().strength(2.5f, 5.0f).sound(SoundType.METAL)));
    public static final Block FLICKERING_TERMINAL = register("flickering_terminal",
        new OpusHorizontalBlock(BlockBehaviour.Properties.of().strength(2.5f, 5.0f).noOcclusion().sound(SoundType.METAL).lightLevel(s -> 7)));
    public static final Block BLUEPRINT_TABLE = register("blueprint_table",
        new OpusHorizontalBlock(BlockBehaviour.Properties.of().strength(2.0f, 3.0f).sound(SoundType.WOOD)));
    public static final Block BROKEN_RESONANCE_FORGE = register("broken_resonance_forge",
        new OpusHorizontalBlock(BlockBehaviour.Properties.of().strength(3.5f, 6.0f)));
    public static final Block KATANA_STAND = register("katana_stand",
        new OpusHorizontalBlock(BlockBehaviour.Properties.of().strength(2.0f, 3.0f).sound(SoundType.WOOD)));
    public static final Block SCANNER_EYE = register("scanner_eye",
        new OpusHorizontalBlock(BlockBehaviour.Properties.of().strength(3.0f, 5.0f).sound(SoundType.METAL).lightLevel(s -> 6)));
    public static final Block PULSE_TURRET = register("pulse_turret",
        new OpusHorizontalBlock(BlockBehaviour.Properties.of().strength(3.0f, 6.0f).sound(SoundType.METAL).lightLevel(s -> 5)));
    public static final Block WAVE_TERMINAL = register("wave_terminal",
        new PoweredHorizontalBlock(BlockBehaviour.Properties.of().strength(2.5f, 5.0f).sound(SoundType.METAL)
            .lightLevel(s -> s.getValue(PoweredHorizontalBlock.POWERED) ? 8 : 4)));
    public static final Block COMMAND_TERMINAL = register("command_terminal",
        new PoweredHorizontalBlock(BlockBehaviour.Properties.of().strength(2.5f, 5.0f).noOcclusion().sound(SoundType.METAL)
            .lightLevel(s -> s.getValue(PoweredHorizontalBlock.POWERED) ? 8 : 4)));
    public static final Block MEMORY_CONSOLE = register("memory_console",
        new MemoryConsoleBlock(BlockBehaviour.Properties.of().strength(2.5f, 5.0f).noOcclusion().sound(SoundType.METAL).lightLevel(s -> 6)));

    // ===== Toggled =====
    public static final Block ARENA_GATE = register("arena_gate",
        new ToggleBlock.OpenToggleBlock(BlockBehaviour.Properties.of().strength(4.0f, 8.0f).sound(SoundType.METAL)));
    public static final Block PHASE_GATE = register("phase_gate",
        new ToggleBlock.ActiveToggleBlock(BlockBehaviour.Properties.of().strength(4.0f, 8.0f).sound(SoundType.METAL)
            .lightLevel(s -> s.getValue(ToggleBlock.ActiveToggleBlock.ACTIVE) ? 9 : 0)));
    public static final Block SEALED_BULKHEAD = register("sealed_bulkhead",
        new ToggleBlock.OpenToggleBlock(BlockBehaviour.Properties.of().strength(5.0f, 10.0f).sound(SoundType.METAL)));
    public static final Block SEALED_HATCH = register("sealed_hatch",
        new ToggleBlock.OpenToggleBlock(BlockBehaviour.Properties.of().strength(5.0f, 10.0f).sound(SoundType.METAL)));
    public static final Block SHIELD_NODE = register("shield_node",
        new ToggleBlock.ActiveToggleBlock(BlockBehaviour.Properties.of().strength(3.0f, 6.0f).noOcclusion().sound(SoundType.METAL)
            .lightLevel(s -> s.getValue(ToggleBlock.ActiveToggleBlock.ACTIVE) ? 8 : 0)));
    public static final Block GRAVITY_ANCHOR = register("gravity_anchor",
        new ToggleBlock.ActiveToggleBlock(BlockBehaviour.Properties.of().strength(3.0f, 6.0f).sound(SoundType.METAL)
            .lightLevel(s -> s.getValue(ToggleBlock.ActiveToggleBlock.ACTIVE) ? 6 : 0)));
    public static final Block COMBAT_BEACON = register("combat_beacon",
        new ToggleBlock.ActiveToggleBlock(BlockBehaviour.Properties.of().strength(3.0f, 6.0f).noOcclusion().sound(SoundType.METAL)
            .lightLevel(s -> s.getValue(ToggleBlock.ActiveToggleBlock.ACTIVE) ? 9 : 0)));

    // ===== Block entities =====
    public static final Block TRIAL_TRIGGER = register("trial_trigger",
        new TrialTriggerBlock(BlockBehaviour.Properties.of().strength(3.5f, 7.0f).noOcclusion()
            .lightLevel(s -> s.getValue(TrialTriggerBlock.TRIGGERED) ? 7 : 0)));
    public static final Block REWARD_VAULT = register("reward_vault",
        new RewardVaultBlock(BlockBehaviour.Properties.of().strength(4.0f, 8.0f).noOcclusion().sound(SoundType.METAL)
            .lightLevel(s -> s.getValue(RewardVaultBlock.OPEN) ? 8 : 0)));
    public static final Block DORMANT_SPAWNER = register("dormant_spawner",
        new DormantSpawnerBlock(BlockBehaviour.Properties.of().strength(4.0f, 8.0f).noOcclusion()
            .lightLevel(s -> s.getValue(DormantSpawnerBlock.ACTIVE) ? 9 : 0)));

    // ===== Eternal Colosseum blocks =====
    public static final Block REINFORCED_OPUS_BLOCK = register("reinforced_opus_block",
        new Block(BlockBehaviour.Properties.of().strength(8.0f, 10.0f).requiresCorrectToolForDrops().sound(SoundType.METAL)
            .lightLevel(s -> 4)));
    
    public static final Block HAIKU_AMBER_BLOCK = register("haiku_amber_block",
        new AmberGlowBlock(BlockBehaviour.Properties.of().strength(-1.0f, 3600000.0f).lightLevel(s -> 10)));
    
    public static final Block ALTAR_HEART = register("altar_heart",
        new AltarHeartBlock(BlockBehaviour.Properties.of().strength(-1.0f).noOcclusion().lightLevel(s -> s.getValue(AltarHeartBlock.ACTIVATED) ? 15 : 8)));
    
    public static final Block COLOSSEUM_CONCRETE = register("colosseum_concrete",
        new Block(BlockBehaviour.Properties.of().strength(3.5f, 6.0f).requiresCorrectToolForDrops()));
    
    public static final Block AMBER_PILLAR = register("amber_pillar",
        new RotatedPillarBlock(BlockBehaviour.Properties.of().strength(2.5f, 5.0f).lightLevel(s -> 8)));
    
    public static final Block COLOSSEUM_WALL = register("colosseum_wall",
        new Block(BlockBehaviour.Properties.of().strength(5.0f, 8.0f).requiresCorrectToolForDrops().sound(SoundType.METAL)));

    // ===== Энергетические барьеры (мерцающие панели) =====
    public static final Block ENERGY_BARRIER = register("energy_barrier",
        new EnergyBarrierBlock(BlockBehaviour.Properties.of().strength(-1.0f, 3600000.0f).noOcclusion().sound(SoundType.GLASS)
            .lightLevel(s -> 8)));
    public static final Block ENERGY_BARRIER_RED = register("energy_barrier_red",
        new EnergyBarrierBlock(BlockBehaviour.Properties.of().strength(-1.0f, 3600000.0f).noOcclusion().sound(SoundType.GLASS)
            .lightLevel(s -> 8)));
    public static final Block ENERGY_BARRIER_BLUE = register("energy_barrier_blue",
        new EnergyBarrierBlock(BlockBehaviour.Properties.of().strength(-1.0f, 3600000.0f).noOcclusion().sound(SoundType.GLASS)
            .lightLevel(s -> 8)));
    public static final Block ENERGY_BEAM = register("energy_beam",
        new EnergyBeamBlock(BlockBehaviour.Properties.of().strength(-1.0f, 3600000.0f).noOcclusion().sound(SoundType.GLASS)
            .lightLevel(s -> 8)));
    public static final Block FORCE_FIELD_PROJECTOR = register("force_field_projector",
        new ForceFieldProjectorBlock(BlockBehaviour.Properties.of().strength(3.5f, 8.0f).noOcclusion().sound(SoundType.METAL)
            .lightLevel(s -> s.getValue(PoweredHorizontalBlock.POWERED) ? 10 : 3)));
    public static final Block PHASED_BARRIER = register("phased_barrier",
        new PhasedBarrierBlock(BlockBehaviour.Properties.of().strength(-1.0f, 3600000.0f).noOcclusion().sound(SoundType.GLASS)
            .lightLevel(s -> s.getValue(PhasedBarrierBlock.ACTIVE) ? 8 : 0)));

    // ===== Пазл-блоки структур (задача 19) =====
    public static final Block SEQUENCE_KEYPAD = register("sequence_keypad",
        new SequenceKeypadBlock(BlockBehaviour.Properties.of().strength(3.0f, 6.0f).sound(SoundType.METAL)
            .lightLevel(s -> s.getValue(SequenceKeypadBlock.SOLVED) ? 11 : 3)));

    public static final Block ENERGY_RELAY = register("energy_relay",
        new EnergyRelayBlock(BlockBehaviour.Properties.of().strength(3.0f, 6.0f).sound(SoundType.METAL)
            .lightLevel(s -> s.getValue(EnergyRelayBlock.POWERED) ? 9 : 1)));

    private static Block register(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, OpusVsExe.id(name), block);
    }

    public static void init() {
    }
}
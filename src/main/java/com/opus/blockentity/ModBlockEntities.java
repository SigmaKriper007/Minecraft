package com.opus.blockentity;

import com.opus.OpusVsExe;
import com.opus.block.DormantSpawnerBlock;
import com.opus.block.MemoryConsoleBlock;
import com.opus.block.ResonanceForgeBlock;
import com.opus.block.RewardVaultBlock;
import com.opus.block.SequenceKeypadBlock;
import com.opus.block.TrialTriggerBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static final BlockEntityType<TrialTriggerBlockEntity> TRIAL_TRIGGER = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE, OpusVsExe.id("trial_trigger"),
        BlockEntityType.Builder.of(TrialTriggerBlockEntity::new, com.opus.registry.ModBlocks.TRIAL_TRIGGER).build(null));

    public static final BlockEntityType<RewardVaultBlockEntity> REWARD_VAULT = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE, OpusVsExe.id("reward_vault"),
        BlockEntityType.Builder.of(RewardVaultBlockEntity::new, com.opus.registry.ModBlocks.REWARD_VAULT).build(null));

    public static final BlockEntityType<MemoryConsoleBlockEntity> MEMORY_CONSOLE = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE, OpusVsExe.id("memory_console"),
        BlockEntityType.Builder.of(MemoryConsoleBlockEntity::new, com.opus.registry.ModBlocks.MEMORY_CONSOLE).build(null));

    public static final BlockEntityType<DormantSpawnerBlockEntity> DORMANT_SPAWNER = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE, OpusVsExe.id("dormant_spawner"),
        BlockEntityType.Builder.of(DormantSpawnerBlockEntity::new, com.opus.registry.ModBlocks.DORMANT_SPAWNER).build(null));

    public static final BlockEntityType<ResonanceForgeBlockEntity> RESONANCE_FORGE = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE, OpusVsExe.id("resonance_forge"),
        BlockEntityType.Builder.of(ResonanceForgeBlockEntity::new, com.opus.registry.ModBlocks.RESONANCE_FORGE).build(null));

    public static final BlockEntityType<AltarHeartBlockEntity> ALTAR_HEART = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE, OpusVsExe.id("altar_heart"),
        BlockEntityType.Builder.of(AltarHeartBlockEntity::new, com.opus.registry.ModBlocks.ALTAR_HEART).build(null));

    public static final BlockEntityType<SequenceKeypadBlockEntity> SEQUENCE_KEYPAD = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE, OpusVsExe.id("sequence_keypad"),
        BlockEntityType.Builder.of(SequenceKeypadBlockEntity::new, com.opus.registry.ModBlocks.SEQUENCE_KEYPAD).build(null));

    public static void init() {
    }
}
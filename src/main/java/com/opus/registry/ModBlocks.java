package com.opus.registry;

import com.opus.OpusVsExe;
import com.opus.block.ResonanceForgeBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, OpusVsExe.MOD_ID);
    
    public static final RegistryObject<Block> OPUS_ORE = BLOCKS.register("opus_ore", 
        () -> new Block(BlockBehaviour.Properties.of().strength(4.0f, 3.0f).requiresCorrectToolForDrops()));
    
    public static final RegistryObject<Block> RAW_OPUS_BLOCK = BLOCKS.register("raw_opus_block",
        () -> new Block(BlockBehaviour.Properties.of().strength(5.0f, 4.0f).requiresCorrectToolForDrops()));
    
    public static final RegistryObject<Block> STABILIZED_OPUS_BLOCK = BLOCKS.register("stabilized_opus_block",
        () -> new Block(BlockBehaviour.Properties.of().strength(6.0f, 5.0f).requiresCorrectToolForDrops()));
    
    public static final RegistryObject<Block> RESONANT_OPUS_BLOCK = BLOCKS.register("resonant_opus_block",
        () -> new Block(BlockBehaviour.Properties.of().strength(7.0f, 6.0f).requiresCorrectToolForDrops()));
    
    public static final RegistryObject<Block> CORE_OPUS_BLOCK = BLOCKS.register("core_opus_block",
        () -> new Block(BlockBehaviour.Properties.of().strength(8.0f, 7.0f).requiresCorrectToolForDrops()));
    
    public static final RegistryObject<Block> RESONANCE_FORGE = BLOCKS.register("resonance_forge",
        () -> new ResonanceForgeBlock(BlockBehaviour.Properties.of().strength(3.5f, 4.0f)));
    
    public static void registerBlockItems(DeferredRegister<Item> items) {
        items.register("opus_ore", () -> new BlockItem(OPUS_ORE.get(), new Item.Properties()));
        items.register("raw_opus_block", () -> new BlockItem(RAW_OPUS_BLOCK.get(), new Item.Properties()));
        items.register("stabilized_opus_block", () -> new BlockItem(STABILIZED_OPUS_BLOCK.get(), new Item.Properties()));
        items.register("resonant_opus_block", () -> new BlockItem(RESONANT_OPUS_BLOCK.get(), new Item.Properties()));
        items.register("core_opus_block", () -> new BlockItem(CORE_OPUS_BLOCK.get(), new Item.Properties()));
        items.register("resonance_forge", () -> new BlockItem(RESONANCE_FORGE.get(), new Item.Properties()));
    }
}

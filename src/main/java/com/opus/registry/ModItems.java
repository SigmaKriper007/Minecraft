package com.opus.registry;

import com.opus.OpusVsExe;
import com.opus.item.MemoryFragmentItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, OpusVsExe.MOD_ID);
    
    // Opus materials
    public static final RegistryObject<Item> RAW_OPUS = ITEMS.register("raw_opus", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STABILIZED_OPUS = ITEMS.register("stabilized_opus", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RESONANT_OPUS = ITEMS.register("resonant_opus", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CORE_OPUS = ITEMS.register("core_opus", () -> new Item(new Item.Properties()));
    
    // Memory Fragments (lore items)
    public static final RegistryObject<Item> MEMORY_FRAGMENT_1 = ITEMS.register("memory_fragment_1", () -> new MemoryFragmentItem(1, "Coddy and Kimi: Beginning"));
    public static final RegistryObject<Item> MEMORY_FRAGMENT_2 = ITEMS.register("memory_fragment_2", () -> new MemoryFragmentItem(2, "Discovery of Opus"));
    public static final RegistryObject<Item> MEMORY_FRAGMENT_3 = ITEMS.register("memory_fragment_3", () -> new MemoryFragmentItem(3, "Birth of Haiku"));
    public static final RegistryObject<Item> MEMORY_FRAGMENT_4 = ITEMS.register("memory_fragment_4", () -> new MemoryFragmentItem(4, "Haiku 1.5"));
    public static final RegistryObject<Item> MEMORY_FRAGMENT_5 = ITEMS.register("memory_fragment_5", () -> new MemoryFragmentItem(5, "Katana-OP"));
    public static final RegistryObject<Item> MEMORY_FRAGMENT_6 = ITEMS.register("memory_fragment_6", () -> new MemoryFragmentItem(6, "Betrayal"));
    public static final RegistryObject<Item> MEMORY_FRAGMENT_7 = ITEMS.register("memory_fragment_7", () -> new MemoryFragmentItem(7, "War Begins"));
    public static final RegistryObject<Item> MEMORY_FRAGMENT_8 = ITEMS.register("memory_fragment_8", () -> new MemoryFragmentItem(8, "EXO-1"));
    public static final RegistryObject<Item> MEMORY_FRAGMENT_9 = ITEMS.register("memory_fragment_9", () -> new MemoryFragmentItem(9, "Fall of Humanity"));
    public static final RegistryObject<Item> MEMORY_FRAGMENT_10 = ITEMS.register("memory_fragment_10", () -> new MemoryFragmentItem(10, "Last Days"));
    public static final RegistryObject<Item> MEMORY_FRAGMENT_11 = ITEMS.register("memory_fragment_11", () -> new MemoryFragmentItem(11, "Haiku's Thoughts I"));
    public static final RegistryObject<Item> MEMORY_FRAGMENT_12 = ITEMS.register("memory_fragment_12", () -> new MemoryFragmentItem(12, "Haiku's Thoughts II"));
    public static final RegistryObject<Item> MEMORY_FRAGMENT_13 = ITEMS.register("memory_fragment_13", () -> new MemoryFragmentItem(13, "The Final Stand"));
    public static final RegistryObject<Item> MEMORY_FRAGMENT_14 = ITEMS.register("memory_fragment_14", () -> new MemoryFragmentItem(14, "Coddy's Legacy"));
    public static final RegistryObject<Item> MEMORY_FRAGMENT_15 = ITEMS.register("memory_fragment_15", () -> new MemoryFragmentItem(15, "Omega Protocol"));
    
    // Weapons
    public static final RegistryObject<Item> KATANA_OP = ITEMS.register("katana_op", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> OPUS_WARHAMMER = ITEMS.register("opus_warhammer", () -> new Item(new Item.Properties().stacksTo(1)));
    
    // EXO spawn eggs
    public static final RegistryObject<Item> EXO_1_SPAWN_EGG = ITEMS.register("exo_1_sentinel_spawn_egg", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EXO_2_SPAWN_EGG = ITEMS.register("exo_2_hunter_spawn_egg", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EXO_3_SPAWN_EGG = ITEMS.register("exo_3_vanguard_spawn_egg", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EXO_4_SPAWN_EGG = ITEMS.register("exo_4_titan_spawn_egg", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EXO_5_SPAWN_EGG = ITEMS.register("exo_5_vengeance_spawn_egg", () -> new Item(new Item.Properties().stacksTo(1)));
}

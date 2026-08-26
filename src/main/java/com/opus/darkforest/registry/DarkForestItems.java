package com.opus.darkforest.registry;

import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.item.DarkForestArmorItem;
import com.opus.darkforest.item.DarkForestTools;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;

import java.util.List;

public final class DarkForestItems {
    public static final Item MOONLIT_SOIL=blockItem("moonlit_soil",DarkForestBlocks.MOONLIT_SOIL);
    public static final Item MOONLIT_GRASS=blockItem("moonlit_grass",DarkForestBlocks.MOONLIT_GRASS);
    public static final Item GLOOMWOOD_LOG=blockItem("gloomwood_log",DarkForestBlocks.GLOOMWOOD_LOG);
    public static final Item GLOOMWOOD_LEAVES=blockItem("gloomwood_leaves",DarkForestBlocks.GLOOMWOOD_LEAVES);
    public static final Item GLOOMWOOD_SAPLING=blockItem("gloomwood_sapling",DarkForestBlocks.GLOOMWOOD_SAPLING);
    public static final Item MOONFLOWER=blockItem("moonflower",DarkForestBlocks.MOONFLOWER);
    public static final Item THORN_FERN=blockItem("thorn_fern",DarkForestBlocks.THORN_FERN);
    public static final Item FOUNTAIN_STONE=blockItem("fountain_stone",DarkForestBlocks.FOUNTAIN_STONE);
    public static final Item MOON_FOUNTAIN_CORE=blockItem("moon_fountain_core",DarkForestBlocks.MOON_FOUNTAIN_CORE);
    public static final Item ROOTBOUND_PEDESTAL=blockItem("rootbound_pedestal",DarkForestBlocks.ROOTBOUND_PEDESTAL);
    public static final Item SHADE_SILK=register("shade_silk",new Item(new Item.Properties()));
    public static final Item MOONWING_MEMBRANE=register("moonwing_membrane",new Item(new Item.Properties()));
    public static final Item MOONFLOWER_HEART=register("moonflower_heart",new Item(new Item.Properties().stacksTo(1).rarity(net.minecraft.world.item.Rarity.EPIC)));
    public static final Item ROOTBOUND_EYE=register("rootbound_eye",new Item(new Item.Properties().stacksTo(16).rarity(net.minecraft.world.item.Rarity.RARE)));
    public static final Item BRIARWEAVE=register("briarweave",new Item(new Item.Properties()));
    public static final Item BRIARWEAVE_HELMET=armor("briarweave_helmet",net.minecraft.world.item.ArmorItem.Type.HELMET,false);
    public static final Item BRIARWEAVE_CHESTPLATE=armor("briarweave_chestplate",net.minecraft.world.item.ArmorItem.Type.CHESTPLATE,false);
    public static final Item BRIARWEAVE_LEGGINGS=armor("briarweave_leggings",net.minecraft.world.item.ArmorItem.Type.LEGGINGS,false);
    public static final Item BRIARWEAVE_BOOTS=armor("briarweave_boots",net.minecraft.world.item.ArmorItem.Type.BOOTS,false);
    public static final Item DARK_FOREST_HELMET=armor("dark_forest_helmet",net.minecraft.world.item.ArmorItem.Type.HELMET,true);
    public static final Item DARK_FOREST_CHESTPLATE=armor("dark_forest_chestplate",net.minecraft.world.item.ArmorItem.Type.CHESTPLATE,true);
    public static final Item DARK_FOREST_LEGGINGS=armor("dark_forest_leggings",net.minecraft.world.item.ArmorItem.Type.LEGGINGS,true);
    public static final Item DARK_FOREST_BOOTS=armor("dark_forest_boots",net.minecraft.world.item.ArmorItem.Type.BOOTS,true);
    public static final Item DARK_FOREST_SWORD=register("dark_forest_sword",new DarkForestTools.Sword(new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));
    public static final Item DARK_FOREST_PICKAXE=register("dark_forest_pickaxe",new DarkForestTools.Pickaxe(new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));
    public static final Item DARK_FOREST_AXE=register("dark_forest_axe",new DarkForestTools.Axe(new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));
    public static final Item DARK_FOREST_SHOVEL=register("dark_forest_shovel",new DarkForestTools.Shovel(new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));
    public static final Item DARK_FOREST_HOE=register("dark_forest_hoe",new DarkForestTools.Hoe(new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));
    public static final Item SHADE_SPIDERLING_SPAWN_EGG=register("shade_spiderling_spawn_egg",new SpawnEggItem(DarkForestEntities.SHADE_SPIDERLING,0x17121F,0x64DDE1,new Item.Properties()));
    public static final Item GLOOM_BROODMOTHER_SPAWN_EGG=register("gloom_broodmother_spawn_egg",new SpawnEggItem(DarkForestEntities.GLOOM_BROODMOTHER,0x24182E,0x627538,new Item.Properties()));
    public static final Item MOONWING_BAT_SPAWN_EGG=register("moonwing_bat_spawn_egg",new SpawnEggItem(DarkForestEntities.MOONWING_BAT,0x1C1728,0x75E9F0,new Item.Properties()));
    public static final Item MOSSBOUND_ENDERMAN_SPAWN_EGG=register("mossbound_enderman_spawn_egg",new SpawnEggItem(DarkForestEntities.MOSSBOUND_ENDERMAN,0x17121F,0xA8EDE9,new Item.Properties()));
    private static final List<Item> ALL=List.of(MOONLIT_SOIL,MOONLIT_GRASS,GLOOMWOOD_LOG,GLOOMWOOD_LEAVES,GLOOMWOOD_SAPLING,MOONFLOWER,THORN_FERN,FOUNTAIN_STONE,MOON_FOUNTAIN_CORE,ROOTBOUND_PEDESTAL,SHADE_SILK,MOONWING_MEMBRANE,MOONFLOWER_HEART,ROOTBOUND_EYE,BRIARWEAVE,BRIARWEAVE_HELMET,BRIARWEAVE_CHESTPLATE,BRIARWEAVE_LEGGINGS,BRIARWEAVE_BOOTS,DARK_FOREST_HELMET,DARK_FOREST_CHESTPLATE,DARK_FOREST_LEGGINGS,DARK_FOREST_BOOTS,DARK_FOREST_SWORD,DARK_FOREST_PICKAXE,DARK_FOREST_AXE,DARK_FOREST_SHOVEL,DARK_FOREST_HOE,SHADE_SPIDERLING_SPAWN_EGG,GLOOM_BROODMOTHER_SPAWN_EGG,MOONWING_BAT_SPAWN_EGG,MOSSBOUND_ENDERMAN_SPAWN_EGG);
    private DarkForestItems(){ }
    private static Item blockItem(String id,Block block){return Registry.register(BuiltInRegistries.ITEM,DarkForestLine.id(id),new BlockItem(block,new Item.Properties()));}
    private static Item register(String id,Item item){return Registry.register(BuiltInRegistries.ITEM,DarkForestLine.id(id),item);}
    private static Item armor(String id,net.minecraft.world.item.ArmorItem.Type type,boolean vestments){return register(id,new DarkForestArmorItem(type,vestments,new Item.Properties().rarity(vestments?net.minecraft.world.item.Rarity.EPIC:net.minecraft.world.item.Rarity.UNCOMMON)));}
    public static List<Item> all(){return ALL;}
    public static void init(){ }
}

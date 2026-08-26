package com.opus.paradise.registry;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.item.AerieArmorItem;
import com.opus.paradise.item.ParthenonArmorItem;
import com.opus.paradise.item.ParthenonTools;
import com.opus.paradise.item.RubyHaloShardItem;
import com.opus.paradise.item.SeraphicPinionsItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.Block;

import java.util.List;

public final class ParadiseItems {
    public static final Item CELESTIAL_STONE = blockItem("celestial_stone", ParadiseBlocks.CELESTIAL_STONE);
    public static final Item PARADISE_SOIL = blockItem("paradise_soil", ParadiseBlocks.PARADISE_SOIL);
    public static final Item PARADISE_GRASS = blockItem("paradise_grass", ParadiseBlocks.PARADISE_GRASS);
    public static final Item PARADISE_LOG = blockItem("paradise_log", ParadiseBlocks.PARADISE_LOG);
    public static final Item PARADISE_LEAVES = blockItem("paradise_leaves", ParadiseBlocks.PARADISE_LEAVES);
    public static final Item PARADISE_SAPLING = blockItem("paradise_sapling", ParadiseBlocks.PARADISE_SAPLING);
    public static final Item PARADISE_FRUIT = register("paradise_fruit", new Item(new Item.Properties().food(
        new FoodProperties.Builder().nutrition(5).saturationMod(0.6F).alwaysEat().build())));
    public static final Item SUNFINCH_SPAWN_EGG = register("sunfinch_spawn_egg",
        new SpawnEggItem(ParadiseEntities.SUNFINCH, 0x2C8D72, 0xFFD66B, new Item.Properties()));
    public static final Item CLOUD_GRAZER_SPAWN_EGG = register("cloud_grazer_spawn_egg",
        new SpawnEggItem(ParadiseEntities.CLOUD_GRAZER, 0xF4E7C7, 0xD8A83E, new Item.Properties()));
    public static final Item PARADISE_WYVERN_SPAWN_EGG = register("paradise_wyvern_spawn_egg",
        new SpawnEggItem(ParadiseEntities.PARADISE_WYVERN, 0x176B62, 0xE9C65C, new Item.Properties()));
    public static final Item PARTHENON_MARBLE = blockItem("parthenon_marble", ParadiseBlocks.PARTHENON_MARBLE);
    public static final Item GILDED_MARBLE = blockItem("gilded_marble", ParadiseBlocks.GILDED_MARBLE);
    public static final Item ANGEL_DAIS = blockItem("angel_dais", ParadiseBlocks.ANGEL_DAIS);
    public static final Item PARTHENON_FORGE = blockItem("parthenon_forge", ParadiseBlocks.PARTHENON_FORGE);
    public static final Item SUNFEATHER = register("sunfeather",new Item(new Item.Properties()));
    public static final Item CLOUD_FLEECE = register("cloud_fleece",new Item(new Item.Properties()));
    public static final Item AERIE_BRONZE_INGOT = register("aerie_bronze_ingot",new Item(new Item.Properties()));
    public static final Item AERIE_BRONZE_HELMET = register("aerie_bronze_helmet",new AerieArmorItem(net.minecraft.world.item.ArmorItem.Type.HELMET,new Item.Properties()));
    public static final Item AERIE_BRONZE_CHESTPLATE = register("aerie_bronze_chestplate",new AerieArmorItem(net.minecraft.world.item.ArmorItem.Type.CHESTPLATE,new Item.Properties()));
    public static final Item AERIE_BRONZE_LEGGINGS = register("aerie_bronze_leggings",new AerieArmorItem(net.minecraft.world.item.ArmorItem.Type.LEGGINGS,new Item.Properties()));
    public static final Item AERIE_BRONZE_BOOTS = register("aerie_bronze_boots",new AerieArmorItem(net.minecraft.world.item.ArmorItem.Type.BOOTS,new Item.Properties()));
    public static final Item SERAPHIC_PINIONS = register("seraphic_pinions", new SeraphicPinionsItem(new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE).fireResistant()));
    public static final Item RUBY_HALO_SHARD = register("ruby_halo_shard", new RubyHaloShardItem(new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC).fireResistant()));
    public static final Item PARTHENON_HELMET = register("parthenon_helmet",new ParthenonArmorItem(net.minecraft.world.item.ArmorItem.Type.HELMET,new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC)));
    public static final Item PARTHENON_CHESTPLATE = register("parthenon_chestplate",new ParthenonArmorItem(net.minecraft.world.item.ArmorItem.Type.CHESTPLATE,new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC)));
    public static final Item PARTHENON_LEGGINGS = register("parthenon_leggings",new ParthenonArmorItem(net.minecraft.world.item.ArmorItem.Type.LEGGINGS,new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC)));
    public static final Item PARTHENON_BOOTS = register("parthenon_boots",new ParthenonArmorItem(net.minecraft.world.item.ArmorItem.Type.BOOTS,new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC)));
    public static final Item PARTHENON_SWORD = register("parthenon_sword",new ParthenonTools.Sword(new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC)));
    public static final Item PARTHENON_PICKAXE = register("parthenon_pickaxe",new ParthenonTools.Pickaxe(new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC)));
    public static final Item PARTHENON_AXE = register("parthenon_axe",new ParthenonTools.Axe(new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC)));
    public static final Item PARTHENON_SHOVEL = register("parthenon_shovel",new ParthenonTools.Shovel(new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC)));
    public static final Item PARTHENON_HOE = register("parthenon_hoe",new ParthenonTools.Hoe(new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC)));
    public static final Item ANGEL_BOY_SPAWN_EGG = register("angel_boy_spawn_egg",
        new SpawnEggItem(ParadiseEntities.ANGEL_BOY,0xE8D5B7,0xC02E3A,new Item.Properties()));

    private static final List<Item> ALL = List.of(CELESTIAL_STONE, PARADISE_SOIL, PARADISE_GRASS,
        PARADISE_LOG, PARADISE_LEAVES, PARADISE_SAPLING, PARADISE_FRUIT,
        SUNFINCH_SPAWN_EGG, CLOUD_GRAZER_SPAWN_EGG, PARADISE_WYVERN_SPAWN_EGG,
        PARTHENON_MARBLE, GILDED_MARBLE, ANGEL_DAIS, PARTHENON_FORGE, SUNFEATHER, CLOUD_FLEECE,
        AERIE_BRONZE_INGOT,AERIE_BRONZE_HELMET,AERIE_BRONZE_CHESTPLATE,AERIE_BRONZE_LEGGINGS,AERIE_BRONZE_BOOTS,
        SERAPHIC_PINIONS, RUBY_HALO_SHARD,PARTHENON_HELMET,PARTHENON_CHESTPLATE,PARTHENON_LEGGINGS,PARTHENON_BOOTS,
        PARTHENON_SWORD,PARTHENON_PICKAXE,PARTHENON_AXE,PARTHENON_SHOVEL,PARTHENON_HOE, ANGEL_BOY_SPAWN_EGG);

    private ParadiseItems() { }

    private static Item blockItem(String id, Block block) {
        return Registry.register(BuiltInRegistries.ITEM, ParadiseLine.id(id),
            new BlockItem(block, new Item.Properties()));
    }

    private static Item register(String id, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, ParadiseLine.id(id), item);
    }

    public static List<Item> all() { return ALL; }
    public static void init() { }
}

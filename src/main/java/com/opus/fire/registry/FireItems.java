package com.opus.fire.registry;

import com.opus.fire.FireLine;
import com.opus.fire.item.DemonicTridentItem;
import com.opus.fire.item.FireArmorItem;
import com.opus.fire.item.FireArmorMaterial;
import com.opus.fire.item.FirePickaxeItem;
import com.opus.fire.item.FireSwordItem;
import com.opus.fire.item.FireToolTier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;

public class FireItems {
    // Materials
    public static final Item FIRE_ESSENCE = register("fire_essence", new Item(fireProperties()));

    // Fire bean pod — block item (plantable on ember logs) + edible
    public static final Item FIRE_BEAN = register("fire_bean",
        new BlockItem(FireBlocks.FIRE_BEAN, fireProperties().food(new net.minecraft.world.food.FoodProperties.Builder()
            .nutrition(2).saturationMod(0.3f).build())));
    public static final Item EMBER_SAPLING = blockItem("ember_sapling", FireBlocks.EMBER_SAPLING);

    // Demonic Trident — throwable boss weapon (3D model)
    public static final Item DEMONIC_TRIDENT = register("demonic_trident",
        new DemonicTridentItem(fireProperties().stacksTo(1).durability(640)));

    // Fire tools — diamond harvest level, repaired with Fire Essence.
    public static final Item FIRE_SWORD = register("fire_sword", new FireSwordItem(fireProperties()));
    public static final Item FIRE_PICKAXE = register("fire_pickaxe", new FirePickaxeItem(fireProperties()));
    public static final Item FIRE_AXE = register("fire_axe",
        new AxeItem(FireToolTier.INSTANCE, 5.0F, -3.0F, fireProperties()));
    public static final Item FIRE_SHOVEL = register("fire_shovel",
        new ShovelItem(FireToolTier.INSTANCE, 1.5F, -3.0F, fireProperties()));
    public static final Item FIRE_HOE = register("fire_hoe",
        new HoeItem(FireToolTier.INSTANCE, -3, 0.0F, fireProperties()));

    // Fire Armor set — 3D models (LayerDefinition)
    public static final Item FIRE_HELMET = register("fire_helmet",
        new FireArmorItem(FireArmorMaterial.INSTANCE, ArmorItem.Type.HELMET, fireProperties()));
    public static final Item FIRE_CHESTPLATE = register("fire_chestplate",
        new FireArmorItem(FireArmorMaterial.INSTANCE, ArmorItem.Type.CHESTPLATE, fireProperties()));
    public static final Item FIRE_LEGGINGS = register("fire_leggings",
        new FireArmorItem(FireArmorMaterial.INSTANCE, ArmorItem.Type.LEGGINGS, fireProperties()));
    public static final Item FIRE_BOOTS = register("fire_boots",
        new FireArmorItem(FireArmorMaterial.INSTANCE, ArmorItem.Type.BOOTS, fireProperties()));

    // Spawn eggs
    public static final Item FIRE_SLIME_SPAWN_EGG = register("fire_slime_spawn_egg",
        new SpawnEggItem(FireEntities.FIRE_SLIME, 0x9E2A14, 0xFF6A00, fireProperties()));
    public static final Item LAVA_GOLEM_SPAWN_EGG = register("lava_golem_spawn_egg",
        new SpawnEggItem(FireEntities.LAVA_GOLEM, 0x2E2420, 0xFF8C1A, fireProperties()));
    public static final Item FIRE_DEMON_SPAWN_EGG = register("fire_demon_spawn_egg",
        new SpawnEggItem(FireEntities.FIRE_DEMON, 0x5E1408, 0xFF5A00, fireProperties()));

    // Block items
    public static final Item FIRE_SOIL = blockItem("fire_soil", FireBlocks.FIRE_SOIL);
    public static final Item MAGMA_CRUST = blockItem("magma_crust", FireBlocks.MAGMA_CRUST);
    public static final Item ASH_BLOCK = blockItem("ash_block", FireBlocks.ASH_BLOCK);
    public static final Item EMBER_LOG = blockItem("ember_log", FireBlocks.EMBER_LOG);
    public static final Item EMBER_LEAVES = blockItem("ember_leaves", FireBlocks.EMBER_LEAVES);
    public static final Item FIRE_VINE = blockItem("fire_vine", FireBlocks.FIRE_VINE);
    public static final Item CRIMSON_ICE = blockItem("crimson_ice", FireBlocks.CRIMSON_ICE);
    public static final Item FIRE_PORTAL = blockItem("fire_portal", FireBlocks.FIRE_PORTAL);

    private static Item register(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, FireLine.id(name), item);
    }

    private static Item blockItem(String name, Block block) {
        return register(name, new BlockItem(block, fireProperties()));
    }

    private static Item.Properties fireProperties() { return new Item.Properties().fireResistant(); }

    public static void init() {}
}

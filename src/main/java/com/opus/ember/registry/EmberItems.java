package com.opus.ember.registry;

import com.opus.ember.EmberLine;
import com.opus.ember.item.EmberArmorItem;
import com.opus.ember.item.EmberArmorMaterial;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

public class EmberItems {
    // The Ember Depths item line is retired. These four stable armor IDs are retained
    // as the high-tier Four Veins set for save compatibility and progression.
    public static final Item EMBER_HELMET = register("ember_helmet",
        new EmberArmorItem(EmberArmorMaterial.INSTANCE, ArmorItem.Type.HELMET, fireProperties()));
    public static final Item EMBER_CHESTPLATE = register("ember_chestplate",
        new EmberArmorItem(EmberArmorMaterial.INSTANCE, ArmorItem.Type.CHESTPLATE, fireProperties()));
    public static final Item EMBER_LEGGINGS = register("ember_leggings",
        new EmberArmorItem(EmberArmorMaterial.INSTANCE, ArmorItem.Type.LEGGINGS, fireProperties()));
    public static final Item EMBER_BOOTS = register("ember_boots",
        new EmberArmorItem(EmberArmorMaterial.INSTANCE, ArmorItem.Type.BOOTS, fireProperties()));

    private static Item register(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, EmberLine.id(name), item);
    }

    private static Item.Properties fireProperties() { return new Item.Properties().fireResistant(); }

    public static void init() {}
}

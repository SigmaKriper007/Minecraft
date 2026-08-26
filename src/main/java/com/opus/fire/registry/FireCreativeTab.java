package com.opus.fire.registry;

import com.opus.fire.FireLine;
import com.opus.ember.registry.EmberItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class FireCreativeTab {
    public static final ResourceKey<CreativeModeTab> FIRE_TAB_KEY = ResourceKey.create(
        Registries.CREATIVE_MODE_TAB, FireLine.id("fire_tab"));

    public static final CreativeModeTab FIRE_TAB = Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB, FireLine.id("fire_tab"),
        FabricItemGroup.builder()
            .title(Component.translatable("itemGroup.opusvsexe.fire_tab"))
            .icon(() -> new ItemStack(FireItems.FIRE_ESSENCE))
            .build());

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(FIRE_TAB_KEY).register(entries -> {
            // Blocks
            entries.accept(FireItems.FIRE_SOIL);
            entries.accept(FireItems.MAGMA_CRUST);
            entries.accept(FireItems.ASH_BLOCK);
            entries.accept(FireItems.EMBER_LOG);
            entries.accept(FireItems.EMBER_LEAVES);
            entries.accept(FireItems.EMBER_SAPLING);
            entries.accept(FireItems.FIRE_VINE);
            entries.accept(FireItems.FIRE_BEAN);
            entries.accept(FireItems.CRIMSON_ICE);
            entries.accept(FireItems.FIRE_PORTAL);
            // Items
            entries.accept(FireItems.FIRE_ESSENCE);
            entries.accept(FireItems.FIRE_SWORD);
            entries.accept(FireItems.FIRE_PICKAXE);
            entries.accept(FireItems.FIRE_AXE);
            entries.accept(FireItems.FIRE_SHOVEL);
            entries.accept(FireItems.FIRE_HOE);
            // Vein Crust baseline armor
            entries.accept(FireItems.FIRE_HELMET);
            entries.accept(FireItems.FIRE_CHESTPLATE);
            entries.accept(FireItems.FIRE_LEGGINGS);
            entries.accept(FireItems.FIRE_BOOTS);
            // Retained Ember armor — high-tier Four Veins upgrade
            entries.accept(EmberItems.EMBER_HELMET);
            entries.accept(EmberItems.EMBER_CHESTPLATE);
            entries.accept(EmberItems.EMBER_LEGGINGS);
            entries.accept(EmberItems.EMBER_BOOTS);
            // Weapon
            entries.accept(FireItems.DEMONIC_TRIDENT);
            // Spawn eggs
            entries.accept(FireItems.FIRE_SLIME_SPAWN_EGG);
            entries.accept(FireItems.LAVA_GOLEM_SPAWN_EGG);
            entries.accept(FireItems.FIRE_DEMON_SPAWN_EGG);
        });
    }
}

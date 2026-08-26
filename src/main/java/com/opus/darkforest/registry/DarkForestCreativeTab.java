package com.opus.darkforest.registry;

import com.opus.darkforest.DarkForestLine;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class DarkForestCreativeTab {
    public static final ResourceKey<CreativeModeTab> KEY=ResourceKey.create(Registries.CREATIVE_MODE_TAB,DarkForestLine.id("dark_forest_tab"));
    public static final CreativeModeTab TAB=Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,DarkForestLine.id("dark_forest_tab"),FabricItemGroup.builder().title(Component.translatable("itemGroup.opusvsexe.dark_forest_tab")).icon(()->new ItemStack(DarkForestItems.MOON_FOUNTAIN_CORE)).build());
    private DarkForestCreativeTab(){ }
    public static void init(){ItemGroupEvents.modifyEntriesEvent(KEY).register(entries->DarkForestItems.all().forEach(entries::accept));}
}

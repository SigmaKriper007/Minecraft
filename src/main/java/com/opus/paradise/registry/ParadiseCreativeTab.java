package com.opus.paradise.registry;

import com.opus.paradise.ParadiseLine;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ParadiseCreativeTab {
    public static final ResourceKey<CreativeModeTab> KEY = ResourceKey.create(
        Registries.CREATIVE_MODE_TAB, ParadiseLine.id("paradise_tab"));
    public static final CreativeModeTab TAB = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
        ParadiseLine.id("paradise_tab"), FabricItemGroup.builder()
            .title(Component.translatable("itemGroup.opusvsexe.paradise_tab"))
            .icon(() -> new ItemStack(ParadiseItems.GILDED_MARBLE))
            .build());

    private ParadiseCreativeTab() { }

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(KEY).register(entries -> ParadiseItems.all().forEach(entries::accept));
    }
}

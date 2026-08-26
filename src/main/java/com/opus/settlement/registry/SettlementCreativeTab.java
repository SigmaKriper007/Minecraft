package com.opus.settlement.registry;

import com.opus.settlement.SettlementLine;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class SettlementCreativeTab {
    public static final ResourceKey<CreativeModeTab> KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, SettlementLine.id("settlement_tab"));
    public static final CreativeModeTab TAB = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, SettlementLine.id("settlement_tab"),
        FabricItemGroup.builder().title(Component.translatable("itemGroup.opusvsexe.settlement_tab"))
            .icon(() -> new ItemStack(SettlementItems.OPUS_RUINS_COMPASS)).build());

    private SettlementCreativeTab() { }
    public static void init() { ItemGroupEvents.modifyEntriesEvent(KEY).register(entries -> SettlementItems.all().forEach(entries::accept)); }
}

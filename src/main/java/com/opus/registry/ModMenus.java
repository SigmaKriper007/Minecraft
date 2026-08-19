package com.opus.registry;

import com.opus.OpusVsExe;
import com.opus.inventory.RewardVaultMenu;
import com.opusvsexe.inventory.ExoInventoryMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class ModMenus {
    public static final MenuType<ExoInventoryMenu> EXO_INVENTORY = Registry.register(
        BuiltInRegistries.MENU, OpusVsExe.id("exo_inventory"),
        new MenuType<>((syncId, playerInventory) -> new ExoInventoryMenu(syncId, playerInventory), FeatureFlags.VANILLA_SET));

    public static final MenuType<RewardVaultMenu> REWARD_VAULT = Registry.register(
        BuiltInRegistries.MENU, OpusVsExe.id("reward_vault"),
        new MenuType<>((syncId, playerInventory) -> new RewardVaultMenu(syncId, playerInventory), FeatureFlags.VANILLA_SET));

    public static void init() {
    }
}
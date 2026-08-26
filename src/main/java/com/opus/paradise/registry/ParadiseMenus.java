package com.opus.paradise.registry;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.inventory.ParthenonForgeMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class ParadiseMenus {
    public static final MenuType<ParthenonForgeMenu> PARTHENON_FORGE=Registry.register(BuiltInRegistries.MENU,ParadiseLine.id("parthenon_forge"),new MenuType<>((id,inventory)->new ParthenonForgeMenu(id,inventory),FeatureFlags.VANILLA_SET));
    private ParadiseMenus(){ }
    public static void init(){ }
}

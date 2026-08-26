package com.opus.settlement.registry;

import com.opus.settlement.SettlementLine;
import com.opus.settlement.item.ExpeditionCompassItem;
import com.opus.settlement.item.JapaneseKatanaItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Tiers;

import java.util.List;

public final class SettlementItems {
    public static final ExpeditionCompassItem OPUS_RUINS_COMPASS = compass("opus_ruins_compass", ExpeditionCompassItem.Target.OPUS_RUINS);
    public static final ExpeditionCompassItem PARADISE_COMPASS = compass("paradise_expedition_compass", ExpeditionCompassItem.Target.PARADISE);
    public static final ExpeditionCompassItem DARK_FOREST_COMPASS = compass("dark_forest_expedition_compass", ExpeditionCompassItem.Target.DARK_FOREST);
    public static final ExpeditionCompassItem MOON_FOUNTAIN_COMPASS = compass("moon_fountain_expedition_compass", ExpeditionCompassItem.Target.MOON_FOUNTAIN);
    public static final Item KATANA = register("katana", new JapaneseKatanaItem(Tiers.IRON, 3, -2.1F, false, new Item.Properties().stacksTo(1)));
    public static final Item LONG_KATANA = register("long_katana", new JapaneseKatanaItem(Tiers.DIAMOND, 5, -2.4F, true, new Item.Properties().stacksTo(1).rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    public static final Item SURVIVOR_SPAWN_EGG = register("survivor_spawn_egg",
        new SpawnEggItem(SettlementEntities.SURVIVOR, 0x5A4034, 0xD8B07C, new Item.Properties()));
    public static final Item BLACK_NINJA_SPAWN_EGG = register("black_ninja_spawn_egg",
        new SpawnEggItem(SettlementEntities.BLACK_NINJA, 0x111116, 0x8D1723, new Item.Properties()));
    public static final Item SAMURAI_SPAWN_EGG = register("samurai_spawn_egg",
        new SpawnEggItem(SettlementEntities.SAMURAI, 0x33251E, 0xC79B50, new Item.Properties()));
    public static final Item YOUNG_SAMURAI_SPAWN_EGG = register("young_samurai_spawn_egg",
        new SpawnEggItem(SettlementEntities.YOUNG_SAMURAI, 0x161017, 0xB81942, new Item.Properties()));
    private static final List<Item> ALL = List.of(OPUS_RUINS_COMPASS, PARADISE_COMPASS, DARK_FOREST_COMPASS, MOON_FOUNTAIN_COMPASS,
        KATANA, LONG_KATANA, SURVIVOR_SPAWN_EGG, BLACK_NINJA_SPAWN_EGG, SAMURAI_SPAWN_EGG, YOUNG_SAMURAI_SPAWN_EGG);

    private SettlementItems() { }
    private static ExpeditionCompassItem compass(String id, ExpeditionCompassItem.Target target) {
        return (ExpeditionCompassItem)register(id, new ExpeditionCompassItem(target, new Item.Properties().stacksTo(1)));
    }
    private static Item register(String id, Item item) { return Registry.register(BuiltInRegistries.ITEM, SettlementLine.id(id), item); }
    public static List<Item> all() { return ALL; }
    public static void init() { }
}

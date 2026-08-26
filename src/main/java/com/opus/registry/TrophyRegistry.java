package com.opus.registry;

import com.opus.OpusVsExe;
import com.opus.ember.registry.EmberEntities;
import com.opus.fire.registry.FireEntities;
import com.opus.paradise.registry.ParadiseEntities;
import com.opus.darkforest.registry.DarkForestEntities;
import com.opus.settlement.registry.SettlementEntities;
import com.opus.item.TrophyItem;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.GameRules;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class TrophyRegistry {
    public static final Item HAIKU_1_5 = register("trophy_haiku_1_5", false);
    public static final Item HAIKU_2 = register("trophy_haiku_2", false);
    public static final Item HAIKU_3 = register("trophy_haiku_3", false);
    public static final Item HAIKU_4 = register("trophy_haiku_4", true);
    public static final Item HAIKU_5 = register("trophy_haiku_5", true);
    public static final Item HAIKU_OMEGA = register("trophy_haiku_omega", true);
    public static final Item HAIKU_DRONE = register("trophy_haiku_drone", false);
    public static final Item HAIKU_DRONE_PLUS = register("trophy_haiku_drone_plus", false);

    public static final Item FIRE_SLIME = register("trophy_fire_slime", false);
    public static final Item LAVA_GOLEM = register("trophy_lava_golem", false);
    public static final Item DIABLO = register("trophy_diablo", true);
    public static final Item EMBER_SLIME = register("trophy_ember_slime", false);
    public static final Item OBSIDIAN_GOLEM = register("trophy_obsidian_golem", false);
    public static final Item FLAME_DEMON = register("trophy_flame_demon", true);
    public static final Item SUNFINCH = register("trophy_sunfinch", false);
    public static final Item CLOUD_GRAZER = register("trophy_cloud_grazer", false);
    public static final Item PARADISE_WYVERN = register("trophy_paradise_wyvern", false);
    public static final Item ANGEL_BOY = register("trophy_angel_boy", true);
    public static final Item SHADE_SPIDERLING = register("trophy_shade_spiderling", false);
    public static final Item GLOOM_BROODMOTHER = register("trophy_gloom_broodmother", false);
    public static final Item MOONWING_BAT = register("trophy_moonwing_bat", false);
    public static final Item MOSSBOUND_ENDERMAN = register("trophy_mossbound_enderman", true);
    public static final Item SURVIVOR = register("trophy_survivor", false);
    public static final Item BLACK_NINJA = register("trophy_black_ninja", false);
    public static final Item SAMURAI = register("trophy_samurai", false);
    public static final Item YOUNG_SAMURAI = register("trophy_young_samurai", true);

    private static final List<Item> ALL = List.of(
        HAIKU_1_5, HAIKU_2, HAIKU_3, HAIKU_4, HAIKU_5, HAIKU_OMEGA, HAIKU_DRONE, HAIKU_DRONE_PLUS,
        FIRE_SLIME, LAVA_GOLEM, DIABLO, EMBER_SLIME, OBSIDIAN_GOLEM, FLAME_DEMON,
        SUNFINCH, CLOUD_GRAZER, PARADISE_WYVERN, ANGEL_BOY,
        SHADE_SPIDERLING, GLOOM_BROODMOTHER, MOONWING_BAT, MOSSBOUND_ENDERMAN, SURVIVOR, BLACK_NINJA, SAMURAI, YOUNG_SAMURAI
    );
    private static final Map<EntityType<?>, Item> BY_ENTITY = new IdentityHashMap<>();
    private static boolean initialized;

    private TrophyRegistry() { }

    private static Item register(String id, boolean bossTrophy) {
        Item.Properties properties = new Item.Properties().stacksTo(1).fireResistant()
            .rarity(bossTrophy ? Rarity.EPIC : Rarity.UNCOMMON);
        return Registry.register(BuiltInRegistries.ITEM, OpusVsExe.id(id), new TrophyItem(properties, id, bossTrophy));
    }

    public static List<Item> all() {
        return ALL;
    }

    public static void init() {
        if (initialized) return;
        initialized = true;

        bind(ModEntities.HAIKU_1_5, HAIKU_1_5);
        bind(ModEntities.HAIKU_2, HAIKU_2);
        bind(ModEntities.HAIKU_3, HAIKU_3);
        bind(ModEntities.HAIKU_4, HAIKU_4);
        bind(ModEntities.HAIKU_5, HAIKU_5);
        bind(ModEntities.HAIKU_OMEGA, HAIKU_OMEGA);
        bind(ModEntities.HAIKU_DRONE, HAIKU_DRONE);
        bind(ModEntities.HAIKU_DRONE_PLUS, HAIKU_DRONE_PLUS);

        bind(FireEntities.FIRE_SLIME, FIRE_SLIME);
        bind(FireEntities.LAVA_GOLEM, LAVA_GOLEM);
        bind(FireEntities.FIRE_DEMON, DIABLO);
        bind(EmberEntities.EMBER_SLIME, EMBER_SLIME);
        bind(EmberEntities.OBSIDIAN_GOLEM, OBSIDIAN_GOLEM);
        bind(EmberEntities.FLAME_DEMON, FLAME_DEMON);
        bind(ParadiseEntities.SUNFINCH, SUNFINCH);
        bind(ParadiseEntities.CLOUD_GRAZER, CLOUD_GRAZER);
        bind(ParadiseEntities.PARADISE_WYVERN, PARADISE_WYVERN);
        bind(ParadiseEntities.ANGEL_BOY, ANGEL_BOY);
        bind(DarkForestEntities.SHADE_SPIDERLING, SHADE_SPIDERLING);
        bind(DarkForestEntities.GLOOM_BROODMOTHER, GLOOM_BROODMOTHER);
        bind(DarkForestEntities.MOONWING_BAT, MOONWING_BAT);
        bind(DarkForestEntities.MOSSBOUND_ENDERMAN, MOSSBOUND_ENDERMAN);
        bind(SettlementEntities.SURVIVOR, SURVIVOR);
        bind(SettlementEntities.BLACK_NINJA, BLACK_NINJA);
        bind(SettlementEntities.SAMURAI, SAMURAI);
        bind(SettlementEntities.YOUNG_SAMURAI, YOUNG_SAMURAI);

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (!shouldDropTrophy(entity)) return;
            Item trophy = trophyFor(entity.getType());
            if (trophy != null) entity.spawnAtLocation(new ItemStack(trophy), 0.5F);
        });
    }

    public static Item trophyFor(EntityType<?> entityType) { return BY_ENTITY.get(entityType); }

    public static boolean shouldDropTrophy(LivingEntity entity) {
        return !entity.level().isClientSide && entity.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)
            && trophyFor(entity.getType()) != null;
    }

    private static void bind(EntityType<?> entityType, Item trophy) {
        Item previous = BY_ENTITY.put(entityType, trophy);
        if (previous != null) throw new IllegalStateException("Duplicate trophy binding for " + BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
    }
}

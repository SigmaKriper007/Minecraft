package com.opus.ember.item;

import com.opus.ember.registry.EmberItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class EmberArmorBonus {
    private static final Set<UUID> FLIGHT_GRANTED = new HashSet<>();
    private static final Set<UUID> SUITED = new HashSet<>();
    private static final int SET_EFFECT_DURATION = 24000;
    private EmberArmorBonus() { }

    public static boolean isFullEmberSuit(Player player) {
        return player != null
            && player.getInventory().getArmor(3).is(EmberItems.EMBER_HELMET)
            && player.getInventory().getArmor(2).is(EmberItems.EMBER_CHESTPLATE)
            && player.getInventory().getArmor(1).is(EmberItems.EMBER_LEGGINGS)
            && player.getInventory().getArmor(0).is(EmberItems.EMBER_BOOTS);
    }

    public static boolean isFullEmberSuitClient(Player player) { return isFullEmberSuit(player); }

    public static boolean hasChestplate(Player player) {
        return player != null && player.getInventory().getArmor(2).is(EmberItems.EMBER_CHESTPLATE);
    }

    public static void init() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            for (ServerPlayer player : world.players()) {
                if (player.isSpectator()) continue;
                updateEffects(player);
                updateFlight(player);
            }
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            UUID id = handler.player.getUUID();
            FLIGHT_GRANTED.remove(id);
            SUITED.remove(id);
        });
    }

    private static void updateEffects(ServerPlayer player) {
        UUID id = player.getUUID();
        if (isFullEmberSuit(player)) {
            var healthBoost = player.getEffect(MobEffects.HEALTH_BOOST);
            boolean firstEquip = SUITED.add(id);
            if (firstEquip || healthBoost == null || healthBoost.getDuration() < 300) {
                float before = player.getHealth();
                applySet(player);
                player.setHealth(Math.min(player.getMaxHealth(), before + (firstEquip ? 4.0F : 0.0F)));
            }
        } else if (SUITED.remove(id)) {
            removeSet(player);
        }
    }

    private static void applySet(ServerPlayer player) {
        player.addEffect(effect(MobEffects.FIRE_RESISTANCE));
        player.addEffect(effect(MobEffects.HEALTH_BOOST));
        player.addEffect(effect(MobEffects.DAMAGE_BOOST));
        player.addEffect(effect(MobEffects.REGENERATION));
    }

    private static void removeSet(ServerPlayer player) {
        player.removeEffect(MobEffects.FIRE_RESISTANCE);
        player.removeEffect(MobEffects.HEALTH_BOOST);
        player.removeEffect(MobEffects.DAMAGE_BOOST);
        player.removeEffect(MobEffects.REGENERATION);
    }

    private static MobEffectInstance effect(net.minecraft.world.effect.MobEffect effect) {
        return new MobEffectInstance(effect, SET_EFFECT_DURATION, 0, true, false, true);
    }

    private static void updateFlight(ServerPlayer player) {
        UUID id = player.getUUID();
        boolean chest = hasChestplate(player);
        if (chest && !player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
            FLIGHT_GRANTED.add(id);
            player.onUpdateAbilities();
        } else if (!chest && FLIGHT_GRANTED.remove(id)) {
            if (!player.isCreative() && !player.isSpectator()) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
        }
    }
}

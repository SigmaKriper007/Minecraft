package com.opus.fire.item;

import com.opus.fire.registry.FireItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class FireArmorBonus {
    private static final Set<UUID> SUITED = new HashSet<>();
    private static final int SET_EFFECT_DURATION = 24000;
    private FireArmorBonus() { }

    public static boolean isFullFireSuit(Player player) {
        return player != null
            && player.getInventory().getArmor(3).is(FireItems.FIRE_HELMET)
            && player.getInventory().getArmor(2).is(FireItems.FIRE_CHESTPLATE)
            && player.getInventory().getArmor(1).is(FireItems.FIRE_LEGGINGS)
            && player.getInventory().getArmor(0).is(FireItems.FIRE_BOOTS);
    }

    public static boolean isFullFireSuitClient(Player player) { return isFullFireSuit(player); }

    public static boolean hasChestplate(Player player) {
        return player != null && player.getInventory().getArmor(2).is(FireItems.FIRE_CHESTPLATE);
    }

    public static void init() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            for (ServerPlayer player : world.players()) {
                if (player.isSpectator()) continue;
                updateEffects(player);
            }
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            UUID id = handler.player.getUUID();
            SUITED.remove(id);
        });
    }

    private static void updateEffects(ServerPlayer player) {
        UUID id = player.getUUID();
        if (isFullFireSuit(player)) {
            boolean firstEquip = SUITED.add(id);
            var fireResistance = player.getEffect(MobEffects.FIRE_RESISTANCE);
            if (firstEquip || fireResistance == null || fireResistance.getDuration() < 300) {
                player.addEffect(effect(MobEffects.FIRE_RESISTANCE));
            }
        } else if (SUITED.remove(id)) {
            player.removeEffect(MobEffects.FIRE_RESISTANCE);
        }
    }

    private static MobEffectInstance effect(net.minecraft.world.effect.MobEffect effect) {
        return new MobEffectInstance(effect, SET_EFFECT_DURATION, 0, true, false, true);
    }
}

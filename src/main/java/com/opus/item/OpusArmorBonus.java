package com.opus.item;

import com.opus.registry.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class OpusArmorBonus {
	private static final int SET_EFFECT_DURATION = 24000;
	private static final int SHOCKWAVE_COOLDOWN_TICKS = 100;
	private static final Set<UUID> SUITED = new HashSet<>();
	private static final Map<UUID, Long> SHOCKWAVE_COOLDOWN = new HashMap<>();

	private OpusArmorBonus() {
	}

	public static boolean isFullOpusSuit(ServerPlayer player) {
		ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
		ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
		ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
		ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
		return head.is(ModItems.OPUS_HELMET)
			&& chest.is(ModItems.OPUS_CHESTPLATE)
			&& legs.is(ModItems.OPUS_LEGGINGS)
			&& feet.is(ModItems.OPUS_BOOTS);
	}

	public static boolean tryTriggerShockwave(ServerPlayer player) {
		long gameTime = player.level().getGameTime();
		Long next = SHOCKWAVE_COOLDOWN.get(player.getUUID());
		if (next != null && gameTime < next) {
			return false;
		}
		SHOCKWAVE_COOLDOWN.put(player.getUUID(), gameTime + SHOCKWAVE_COOLDOWN_TICKS);
		return true;
	}

	public static void onPlayerDisconnect(ServerPlayer player) {
		SHOCKWAVE_COOLDOWN.remove(player.getUUID());
		SUITED.remove(player.getUUID());
	}

	private static void applySet(ServerPlayer player) {
		player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, SET_EFFECT_DURATION, 1, true, false));
		player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, SET_EFFECT_DURATION, 0, true, false));
		player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, SET_EFFECT_DURATION, 9, true, false));
		player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, SET_EFFECT_DURATION, 1, true, false));
		player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, SET_EFFECT_DURATION, 0, true, false));
		player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, SET_EFFECT_DURATION, 2, true, false));
		player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, SET_EFFECT_DURATION, 2, true, false));
		player.setHealth(player.getMaxHealth());
	}

	private static void removeSet(ServerPlayer player) {
		player.removeEffect(MobEffects.MOVEMENT_SPEED);
		player.removeEffect(MobEffects.REGENERATION);
		player.removeEffect(MobEffects.HEALTH_BOOST);
		player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
		player.removeEffect(MobEffects.DIG_SPEED);
		player.removeEffect(MobEffects.DOLPHINS_GRACE);
		player.removeEffect(MobEffects.DAMAGE_BOOST);
		player.setHealth(Math.min(player.getHealth(), 20.0f));
	}

	public static void init() {
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			if (handler.getPlayer() != null) {
				onPlayerDisconnect(handler.getPlayer());
			}
		});
		ServerTickEvents.END_WORLD_TICK.register(world -> {
			if (world.getServer() == null) {
				return;
			}
			for (ServerPlayer player : world.getPlayers(p -> true)) {
				if (isFullOpusSuit(player)) {
					MobEffectInstance boost = player.getEffect(MobEffects.HEALTH_BOOST);
					if (SUITED.add(player.getUUID()) || boost == null || boost.getDuration() < 300) {
						applySet(player);
					}
				} else if (SUITED.remove(player.getUUID())) {
					removeSet(player);
				}
			}
		});
	}
}
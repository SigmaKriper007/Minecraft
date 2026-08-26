package com.opus.item;

import com.opus.registry.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class OpusArmorBonus {
	private static final int SET_EFFECT_DURATION = 24000;
	public static final int ARMOR_ABILITY_COOLDOWN_TICKS = 240;
	public static final int RAGE_DURATION_TICKS = 120;
	private static final UUID RAGE_ATTACK_SPEED_ID = UUID.fromString("45a95028-575a-48be-b743-3bfcedaa1934");
	private static final AttributeModifier RAGE_ATTACK_SPEED = new AttributeModifier(
		RAGE_ATTACK_SPEED_ID, "Opus resonant rage attack speed", 0.35D,
		AttributeModifier.Operation.MULTIPLY_TOTAL);
	private static final DustParticleOptions RAGE_DUST = new DustParticleOptions(
		new Vector3f(0.62F, 0.18F, 0.92F), 1.35F);
	private static final Set<UUID> SUITED = new HashSet<>();
	private static final Map<UUID, Long> SHOCKWAVE_COOLDOWN = new HashMap<>();
	private static final Map<UUID, Long> RAGE_UNTIL = new HashMap<>();

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
		SHOCKWAVE_COOLDOWN.put(player.getUUID(), gameTime + ARMOR_ABILITY_COOLDOWN_TICKS);
		return true;
	}

	public static void activateRage(ServerPlayer player) {
		if (!isFullOpusSuit(player)) {
			return;
		}
		RAGE_UNTIL.put(player.getUUID(), player.level().getGameTime() + RAGE_DURATION_TICKS);
		AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
		if (attackSpeed != null) {
			attackSpeed.removeModifier(RAGE_ATTACK_SPEED_ID);
			attackSpeed.addTransientModifier(RAGE_ATTACK_SPEED);
		}
		player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,
			RAGE_DURATION_TICKS, 3, true, false, true));
		player.displayClientMessage(Component.translatable("ability.opusvsexe.resonant_rage"), true);
		player.serverLevel().sendParticles(RAGE_DUST, player.getX(), player.getY() + 1.0D, player.getZ(),
			70, 1.3D, 1.1D, 1.3D, 0.08D);
	}

	public static boolean isRaging(ServerPlayer player) {
		Long until = RAGE_UNTIL.get(player.getUUID());
		return until != null && player.level().getGameTime() < until;
	}

	public static void onPlayerDisconnect(ServerPlayer player) {
		endRage(player, false);
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
		endRage(player, false);
		player.removeEffect(MobEffects.MOVEMENT_SPEED);
		player.removeEffect(MobEffects.REGENERATION);
		player.removeEffect(MobEffects.HEALTH_BOOST);
		player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
		player.removeEffect(MobEffects.DIG_SPEED);
		player.removeEffect(MobEffects.DOLPHINS_GRACE);
		player.removeEffect(MobEffects.DAMAGE_BOOST);
		player.setHealth(Math.min(player.getHealth(), 20.0f));
	}

	private static void tickRage(ServerPlayer player) {
		Long until = RAGE_UNTIL.get(player.getUUID());
		if (until != null && player.level().getGameTime() >= until) {
			endRage(player, true);
		}
	}

	private static void endRage(ServerPlayer player, boolean restoreStrength) {
		if (RAGE_UNTIL.remove(player.getUUID()) == null) {
			return;
		}
		AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
		if (attackSpeed != null) {
			attackSpeed.removeModifier(RAGE_ATTACK_SPEED_ID);
		}
		if (restoreStrength && isFullOpusSuit(player)) {
			player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,
				SET_EFFECT_DURATION, 2, true, false));
		}
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
					tickRage(player);
				} else if (SUITED.remove(player.getUUID())) {
					removeSet(player);
				}
			}
		});
	}
}

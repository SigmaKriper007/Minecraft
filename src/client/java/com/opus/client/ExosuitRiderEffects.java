package com.opus.client;

import com.opus.network.ModNetwork;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public final class ExosuitRiderEffects {
	private static boolean inventoryKeyDown = false;

	private ExosuitRiderEffects() {
	}

	public static void init() {
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (client == null || client.player == null) {
				inventoryKeyDown = false;
				return;
			}
			if (client.player.getVehicle() instanceof ExosuitEntity) {
				boolean down = client.options.keyInventory.isDown();
				if (down && !inventoryKeyDown) {
					client.options.keyInventory.consumeClick();
					if (client.screen == null) {
						ClientPlayNetworking.send(ModNetwork.EXO_INVENTORY, PacketByteBufs.empty());
					}
				}
				inventoryKeyDown = down;
			} else {
				inventoryKeyDown = false;
			}
		});
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client == null || client.player == null) {
				return;
			}
			Player player = client.player;
			if (player.getVehicle() instanceof ExosuitEntity exo
				&& exo.isControlledByLocalInstance()
				&& client.options.keyJump.isDown()) {
				exo.rideJump();
			}
		});
	}
}
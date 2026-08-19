package com.opus.client;

import com.opus.network.ModNetwork;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

/**
 * The single place that turns pilot input into packets.
 *
 * Jump and thruster bursts are applied locally (the piloting client owns vehicle
 * movement) and mirrored to the server for validation, energy and sound. Sprint
 * is edge triggered, so no per-tick packet spam. Ability keys are edge triggered
 * too, so holding a key no longer machine guns the ability.
 */
public final class ExoInputHandler {

    private static boolean jumpWasDown;
    private static boolean sprintWasDown;
    private static int clientTicks;
    private static int lastAttackTick = Integer.MIN_VALUE;

    private ExoInputHandler() {
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            clientTicks++;

            // Opus armour shockwave works with or without a suit.
            while (ExoKeybinds.ARMOR_SHOCKWAVE.consumeClick()) {
                ClientPlayNetworking.send(ModNetwork.ARMOR_SHOCKWAVE, PacketByteBufs.empty());
            }

            if (client.player == null || !(client.player.getVehicle() instanceof ExosuitEntity exo)) {
                reset();
                return;
            }
            if (client.screen != null) {
                // No steering while a GUI is open.
                if (sprintWasDown) {
                    sprintWasDown = false;
                    exo.setExoSprint(false);
                    sendInput(false);
                }
                jumpWasDown = false;
                drain();
                return;
            }

            for (int slot = 0; slot < ExoKeybinds.ABILITY.length; slot++) {
                while (ExoKeybinds.ABILITY[slot].consumeClick()) {
                    FriendlyByteBuf buf = PacketByteBufs.create();
                    buf.writeVarInt(slot);
                    ClientPlayNetworking.send(ModNetwork.EXO_ABILITY, buf);
                }
            }
            while (ExoKeybinds.EXO_INVENTORY.consumeClick()) {
                ClientPlayNetworking.send(ModNetwork.EXO_INVENTORY, PacketByteBufs.empty());
            }

            boolean sprintDown = client.options.keySprint.isDown();
            if (sprintDown != sprintWasDown) {
                sprintWasDown = sprintDown;
                exo.setExoSprint(sprintDown);
                sendInput(sprintDown);
            }

            boolean jumpDown = client.options.keyJump.isDown();
            if (jumpDown && !jumpWasDown) {
                if (exo.canGroundJump()) {
                    exo.doGroundJump();
                    sendJump(false);
                } else if (exo.canAirThrust()) {
                    exo.doAirThrust();
                    sendJump(true);
                }
            }
            jumpWasDown = jumpDown;
        });
    }

    /** Client side rate limit so left click does not flood the server. */
    public static boolean tryClientAttack(ExosuitEntity exo) {
        int cooldown = exo.getTier().attackCooldown();
        if (clientTicks - lastAttackTick < cooldown) {
            return false;
        }
        lastAttackTick = clientTicks;
        return true;
    }

    private static void reset() {
        jumpWasDown = false;
        if (sprintWasDown) {
            sprintWasDown = false;
        }
        drain();
    }

    /** Swallow queued clicks so they do not fire the moment a suit is boarded. */
    private static void drain() {
        for (KeyMapping key : ExoKeybinds.ABILITY) {
            while (key != null && key.consumeClick()) {
                // discard
            }
        }
        while (ExoKeybinds.EXO_INVENTORY != null && ExoKeybinds.EXO_INVENTORY.consumeClick()) {
            // discard
        }
    }

    private static void sendJump(boolean airThrust) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(airThrust);
        ClientPlayNetworking.send(ModNetwork.EXO_JUMP, buf);
    }

    private static void sendInput(boolean sprinting) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeByte(sprinting ? ModNetwork.INPUT_SPRINT : 0);
        ClientPlayNetworking.send(ModNetwork.EXO_INPUT, buf);
    }

    static Minecraft client() {
        return Minecraft.getInstance();
    }
}

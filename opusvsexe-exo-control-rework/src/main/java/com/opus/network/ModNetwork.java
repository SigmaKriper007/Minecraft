package com.opus.network;

import com.opus.OpusVsExe;
import com.opus.item.CombatEffects;
import com.opus.item.OpusArmorBonus;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * EXO control protocol.
 *
 * Client to server: everything the pilot presses. Nothing is trusted; the suit
 * re-checks pilot identity, energy, cooldowns and reach on the server.
 * Server to client: movement impulses, because a client controlled vehicle
 * ignores velocity written on the server.
 */
public final class ModNetwork {

    // client -> server
    public static final ResourceLocation EXO_ABILITY = OpusVsExe.id("exo_ability");
    public static final ResourceLocation EXO_ATTACK = OpusVsExe.id("exo_attack");
    public static final ResourceLocation EXO_JUMP = OpusVsExe.id("exo_jump");
    public static final ResourceLocation EXO_INPUT = OpusVsExe.id("exo_input");
    public static final ResourceLocation EXO_INVENTORY = OpusVsExe.id("exo_inventory");
    public static final ResourceLocation ARMOR_SHOCKWAVE = OpusVsExe.id("armor_shockwave");

    // server -> client
    public static final ResourceLocation EXO_IMPULSE = OpusVsExe.id("exo_impulse");

    public static final int INPUT_SPRINT = 1;

    private ModNetwork() {
    }

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(EXO_ABILITY, (server, player, handler, buf, response) -> {
            int slot = buf.readVarInt();
            server.execute(() -> withPilotedExo(player, exo -> exo.tryUseAbility(slot, player)));
        });

        ServerPlayNetworking.registerGlobalReceiver(EXO_ATTACK, (server, player, handler, buf, response) ->
                server.execute(() -> withPilotedExo(player, exo -> exo.pilotAttack(player))));

        ServerPlayNetworking.registerGlobalReceiver(EXO_JUMP, (server, player, handler, buf, response) -> {
            boolean airThrust = buf.readBoolean();
            server.execute(() -> withPilotedExo(player, exo -> exo.onPilotJump(airThrust)));
        });

        ServerPlayNetworking.registerGlobalReceiver(EXO_INPUT, (server, player, handler, buf, response) -> {
            int flags = buf.readByte();
            server.execute(() -> withPilotedExo(player, exo -> exo.setExoSprint((flags & INPUT_SPRINT) != 0)));
        });

        ServerPlayNetworking.registerGlobalReceiver(EXO_INVENTORY, (server, player, handler, buf, response) ->
                server.execute(() -> withPilotedExo(player, exo -> player.openMenu(exo.getMenuProvider()))));

        ServerPlayNetworking.registerGlobalReceiver(ARMOR_SHOCKWAVE, (server, player, handler, buf, response) ->
                server.execute(() -> {
                    if (OpusArmorBonus.isFullOpusSuit(player) && OpusArmorBonus.tryTriggerShockwave(player)) {
                        CombatEffects.shockwave(player, 7.0D, 12.0F, 3.0D, true);
                    }
                }));
    }

    public static void sendImpulse(ServerPlayer pilot, Vec3 impulse) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeDouble(impulse.x);
        buf.writeDouble(impulse.y);
        buf.writeDouble(impulse.z);
        ServerPlayNetworking.send(pilot, EXO_IMPULSE, buf);
    }

    /** Single choke point: the sender must actually be the pilot of that suit. */
    private static void withPilotedExo(ServerPlayer player, java.util.function.Consumer<ExosuitEntity> action) {
        if (player.getVehicle() instanceof ExosuitEntity exo && exo.isPilot(player) && exo.isAlive()) {
            action.accept(exo);
        }
    }
}

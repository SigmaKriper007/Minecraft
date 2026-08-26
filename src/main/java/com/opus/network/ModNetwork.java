package com.opus.network;

import com.opus.OpusVsExe;
import com.opus.item.CombatEffects;
import com.opus.item.OpusArmorBonus;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
    public static final ResourceLocation EXO_PUNCH = OpusVsExe.id("exo_punch");
    public static final ResourceLocation ARMOR_SHOCKWAVE = OpusVsExe.id("armor_shockwave");

    // server -> client
    public static final ResourceLocation EXO_IMPULSE = OpusVsExe.id("exo_impulse");
    /** Визуальные эффекты боя Омеги: тряска камеры + частицы-вспышка (задача 13). */
    public static final ResourceLocation OMEGA_FX = OpusVsExe.id("omega_fx");

    public static final int INPUT_SPRINT = 1;

    // типы FX-пакета Омеги
    public static final int FX_SHAKE_MINOR = 1;   // короткая тряска (кулак/снаряд)
    public static final int FX_SHAKE_MAJOR = 2;   // длинная тряска (слэм, кольца)
    public static final int FX_PHASE_OPEN = 3;    // переход в фазу 2
    public static final int FX_PHASE_ENRAGE = 4;  // переход в фазу 3
    public static final int FX_REQUIEM = 5;       // купольный шквал атаки Реквием
    public static final int FX_MOON_START = 6;    // босс призван — начинается «кровавая луна»
    public static final int FX_MOON_END = 7;      // босс пал — кровь уходит с неба

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

        ServerPlayNetworking.registerGlobalReceiver(EXO_PUNCH, (server, player, handler, buf, response) ->
                server.execute(() -> withPilotedExo(player, exo -> exo.tryResonancePunch(player))));

        ServerPlayNetworking.registerGlobalReceiver(ARMOR_SHOCKWAVE, (server, player, handler, buf, response) ->
                server.execute(() -> {
                    if (OpusArmorBonus.isFullOpusSuit(player) && OpusArmorBonus.tryTriggerShockwave(player)) {
                        CombatEffects.shockwave(player, 7.0D, 12.0F, 3.0D, true);
                        OpusArmorBonus.activateRage(player);
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

    /**
     * Сلاتит всем игрокам в радиусе effectRadius от центра эффект боя Омеги
     * (тряска камеры / вспышка фазы / купольная атака реквиема).
     */
    public static void sendOmegaFx(ServerLevel level, Vec3 center, int fxType, float effectRadius) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(fxType);
        buf.writeDouble(center.x);
        buf.writeDouble(center.y + 8.0D);  // уровень груди босса как источник звука
        buf.writeDouble(center.z);
        buf.writeFloat(effectRadius);
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(center) <= (effectRadius + 96.0F) * (effectRadius + 96.0F)) {
                FriendlyByteBuf copy = PacketByteBufs.create();
                copy.writeBytes(bytes);
                ServerPlayNetworking.send(player, OMEGA_FX, copy);
            }
        }
    }

    /**
     * Шлёт сигнал «одной луны» всем игрокам измерения без фильтра по радиусу
     * (мир должен покраснеть целиком, а не только вокруг босса).
     */
    public static void broadcastOmegaFx(ServerLevel level, int fxType) {
        byte[] payload;
        {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeVarInt(fxType);
            buf.writeDouble(0.0D);
            buf.writeDouble(0.0D);
            buf.writeDouble(0.0D);
            buf.writeFloat(1.0F);
            payload = new byte[buf.readableBytes()];
            buf.readBytes(payload);
        }
        for (ServerPlayer player : level.players()) {
            FriendlyByteBuf copy = PacketByteBufs.create();
            copy.writeBytes(payload);
            ServerPlayNetworking.send(player, OMEGA_FX, copy);
        }
    }

    /** Single choke point: the sender must actually be the pilot of that suit. */
    private static void withPilotedExo(ServerPlayer player, java.util.function.Consumer<ExosuitEntity> action) {
        if (player.getVehicle() instanceof ExosuitEntity exo && exo.isPilot(player) && exo.isAlive()) {
            action.accept(exo);
        }
    }
}

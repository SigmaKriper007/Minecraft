package com.opus.fire.network;

import com.opus.fire.FireLine;
import com.opus.fire.entity.projectile.FireballProjectile;
import com.opus.fire.item.FireArmorBonus;
import com.opus.fire.sound.FireSounds;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class FireNetwork {
    public static final ResourceLocation FIRE_CHARGE = FireLine.id("fire_charge");
    public static final ResourceLocation FIRE_CHARGE_FX = FireLine.id("fire_charge_fx");
    public static final ResourceLocation DIABLO_AUDIO = FireLine.id("diablo_audio");
    public static final int CHARGE_TICKS = 16;
    public static final int COOLDOWN_TICKS = 40;
    private static final Map<UUID, Integer> PENDING = new HashMap<>();
    private static final Map<UUID, Long> LAST_CAST = new HashMap<>();

    private FireNetwork() { }

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(FIRE_CHARGE, (server, player, handler, buffer, response) ->
            server.execute(() -> beginCharge(player)));
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<Map.Entry<UUID, Integer>> iterator = PENDING.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, Integer> entry = iterator.next();
                int left = entry.getValue() - 1;
                if (left > 0) { entry.setValue(left); continue; }
                iterator.remove();
                ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
                if (player != null && player.isAlive() && FireArmorBonus.isFullFireSuit(player)) cast(player);
            }
        });
    }

    private static void beginCharge(ServerPlayer player) {
        if (!player.isAlive() || player.isSpectator() || !FireArmorBonus.isFullFireSuit(player)) return;
        long now = player.level().getGameTime();
        if (now - LAST_CAST.getOrDefault(player.getUUID(), Long.MIN_VALUE / 2) < COOLDOWN_TICKS) return;
        if (PENDING.putIfAbsent(player.getUUID(), CHARGE_TICKS) != null) return;
        LAST_CAST.put(player.getUUID(), now);
        var buffer = PacketByteBufs.create();
        buffer.writeVarInt(CHARGE_TICKS);
        ServerPlayNetworking.send(player, FIRE_CHARGE_FX, buffer);
        player.level().playSound(null, player, FireSounds.FIRE_CHARGE, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    private static void cast(ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        FireballProjectile projectile = new FireballProjectile(player.level(), player, look.x, look.y, look.z);
        projectile.setPos(player.getEyePosition().add(look.scale(1.15)));
        player.level().addFreshEntity(projectile);
        player.level().playSound(null, player, FireSounds.FIREBALL_LAUNCH, SoundSource.PLAYERS, 1.2f, 1.0f);
    }

    public static void sendDiabloAudio(ServerPlayer player, boolean active) {
        var buffer = PacketByteBufs.create();
        buffer.writeBoolean(active);
        ServerPlayNetworking.send(player, DIABLO_AUDIO, buffer);
    }
}

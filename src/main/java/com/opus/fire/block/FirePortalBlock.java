package com.opus.fire.block;

import com.opus.fire.FireLine;
import com.opus.fire.sound.FireSounds;
import com.opus.fire.world.FireRealmBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FirePortalBlock extends Block {
    public static final ResourceKey<Level> FIRE_REALM = ResourceKey.create(Registries.DIMENSION, FireLine.id("fire_realm"));
    private static final Map<UUID, Long> LAST_USE = new HashMap<>();
    private static final Map<UUID, ReturnPoint> RETURN_POS = new HashMap<>();
    private static final int COOLDOWN = 80;

    public FirePortalBlock(Properties properties) { super(properties); }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player) || player.isSpectator()) return;
        long now = player.getServer().getTickCount();
        if (now - LAST_USE.getOrDefault(player.getUUID(), Long.MIN_VALUE / 2) < COOLDOWN) return;
        LAST_USE.put(player.getUUID(), now);

        if (level.dimension() == FIRE_REALM) returnToOverworld(player, pos);
        else enterRealm(player, pos);
    }

    private static void enterRealm(ServerPlayer player, BlockPos portal) {
        ServerLevel realm = player.getServer().getLevel(FIRE_REALM);
        if (realm == null) return;
        RETURN_POS.put(player.getUUID(), new ReturnPoint(player.level().dimension(),
            Vec3.atBottomCenterOf(portal.above())));
        Vec3 destination = FireRealmBuilder.ensureBuilt(realm);
        player.teleportTo(realm, destination.x, destination.y, destination.z, player.getYRot(), player.getXRot());
        realm.playSound(null, BlockPos.containing(destination), FireSounds.PORTAL_IGNITE, SoundSource.BLOCKS, 1.2f, 0.82f);
    }

    private static void returnToOverworld(ServerPlayer player, BlockPos portal) {
        ReturnPoint returnPoint = RETURN_POS.remove(player.getUUID());
        ServerLevel destinationLevel = returnPoint == null ? null : player.getServer().getLevel(returnPoint.dimension());
        Vec3 destination = returnPoint == null ? null : returnPoint.position();
        if (destinationLevel == null || destination == null) {
            destinationLevel = player.getServer().overworld();
            BlockPos spawn = destinationLevel.getSharedSpawnPos();
            destination = Vec3.atBottomCenterOf(spawn.above());
        }
        player.teleportTo(destinationLevel, destination.x, destination.y, destination.z, player.getYRot(), player.getXRot());
        destinationLevel.playSound(null, BlockPos.containing(destination), FireSounds.PORTAL_IGNITE,
            SoundSource.BLOCKS, 1.0f, 1.08f);
    }

    private record ReturnPoint(ResourceKey<Level> dimension, Vec3 position) { }
}

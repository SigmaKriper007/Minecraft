package com.opus.item;

import com.opus.entity.BlasterBeamEntity;
import com.opus.registry.ModEntities;
import com.opus.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LightLaserGunItem extends Item {
    private static final int COOLDOWN_TICKS = 140;
    private static final double RAY_RANGE = 75.0D;
    private static final double SPAWN_OFFSET = 0.9D;

    private final EntityType<? extends BlasterBeamEntity> beamType;

    public LightLaserGunItem(Properties properties) {
        this(properties, ModEntities.BLASTER_BEAM);
    }

    public LightLaserGunItem(Properties properties, EntityType<? extends BlasterBeamEntity> beamType) {
        super(properties);
        this.beamType = beamType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }
        if (!level.isClientSide) {
            Vec3 eye = player.getEyePosition(1.0F);
            Vec3 look = player.getViewVector(1.0F);
            Vec3 spawn = eye.add(look.scale(SPAWN_OFFSET));

            double beamLength = RAY_RANGE - SPAWN_OFFSET;
            BlockHitResult hit = level.clip(new ClipContext(eye, eye.add(look.scale(RAY_RANGE)),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            if (hit.getType() != HitResult.Type.MISS) {
                beamLength = Math.max(1.0D, eye.distanceTo(hit.getLocation()) - SPAWN_OFFSET);
            }

            BlasterBeamEntity beam = new BlasterBeamEntity(this.beamType, level);
            beam.setPos(spawn);
            beam.faceTo(player.getYRot(), player.getXRot());
            beam.setDeltaMovement(beam.getBeamDirection());
            beam.setSyncedDirection(beam.getBeamDirection());
            beam.setShooter(player.getUUID());
            beam.setBeamLength((float) beamLength);
            level.addFreshEntity(beam);

            level.playSound(null, spawn.x, spawn.y, spawn.z, ModSounds.SUPER_LASER, SoundSource.PLAYERS, 1.0F, 1.35F);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.opusvsexe.light_laser_gun_lore").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.literal("ПКМ: испускает вращающийся энергетический луч").withStyle(ChatFormatting.GOLD));
    }
}
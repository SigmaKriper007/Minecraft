package com.opus.item;

import com.opus.entity.LaserEntity;
import com.opus.registry.ModEntities;
import com.opus.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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

public class LaserGunItem extends Item {
    private static final int COOLDOWN_TICKS = 300;
    private static final double RANGE = 128.0D;

    public LaserGunItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.getCooldowns().isOnCooldown(this)) {
            if (!level.isClientSide) {
                Vec3 eye = player.getEyePosition(1.0F);
                Vec3 look = player.getViewVector(1.0F);
                BlockHitResult hit = level.clip(new ClipContext(eye, eye.add(look.scale(RANGE)),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                Vec3 pos;
                if (hit.getType() == HitResult.Type.MISS) {
                    pos = new Vec3(Math.floor(hit.getLocation().x) + 0.5D,
                        Math.floor(hit.getLocation().y) + 0.5D,
                        Math.floor(hit.getLocation().z) + 0.5D);
                } else {
                    pos = new Vec3(Math.floor(hit.getLocation().x) + 0.5D,
                        hit.getLocation().y,
                        Math.floor(hit.getLocation().z) + 0.5D);
                }
                LaserEntity laser = new LaserEntity(ModEntities.LASER, level);
                laser.setPos(pos);
                laser.setShooter(player.getUUID());
                level.addFreshEntity(laser);
                level.playSound(null, pos.x, pos.y, pos.z, ModSounds.SUPER_LASER, SoundSource.PLAYERS, 3.0F, 1.0F);
                player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.opusvsexe.laser_gun_lore").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.literal("ПКМ: призывает колоссальный лазер в указанной точке").withStyle(ChatFormatting.GOLD));
    }
}
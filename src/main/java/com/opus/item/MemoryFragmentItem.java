package com.opus.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;

public class MemoryFragmentItem extends Item {
    private final int fragmentId;
    
    public MemoryFragmentItem(int fragmentId) {
        super(new Properties().stacksTo(1));
        this.fragmentId = fragmentId;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.sendSystemMessage(Component.translatable(titleKey()).withStyle(ChatFormatting.GOLD));
            player.sendSystemMessage(Component.translatable(textKey()).withStyle(ChatFormatting.GRAY));
            level.playSound(null, player.blockPosition(),
                    net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP,
                    net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.5f);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.opusvsexe.memory_fragment.desc"));
        tooltip.add(Component.translatable(titleKey()).withStyle(ChatFormatting.DARK_AQUA));
    }

    private String titleKey() {
        return "lore.opusvsexe.memory_fragment." + fragmentId + ".title";
    }

    private String textKey() {
        return "lore.opusvsexe.memory_fragment." + fragmentId + ".text";
    }
}

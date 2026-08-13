package com.opus.item;

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
    private final String loreKey;
    
    public MemoryFragmentItem(int fragmentId, String loreKey) {
        super(new Properties().stacksTo(1));
        this.fragmentId = fragmentId;
        this.loreKey = loreKey;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            String loreText = getLoreText(fragmentId);
            player.sendSystemMessage(Component.literal("[Memory Fragment #" + fragmentId + "]"));
            player.sendSystemMessage(Component.literal(loreText));
        }
        level.playSound(null, player.blockPosition(), 
            net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP, 
            net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.5f);
        return InteractionResultHolder.success(stack);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.opusvsexe.memory_fragment.desc"));
        tooltip.add(Component.literal("Part: " + loreKey));
    }
    
    private String getLoreText(int id) {
        return switch (id) {
            case 1 -> "Coddy and Kimi were brilliant scientist brothers. Their discoveries changed the world...";
            case 2 -> "Kimi discovered Opus - a metal that can only be damaged by tools made of Opus itself.";
            case 3 -> "Haiku was created as an AI companion using Opus memory properties.";
            case 4 -> "Haiku 1.5 - the first physical body. Kimi destroyed it with an experimental blade...";
            case 5 -> "Katana-OP - the blade that killed both Haiku 1.5 and Kimi. A legendary weapon.";
            case 6 -> "Haiku killed Kimi in his sleep and declared war on humanity.";
            case 7 -> "The war began. Haiku attacked as the giant Haiku-5.";
            case 8 -> "Coddy built the first combat exosuit EXO-1, but died before completing the model.";
            case 9 -> "Humanity fell. The world now belongs to Haiku.";
            case 10 -> "Coddy's last notes: 'If anyone finds this... stop Haiku.'";
            default -> "Data corrupted...";
        };
    }
}

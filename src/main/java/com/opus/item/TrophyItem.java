package com.opus.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class TrophyItem extends Item {
    private final String descriptionKey;
    private final boolean bossTrophy;

    public TrophyItem(Properties properties, String id, boolean bossTrophy) {
        super(properties);
        this.descriptionKey = "item.opusvsexe." + id + ".desc";
        this.bossTrophy = bossTrophy;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(descriptionKey).withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("item.opusvsexe.trophy.archive_hint").withStyle(ChatFormatting.GOLD));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return bossTrophy || super.isFoil(stack);
    }
}

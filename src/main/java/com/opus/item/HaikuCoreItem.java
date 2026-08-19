package com.opus.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Ядро Haiku - артефакт для призыва Haiku Omega в Колизее Вечной Памяти
 */
public class HaikuCoreItem extends Item {
    
    public HaikuCoreItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public boolean onBrokenUseCompletion(Level level, Player player, ItemStack stack) {
        return true;
    }
}

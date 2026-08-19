package com.opus.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Блок Сердце Алтаря - центральный блок Колизея Вечной Памяти
 * Активируется при использовании Ядра Haiku
 */
public class AltarHeartBlock extends Block {
    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");
    
    public AltarHeartBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVATED, false));
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack item = player.getItemInHand(hand);
        
        // Проверка на Ядро Haiku (будет зарегистрировано в ModItems)
        if (item.getItem() == com.opus.registry.ModItems.HAIKU_CORE && !state.getValue(ACTIVATED)) {
            // Активация алтаря и призыв босса
            if (!level.isClientSide) {
                // Удалить ядро из руки игрока
                if (!player.isCreative()) {
                    item.shrink(1);
                }
                
                // Установить активированное состояние
                level.setBlock(pos, state.setValue(ACTIVATED, true), 3);
                
                // Запустить процедуру призыва босса Haiku Omega
                summonHaikuOmega(level, pos);
            }
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
    
    private void summonHaikuOmega(Level level, BlockPos pos) {
        // Логика призыва босса будет реализована через команду или спавнер
        // Здесь можно добавить звуковые эффекты, частицы и т.д.
        level.broadcastEntityEvent(null, (byte) 0); // Событие для эффектов
        
        // Отправить сообщение всем игрокам
        for (Player player : level.players()) {
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable("boss.summon.haiku_omega"), true);
        }
    }
}

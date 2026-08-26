package com.opus.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Сердце Алтаря — точка присутствия блока в мире. Сам по себе блок-entity
 * пустой: зацикленный эмбиент altar_heart_loop гонится на стороне клиента
 * (AltarHeartAmbience), которая находит этот блок-entity в чанках рядом
 * с игроком.
 */
public class AltarHeartBlockEntity extends BlockEntity {

    public AltarHeartBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALTAR_HEART, pos, state);
    }
}

package com.opus.item;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

/**
 * Эффект "Ослепление Вспышкой" - временное ослепление от яркого света/взрыва
 * Применяется при призыве Haiku Omega всем игрокам в радиусе 128 блоков
 */
public class FlashBlindnessEffect extends MobEffect {
    
    public FlashBlindnessEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFFFFF); // Белый цвет для эффекта ослепления
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }
    
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // Дополнительная логика эффекта (если нужна)
    }
    
    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
    }
}

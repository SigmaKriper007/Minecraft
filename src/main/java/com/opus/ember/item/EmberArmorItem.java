package com.opus.ember.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

/**
 * Тлеющая броня. Вся визуализация — 3D (слои EmberHelmetLayer / EmberPlateLayer).
 * Бонусы сета и полёт — в EmberArmorBonus (серверный тик).
 */
public class EmberArmorItem extends ArmorItem {

    public EmberArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }
}

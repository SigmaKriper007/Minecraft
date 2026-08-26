package com.opus.settlement.entity;

import com.opus.settlement.registry.SettlementItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class SamuraiEntity extends JapaneseWarriorEntity {
    public SamuraiEntity(EntityType<? extends Monster> type, Level level) { super(type, level); }
    public static AttributeSupplier.Builder createAttributes() { return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH,36D).add(Attributes.MOVEMENT_SPEED,.27D).add(Attributes.ATTACK_DAMAGE,9D).add(Attributes.ARMOR,6D).add(Attributes.KNOCKBACK_RESISTANCE,.25D).add(Attributes.FOLLOW_RANGE,32D); }
    @Override protected ItemStack defaultWeapon() { return new ItemStack(SettlementItems.LONG_KATANA); }
    @Override protected int specialAction() { return ACTION_LONG_LUNGE; }
    @Override protected int actionWindup() { return 8; }
    @Override protected float techniqueDamage() { return 12F; }
}

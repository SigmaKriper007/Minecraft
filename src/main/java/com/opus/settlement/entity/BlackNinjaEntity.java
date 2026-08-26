package com.opus.settlement.entity;

import com.opus.settlement.registry.SettlementItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class BlackNinjaEntity extends JapaneseWarriorEntity {
    public BlackNinjaEntity(EntityType<? extends Monster> type, Level level) { super(type, level); }
    public static AttributeSupplier.Builder createAttributes() { return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH,20D).add(Attributes.MOVEMENT_SPEED,.34D).add(Attributes.ATTACK_DAMAGE,5D).add(Attributes.FOLLOW_RANGE,32D); }
    @Override protected ItemStack defaultWeapon() { return new ItemStack(SettlementItems.KATANA); }
    @Override protected int specialAction() { return ACTION_SMOKE_STEP; }
    @Override protected int actionWindup() { return 4; }
    @Override protected float techniqueDamage() { return 7F; }
}

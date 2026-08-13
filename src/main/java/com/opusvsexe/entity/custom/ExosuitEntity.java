package com.opusvsexe.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public abstract class ExosuitEntity extends LivingEntity {
    protected int energy;
    protected int maxEnergy;
    protected String exoTier;

    protected ExosuitEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        this.maxEnergy = 1000;
        this.energy = this.maxEnergy;
        this.exoTier = "unknown";
    }

    public int getEnergy() {
        return this.energy;
    }

    public int getMaxEnergy() {
        return this.maxEnergy;
    }

    public void addEnergy(int amount) {
        this.energy = Math.min(this.energy + amount, this.maxEnergy);
    }

    public void consumeEnergy(int amount) {
        this.energy = Math.max(this.energy - amount, 0);
    }

    public boolean hasEnoughEnergy(int amount) {
        return this.energy >= amount;
    }

    public abstract void performAbility();

    public abstract void performAttack();

    @Override
    protected void initCapabilities() {
        // Инициализация компонентов
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, LivingState state) {
        // Экзоскелеты не получают урон от падения
    }
}

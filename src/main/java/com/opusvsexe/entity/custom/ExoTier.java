package com.opusvsexe.entity.custom;

/**
 * Static stat block for every EXO frame.
 *
 * Every number the control layer needs lives here, so no subclass ever has to
 * poke protected fields in its constructor again (that was the reason attack
 * damage always resolved to the fallback value in the old build).
 */
public enum ExoTier {
    //     display name          hp     energy  speed  sprint  dmg   reach  atkCd  jump  step  regen drain  kbRes  armor  airThrust
    EXO_1("EXO-1 Sentinel",     60.0D,   500,  0.24D, 1.35D, 10.0F,  4.5D,   20,  0.62D, 1.0F,   6,    1,   0.6D,   8.0D, false),
    EXO_2("EXO-2 Hunter",       90.0D,   700,  0.32D, 1.60D, 12.0F,  5.0D,   16,  0.70D, 1.0F,   7,    2,   0.6D,  10.0D, true),
    EXO_3("EXO-3 Vanguard",    140.0D,   900,  0.28D, 1.45D, 15.0F,  6.0D,   20,  0.78D, 1.5F,   8,    2,   0.8D,  14.0D, true),
    EXO_4("EXO-4 Titan",       220.0D,  1200, 0.24D, 1.30D, 18.0F,  7.5D,   24,  0.85D, 2.0F,   9,    3,   1.0D,  18.0D, false),
    EXO_5("EXO-5 Vengeance",   400.0D,  2500, 0.28D, 1.45D, 24.0F, 10.0D,   18,  1.05D, 2.0F,  14,    4,   1.0D,  24.0D, true);

    /** Energy burned by a normal mech hop. */
    public static final int JUMP_COST = 5;
    /** Energy burned by a mid-air thruster burst. */
    public static final int AIR_THRUST_COST = 25;

    private final String displayName;
    private final double maxHealth;
    private final int maxEnergy;
    private final double moveSpeed;
    private final double sprintMultiplier;
    private final float attackDamage;
    private final double attackReach;
    private final int attackCooldown;
    private final double jumpPower;
    private final float stepHeight;
    private final int energyRegen;
    private final int energyDrain;
    private final double knockbackResistance;
    private final double armor;
    private final boolean airThrust;

    ExoTier(String displayName, double maxHealth, int maxEnergy, double moveSpeed, double sprintMultiplier,
            float attackDamage, double attackReach, int attackCooldown, double jumpPower, float stepHeight,
            int energyRegen, int energyDrain, double knockbackResistance, double armor, boolean airThrust) {
        this.displayName = displayName;
        this.maxHealth = maxHealth;
        this.maxEnergy = maxEnergy;
        this.moveSpeed = moveSpeed;
        this.sprintMultiplier = sprintMultiplier;
        this.attackDamage = attackDamage;
        this.attackReach = attackReach;
        this.attackCooldown = attackCooldown;
        this.jumpPower = jumpPower;
        this.stepHeight = stepHeight;
        this.energyRegen = energyRegen;
        this.energyDrain = energyDrain;
        this.knockbackResistance = knockbackResistance;
        this.armor = armor;
        this.airThrust = airThrust;
    }

    public String displayName() { return this.displayName; }
    public double maxHealth() { return this.maxHealth; }
    public int maxEnergy() { return this.maxEnergy; }
    public double moveSpeed() { return this.moveSpeed; }
    public double sprintMultiplier() { return this.sprintMultiplier; }
    public float attackDamage() { return this.attackDamage; }
    public double attackReach() { return this.attackReach; }
    public int attackCooldown() { return this.attackCooldown; }
    public double jumpPower() { return this.jumpPower; }
    public float stepHeight() { return this.stepHeight; }
    public int energyRegen() { return this.energyRegen; }
    public int energyDrain() { return this.energyDrain; }
    public double knockbackResistance() { return this.knockbackResistance; }
    public double armor() { return this.armor; }
    public boolean canAirThrust() { return this.airThrust; }
}

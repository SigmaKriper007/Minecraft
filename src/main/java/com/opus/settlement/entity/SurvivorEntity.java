package com.opus.settlement.entity;

import com.opus.entity.haiku.HaikuMob;
import com.opus.registry.ModTags;
import com.opus.settlement.registry.SettlementItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;

public final class SurvivorEntity extends AbstractVillager implements NeutralMob {
    public static final int SKIN_VARIANTS = 12;
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(SurvivorEntity.class, EntityDataSerializers.INT);
    private static final UniformInt ANGER_TIME = UniformInt.of(400, 800);
    private int remainingAngerTime;
    @Nullable private UUID angerTarget;

    public SurvivorEntity(EntityType<? extends SurvivorEntity> type, Level level) {
        super(type, level);
        setCanPickUpLoot(true);
        setPersistenceRequired();
        xpReward = 3;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, .10D)
            .add(Attributes.ATTACK_DAMAGE, 1.0D)
            .add(Attributes.ATTACK_SPEED, 4.0D)
            .add(Attributes.ARMOR, 0.0D)
            .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(VARIANT, 0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new LowHealthFleeGoal());
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new AvoidEntityGoal<>(this, HaikuMob.class, 18F, 1.05D, 1.35D, ignored -> !isOpusArmed()));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.15D, true) {
            @Override public boolean canUse() { return !isRetreating() && super.canUse(); }
            @Override public boolean canContinueToUse() { return !isRetreating() && super.canContinueToUse(); }
        });
        goalSelector.addGoal(6, new RandomStrollGoal(this, .85D));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, HaikuMob.class, 10, true, false,
            target -> isOpusArmed() && !isRetreating()));
        targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        entityData.set(VARIANT, random.nextInt(SKIN_VARIANTS));
        return super.finalizeSpawn(level, difficulty, reason, data, tag);
    }

    public int getVariant() { return Math.floorMod(entityData.get(VARIANT), SKIN_VARIANTS); }
    public void setVariant(int variant) { entityData.set(VARIANT, Math.floorMod(variant, SKIN_VARIANTS)); }
    public boolean isRetreating() { return getHealth() < 6.0F; }
    public boolean isOpusArmed() { return getMainHandItem().is(ModTags.OPUS_WEAPON) || getOffhandItem().is(ModTags.OPUS_WEAPON); }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (isRetreating()) return false;
        if (target instanceof HaikuMob) return isOpusArmed() && super.canAttack(target);
        if (target instanceof Player player) return hasPlayerAnger(player) && super.canAttack(target);
        return super.canAttack(target);
    }

    /** NeutralMob.isAngryAt calls canAttack, so player anger must be read without calling it back. */
    private boolean hasPlayerAnger(Player player) {
        UUID target = getPersistentAngerTarget();
        if (target != null) return target.equals(player.getUUID());
        return getRemainingPersistentAngerTime() > 0 && level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean accepted = super.hurt(source, amount);
        if (accepted && source.getEntity() instanceof Player player && !level().isClientSide) {
            setPersistentAngerTarget(player.getUUID());
            startPersistentAngerTimer();
            setLastHurtByPlayer(player);
            if (!isRetreating()) setTarget(player);
        }
        return accepted;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (level() instanceof ServerLevel server) updatePersistentAnger(server, true);
        if (tickCount % 80 == 0) applyArmorAbilities();
        if (isRetreating() && getTarget() != null) setTarget(null);
    }

    private void applyArmorAbilities() {
        if (wearsSet("opus")) {
            renew(MobEffects.DAMAGE_BOOST, 2);
            renew(MobEffects.HEALTH_BOOST, 4);
        } else if (wearsSet("fire") || wearsSet("ember")) {
            renew(MobEffects.FIRE_RESISTANCE, 0);
        } else if (wearsSet("parthenon")) {
            renew(MobEffects.DAMAGE_RESISTANCE, 0);
            renew(MobEffects.SLOW_FALLING, 0);
        } else if (wearsSet("dark_forest")) {
            renew(MobEffects.NIGHT_VISION, 0);
            renew(MobEffects.DIG_SPEED, 2);
            renew(MobEffects.MOVEMENT_SPEED, 1);
            renew(MobEffects.DAMAGE_BOOST, 0);
            renew(MobEffects.DOLPHINS_GRACE, 1);
        }
    }

    public void refreshArmorAbilities() { applyArmorAbilities(); }

    private boolean wearsSet(String prefix) {
        return armorPath(EquipmentSlot.HEAD).equals(prefix + "_helmet")
            && armorPath(EquipmentSlot.CHEST).equals(prefix + "_chestplate")
            && armorPath(EquipmentSlot.LEGS).equals(prefix + "_leggings")
            && armorPath(EquipmentSlot.FEET).equals(prefix + "_boots");
    }

    private String armorPath(EquipmentSlot slot) {
        var id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(getItemBySlot(slot).getItem());
        return id == null || !id.getNamespace().equals("opusvsexe") ? "" : id.getPath();
    }

    private void renew(MobEffect effect, int amplifier) {
        MobEffectInstance current = getEffect(effect);
        if (current == null || current.getAmplifier() != amplifier || current.getDuration() < 60)
            addEffect(new MobEffectInstance(effect, 220, amplifier, false, false, true));
    }

    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        return stack.is(ModTags.OPUS_WEAPON) || stack.getItem() instanceof ArmorItem || stack.getItem() instanceof TieredItem
            || stack.getItem() instanceof ProjectileWeaponItem || stack.getItem() instanceof ShieldItem || stack.getItem() instanceof TridentItem;
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        super.pickUpItem(itemEntity);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!isAlive() || isBaby() || isTrading() || player.isSecondaryUseActive() || isAngryAt(player)) return super.mobInteract(player, hand);
        if (!level().isClientSide) {
            setTradingPlayer(player);
            openTradingScreen(player, getDisplayName(), 1);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    protected void updateTrades() {
        MerchantOffers list = getOffers();
        list.add(new MerchantOffer(new ItemStack(Items.BREAD, 16), new ItemStack(Items.EMERALD, 2), 16, 2, .05F));
        list.add(new MerchantOffer(new ItemStack(Items.IRON_INGOT, 8), new ItemStack(Items.EMERALD, 3), 12, 3, .05F));
        list.add(routeOffer(SettlementItems.OPUS_RUINS_COMPASS, 10));
        list.add(routeOffer(SettlementItems.PARADISE_COMPASS, 16));
        list.add(routeOffer(SettlementItems.DARK_FOREST_COMPASS, 14));
        list.add(routeOffer(SettlementItems.MOON_FOUNTAIN_COMPASS, 18));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 5), new ItemStack(Items.COOKED_BEEF, 12), 8, 2, .05F));
        list.add(new MerchantOffer(new ItemStack(Items.EMERALD, 4), new ItemStack(Items.TORCH, 32), 12, 2, .05F));
    }

    private MerchantOffer routeOffer(net.minecraft.world.item.Item item, int emeralds) {
        return new MerchantOffer(new ItemStack(Items.EMERALD, emeralds), new ItemStack(Items.COMPASS), new ItemStack(item), 4, 12, .12F);
    }

    @Override protected void rewardTradeXp(MerchantOffer offer) { }
    @Override public boolean showProgressBar() { return false; }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) { return null; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.VILLAGER_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.PLAYER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.PLAYER_DEATH; }
    @Override protected SoundEvent getTradeUpdatedSound(boolean success) { return success ? SoundEvents.VILLAGER_YES : SoundEvents.VILLAGER_NO; }
    @Override public SoundEvent getNotifyTradeSound() { return SoundEvents.VILLAGER_YES; }

    @Override public int getRemainingPersistentAngerTime() { return remainingAngerTime; }
    @Override public void setRemainingPersistentAngerTime(int ticks) { remainingAngerTime = ticks; }
    @Override @Nullable public UUID getPersistentAngerTarget() { return angerTarget; }
    @Override public void setPersistentAngerTarget(@Nullable UUID uuid) { angerTarget = uuid; }
    @Override public void startPersistentAngerTimer() { setRemainingPersistentAngerTime(ANGER_TIME.sample(random)); }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", getVariant());
        addPersistentAngerSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setVariant(tag.getInt("Variant"));
        readPersistentAngerSaveData(level(), tag);
    }

    private final class LowHealthFleeGoal extends Goal {
        private Vec3 escape;
        LowHealthFleeGoal() { setFlags(EnumSet.of(Flag.MOVE)); }
        @Override public boolean canUse() {
            if (!isRetreating()) return false;
            LivingEntity threat = getLastHurtByMob();
            if (threat == null) threat = getTarget();
            if (threat == null) return false;
            escape = DefaultRandomPos.getPosAway(SurvivorEntity.this, 18, 8, threat.position());
            return escape != null;
        }
        @Override public boolean canContinueToUse() { return isRetreating() && !getNavigation().isDone(); }
        @Override public void start() { setTarget(null); getNavigation().moveTo(escape.x, escape.y, escape.z, 1.45D); }
        @Override public void stop() { escape = null; }
    }
}

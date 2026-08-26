package com.opus.entity.haiku;

import com.opus.registry.ModTags;
import com.opus.sound.ModSounds;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Shared Haiku chassis rules: faction alliance, Opus-only damage lattice,
 * fire immunity and reusable low-bandwidth telegraph helpers.
 */
public abstract class HaikuMob extends PathfinderMob implements GeoAnimatable {

    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation DRIFT_ANIM = RawAnimation.begin().thenLoop("drift");

    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);

    protected HaikuMob(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    // ---- faction and Opus memory-lattice protection ----------------------

    @Override
    public boolean isAlliedTo(Entity entity) {
        return entity instanceof HaikuMob || super.isAlliedTo(entity);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !(target instanceof HaikuMob) && super.canAttack(target);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    /**
     * Only an Opus-tagged item or an EXO chassis can transfer force through
     * Opus memory metal. Administrative/void damage remains available so a
     * broken entity can still be removed safely.
     */
    protected boolean isValidOpusDamage(DamageSource source) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return true;
        }

        Entity attacker = source.getEntity();
        if (attacker instanceof Player player) {
            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();
            return main.is(ModTags.OPUS_WEAPON) || off.is(ModTags.OPUS_WEAPON);
        }
        if (attacker instanceof Mob mob) {
            if (mob.getMainHandItem().is(ModTags.OPUS_WEAPON) || mob.getOffhandItem().is(ModTags.OPUS_WEAPON)) {
                return true;
            }
            String typeId = String.valueOf(EntityType.getKey(mob.getType()));
            return typeId.startsWith("opusvsexe:exo_");
        }
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide && !isValidOpusDamage(source)) {
            if (amount > 0.0F && this.tickCount % 5 == 0) {
                this.level().playSound(null, this.blockPosition(), ModSounds.BOSS_DEFLECT,
                        SoundSource.HOSTILE, 0.8F, 1.25F);
                if (this.level() instanceof ServerLevel server) {
                    server.sendParticles(ParticleTypes.CRIT,
                            this.getX(), this.getY() + this.getBbHeight() * 0.55, this.getZ(),
                            Math.max(4, Math.min(18, (int) this.getBbHeight() * 2)),
                            this.getBbWidth() * 0.35, this.getBbHeight() * 0.2,
                            this.getBbWidth() * 0.35, 0.04);
                }
            }
            return false;
        }
        return super.hurt(source, amount);
    }

    protected void emitTelegraphRing(double radius, double yOffset, int points,
                                     ParticleOptions particle) {
        if (!(this.level() instanceof ServerLevel server)) {
            return;
        }
        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0 * i / points;
            server.sendParticles(particle,
                    this.getX() + Math.cos(angle) * radius,
                    this.getY() + yOffset,
                    this.getZ() + Math.sin(angle) * radius,
                    1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "haiku_controller", 0, this::animationPredicate));
    }

    /**
     * Безопасный запуск анимации с принудительным сбросом.
     * GeckoLib 4.4.9: setAnimation() с тем же RawAnimation не перезапускает
     * анимацию, если она уже завершилась (остаётся в STOPPED).
     * forceAnimationReset() принудительно перезагружает, что позволяет
     * повторно сыграть одноразовую анимацию (attack/hurt/special).
     *
     * @param repeat true — можно перезапускать повторно, false — сыграть
     *               один раз и остаться (смерть)
     */
    protected <T extends GeoAnimatable> void playOnce(AnimationState<T> state, RawAnimation anim, boolean repeat) {
        AnimationController<T> controller = state.getController();
        RawAnimation current = controller.getCurrentRawAnimation();
        boolean finished = controller.hasAnimationFinished();
        // Принудительный сброс: если анимация другая, или она завершилась и repeat=true
        if (current == null || !current.equals(anim) || (repeat && finished)) {
            controller.forceAnimationReset();
        }
        controller.setAnimation(anim);
    }

    protected PlayState animationPredicate(AnimationState<HaikuMob> state) {
        state.getController().setAnimation(state.isMoving() ? DRIFT_ANIM : IDLE_ANIM);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animatableCache;
    }

    @Override
    public double getTick(Object object) {
        return this.tickCount;
    }
}

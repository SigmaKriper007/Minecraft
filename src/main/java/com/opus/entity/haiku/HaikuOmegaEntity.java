package com.opus.entity.haiku;

import com.opus.OpusVsExe;
import com.opus.entity.omega.OmegaRingWaveEntity;
import com.opus.entity.omega.OmegaShrapnelEntity;
import com.opus.entity.omega.OmegaSkyLaserEntity;
import com.opus.entity.omega.OmegaSlashEntity;
import com.opus.registry.ModEntities;
import com.opus.registry.ModItems;
import com.opus.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;


/**
 * Haiku-Ω «Omega» — финальный босс (задача 13, полный бой).
 *
 * Механики:
 *  - Опущный гейт: урон наносят ТОЛЬКО предметы тега opus_weapon и урон
 *    экзоскелетов EXO. Всё остальное — 0 урона («звон» + искры).
 *  - Три фазы по HP: 100-50 / 50-25 / 25-0. Переходы с анимациями
 *    phase_open (ядро раскрыто, уязвимость ×2 на 5с) и enrage_roar.
 *  - Лейш-зона R=64 вокруг алтаря: вне зоны босс регенерирует и не
 *    преследует.
 *  - Планировщик атак по фазам: снаряды турели, орбитальные лучи,
 *    слэмы, серповидные взмахи, кольцевой импульс, телепорт, Реквием.
 *  - Миньоны: фаза 2 — дроны + хаски (кап 4), фаза 3 — Drone+ + элит
 *    (Enforcer) (кап 3). Интервалы зависят от фазы.
 *  - Боссбар с именем фазы, музыка темы, переход фазы 3 ускоряет трек.
 */
public class HaikuOmegaEntity extends HaikuMob {

    // ---- константы боя ----------------------------------------------------
    /** Радиус арены для VFX/реквиема (не радиус регена). */
    public static final double LEASH_RADIUS = 64.0D;
    /** Максимальная дистанция ИГРОКА от алтаря: если игрок ушёл дальше,
     *  босс регенерирует и не атакует (задача 20). */
    public static final double PLAYER_LEASH_RADIUS = 42.0D;
    /** Реген, пока игрок вне арены, HP/сек. */
    private static final float LEASH_REGEN_PER_SEC = 12.0F;
    /** Окно уязвимости ядра после перехода в фазу 2, в тиках. */
    public static final int CORE_OPEN_TICKS = 100;
    /** Стандартная пауза между атаками по фазам (тик). */
    private static final int[] ATTACK_PAUSE = {70, 45, 30};
    /** Интервалы саммона миньонов (тик) — частота ×10 (фидбек 2026-08-22). */
    private static final int[] MINION_INTERVAL = {40, 15, 16};
    private static final int[] MINION_CAP = {6, 10, 8};
    /** Полосы HP фаз: фаза2 <= 50%, фаза3 <= 25%. */
    private static final float PHASE2_HP = 0.5F;
    private static final float PHASE3_HP = 0.25F;

    private int currentPhase = 1;
    private boolean musicStarted = false;
    private long musicRestartTick = 0;
    private static final int MUSIC_LENGTH_TICKS = 8270;

    /** Якорь лейша — позиция алтаря призыва. */
    private Vec3 leashAnchor = null;
    /** Счётчик длинной death-анимации (как у эндер-дракона, 200 тиков). */
    private int deathAnimTime = 0;
    /** GameTime, до которого активно уязвимое ядро (переход в фазу 2). */
    private long coreOpenUntil = 0;
    /** Следующий тик очередного планировщика. */
    private long nextAttackTick = 0;
    private long nextMinionTick = 0;
    /** Активная однократная атака: анимация + тик запуска действия. */
    private ActiveAttack activeAttack = null;

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.opusvsexe.haiku_omega"),
            ServerBossEvent.BossBarColor.YELLOW, ServerBossEvent.BossBarOverlay.PROGRESS);

    public HaikuOmegaEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        // Опыт как у эндер-дракона: xpReward=0, а 500 XP отдаются порциями
        // в tickDeath (как ванильный дракон) — задача 21
        this.xpReward = 0;
    }

    // ---- анимации ---------------------------------------------------------

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation HURT_ANIM = RawAnimation.begin().thenPlay("hurt");
    private static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlay("death");
    private static final RawAnimation MELEE_ANIM = RawAnimation.begin().thenPlay("attack_melee");
    private static final RawAnimation VOLLEY_ANIM = RawAnimation.begin().thenPlay("attack_volley");
    private static final RawAnimation ORBITAL_ANIM = RawAnimation.begin().thenPlay("attack_orbital");
    private static final RawAnimation SLASH_ANIM = RawAnimation.begin().thenPlay("attack_slash");
    private static final RawAnimation RING_ANIM = RawAnimation.begin().thenPlay("attack_ring");
    private static final RawAnimation SLAM_ANIM = RawAnimation.begin().thenPlay("attack_slam");
    private static final RawAnimation TELEPORT_ANIM = RawAnimation.begin().thenPlay("attack_teleport");
    private static final RawAnimation REQUIEM_ANIM = RawAnimation.begin().thenPlay("attack_requiem");
    private static final RawAnimation PHASE_OPEN_ANIM = RawAnimation.begin().thenPlay("phase_open");
    private static final RawAnimation ENRAGE_ANIM = RawAnimation.begin().thenPlay("enrage_roar");
    private static final RawAnimation SUMMON_ANIM = RawAnimation.begin().thenPlay("summon_call");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "omega_controller", 4, this::omegaAnimationPredicate));
    }

    private PlayState omegaAnimationPredicate(AnimationState<HaikuOmegaEntity> state) {
        HaikuOmegaEntity self = state.getAnimatable();
        if (self.isDeadOrDying()) {
            playOnce(state, DEATH_ANIM, false);
            return PlayState.CONTINUE;
        }
        // активная атака/переход фазы
        ActiveAttack atk = self.activeAttack;
        if (atk != null && self.level().getGameTime() <= atk.animEndTick) {
            playOnce(state, atk.anim, true);
            return PlayState.CONTINUE;
        }
        if (self.hurtTime > 0) {
            playOnce(state, HURT_ANIM, true);
            return PlayState.CONTINUE;
        }
        if (state.isMoving()) {
            state.getController().setAnimation(WALK_ANIM);
            return PlayState.CONTINUE;
        }
        state.getController().setAnimation(IDLE_ANIM);
        return PlayState.CONTINUE;
    }

    // ---- атрибуты, цели ----------------------------------------------------

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.3));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 500.0)
            .add(Attributes.MOVEMENT_SPEED, 0.45)
            .add(Attributes.ATTACK_DAMAGE, 25.0)
            .add(Attributes.FOLLOW_RANGE, 50.0)
            .add(Attributes.ARMOR, 20.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 22.0f;
    }

    // ---- опуссный гейт ------------------------------------------------------

    /**
     * Урон проходит ТОЛЬКО от оружия из Опуса (тег #opusvsexe:opus_weapon)
     * или от экзоскелетов EXO. Фаза 2 + открытое ядро: урон ×2.
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide && !isValidOpusDamage(source)) {
            return super.hurt(source, amount);
        }
        // уязвимое ядро фазы 2 — двойной урон
        float dmg = amount;
        if (this.currentPhase == 2 && this.level().getGameTime() < this.coreOpenUntil) {
            dmg *= 2.0F;
            if (this.level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                        this.getX(), this.getY() + 14.0, this.getZ(), 30, 0.8, 0.8, 0.8, 0.1);
            }
            this.level().playSound(null, this.getX(), this.getY() + 14.0, this.getZ(),
                    ModSounds.BOSS_CORE_HIT, SoundSource.HOSTILE, 1.5F, 1.0F);
        }
        return super.hurt(source, dmg);
    }

    // ---- жизненный цикл ------------------------------------------------------

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        updatePhase();

        boolean hasTarget = getTarget() != null && isAlive();
        if (hasTarget) {
            if (!musicStarted) {
                musicStarted = true;
                musicRestartTick = level().getGameTime() + MUSIC_LENGTH_TICKS;
                startBossMusic();
            } else if (level().getGameTime() >= musicRestartTick) {
                musicRestartTick = level().getGameTime() + MUSIC_LENGTH_TICKS;
                startBossMusic();
            }
            if (leashAnchor == null) {
                leashAnchor = position();
            }
            // Проверка дистанции игрока от алтаря: если игрок ушёл за 42 блока,
            // босс регенерирует и не атакует (задача 20).
            if (isPlayerOutsideLeash()) {
                this.heal(LEASH_REGEN_PER_SEC / 20.0F);
                // не атакуем, но не сбрасываем музыку
            } else {
                runLeash();
                runAttackScheduler();
            }
        } else if (musicStarted) {
            musicStarted = false;
            stopBossMusic();
        }
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    /**
     * Проверяет, находится ли текущий игрок-цель за пределами
     * PLAYER_LEASH_RADIUS от якоря лейша (алтаря).
     */
    private boolean isPlayerOutsideLeash() {
        if (leashAnchor == null) return false;
        LivingEntity target = getTarget();
        if (target == null) return false;
        double dx = target.getX() - leashAnchor.x;
        double dz = target.getZ() - leashAnchor.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        return dist > PLAYER_LEASH_RADIUS;
    }

    private void runLeash() {
        if (leashAnchor == null) return;
        Vec3 d = this.position().subtract(leashAnchor);
        double dist = Math.sqrt(d.x * d.x + d.z * d.z);
        if (dist > LEASH_RADIUS) {
            // возврат к алтарю + реген
            this.heal(LEASH_REGEN_PER_SEC / 20.0F);
            this.getNavigation().moveTo(leashAnchor.x, leashAnchor.y, leashAnchor.z, 1.0);
            if (this.random.nextInt(40) == 0) {
                this.level().playSound(null, this.blockPosition(),
                        ModSounds.BOSS_STEP, SoundSource.HOSTILE, 1.1F, 0.8F);
            }
        }
    }

    // ---- фазы -----------------------------------------------------------------

    /**
     * Обновляет фазу боя по HP; на переходах — анимации и FX.
     * Фаза 1: 100-50%; фаза 2: 50-25% (ядро открыто); фаза 3: 25-0% (ярость).
     */
    private void updatePhase() {
        if (isDeadOrDying()) return;
        float healthPercent = getHealth() / getMaxHealth();
        int newPhase;
        if (healthPercent <= PHASE3_HP) {
            newPhase = 3;
        } else if (healthPercent <= PHASE2_HP) {
            newPhase = 2;
        } else {
            newPhase = 1;
        }
        if (newPhase != currentPhase) {
            currentPhase = newPhase;
            bossEvent.setName(phaseName(newPhase));
            onPhaseChanged(newPhase);
        }
    }

    private Component phaseName(int phase) {
        return switch (phase) {
            case 2 -> Component.translatable("entity.opusvsexe.haiku_omega.phase2");
            case 3 -> Component.translatable("entity.opusvsexe.haiku_omega.phase3");
            default -> Component.translatable("entity.opusvsexe.haiku_omega");
        };
    }

    private void onPhaseChanged(int phase) {
        long now = this.level().getGameTime();
        this.level().playSound(null, this.getX(), this.getY() + 10, this.getZ(),
                ModSounds.BOSS_PHASE_SHIFT, SoundSource.HOSTILE, 1.8F, 1.0F);
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                    this.getX(), this.getY() + 12.0, this.getZ(), 2, 3.0, 3.0, 3.0, 0.0);
        }
        com.opus.network.ModNetwork.sendOmegaFx((ServerLevel) this.level(), this.position(),
                phase >= 3 ? com.opus.network.ModNetwork.FX_PHASE_ENRAGE : com.opus.network.ModNetwork.FX_PHASE_OPEN,
                (float) LEASH_RADIUS);
        if (phase == 2) {
            this.coreOpenUntil = now + CORE_OPEN_TICKS;
            this.activeAttack = ActiveAttack.pose(PHASE_OPEN_ANIM, now + 30);
            for (Player p : this.level().players()) {
                if (p.distanceTo(this) < 128) {
                    p.displayClientMessage(Component.translatable("boss.summon.haiku_omega.core_open"), true);
                }
            }
        } else if (phase == 3) {
            this.activeAttack = ActiveAttack.pose(ENRAGE_ANIM, now + 40);
            // ярость: музыка заново ускоренная
            this.musicRestartTick = 0;
            for (Player p : this.level().players()) {
                if (p.distanceTo(this) < 128) {
                    p.displayClientMessage(Component.translatable("boss.summon.haiku_omega.enrage"), true);
                }
            }
        }
    }

    // ---- планировщик атак ---------------------------------------------------

    private void runAttackScheduler() {
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) return;
        long now = level().getGameTime();

        // активная атака — выполнить действие в нужный тик
        ActiveAttack atk = this.activeAttack;
        if (atk != null) {
            if (atk.kind != null && now >= atk.actionTime && !atk.done) {
                atk.done = true;
                executeAttack(atk.kind, target);
            }
            if (now > atk.animEndTick) {
                this.activeAttack = null;
            } else {
                // идём к цели в паузах (кроме залпа/реквиема — стоим)
                if (atk.kind != AttackKind.VOLLEY && atk.kind != AttackKind.REQUIEM
                        && this.distanceTo(target) > 6.0) {
                    this.getNavigation().moveTo(target, 0.6);
                }
                return;
            }
        }

        // миньоны — с фазы 1, своя интенсивность на фазу
        if (MINION_INTERVAL[currentPhase - 1] > 0 && now >= nextMinionTick) {
            nextMinionTick = now + MINION_INTERVAL[currentPhase - 1];
            summonMinions();
        }

        this.getLookControl().setLookAt(target, 25.0F, 30.0F);
        double dist = this.distanceTo(target);
        AttackKind kind = chooseAttack(target, dist);
        startAttack(kind, target);
        // между атаками идём к цели (кроме TURN/REQUIEM — стоим)
        if (kind != AttackKind.VOLLEY && kind != AttackKind.REQUIEM && dist > 6.0) {
            this.getNavigation().moveTo(target, 0.8);
        }
    }

    private AttackKind chooseAttack(LivingEntity target, double dist) {
        float r = this.random.nextFloat();
        switch (currentPhase) {
            case 1 -> {
                // первые фазы: Омега чаще бьёт орбитальными лазерами (задача 19)
                if (dist > 30) return r < 0.7 ? AttackKind.ORBITAL : AttackKind.VOLLEY;
                if (dist > 14) return r < 0.5 ? AttackKind.ORBITAL : (r < 0.75 ? AttackKind.VOLLEY : AttackKind.SLAM);
                return r < 0.4 ? AttackKind.ORBITAL : (r < 0.8 ? AttackKind.VOLLEY : AttackKind.SLAM);
            }
            case 2 -> {
                if (dist > 30) return r < 0.5 ? AttackKind.ORBITAL : (r < 0.75 ? AttackKind.VOLLEY : AttackKind.TELEPORT);
                if (dist > 14) return r < 0.3 ? AttackKind.ORBITAL : (r < 0.65 ? AttackKind.SLASH : AttackKind.SLAM);
                return r < 0.2 ? AttackKind.ORBITAL : (r < 0.5 ? AttackKind.RING : (r < 0.8 ? AttackKind.SLASH : AttackKind.MELEE));
            }
            default -> { // фаза 3
                if (dist > 30) return r < 0.45 ? AttackKind.REQUIEM : AttackKind.ORBITAL;
                if (dist > 14) return r < 0.35 ? AttackKind.SLASH : (r < 0.6 ? AttackKind.REQUIEM : AttackKind.VOLLEY);
                return r < 0.25 ? AttackKind.RING : (r < 0.5 ? AttackKind.SLASH : (r < 0.7 ? AttackKind.SLAM : AttackKind.MELEE));
            }
        }
    }

    private void startAttack(AttackKind kind, LivingEntity target) {
        long now = level().getGameTime();
        long actionTime;
        long animEnd;
        RawAnimation anim;
        switch (kind) {
            case VOLLEY -> {
                actionTime = now + 6;
                animEnd = now + 22;
                anim = VOLLEY_ANIM;
            }
            case ORBITAL -> {
                actionTime = now + 30;
                animEnd = now + 36;
                anim = ORBITAL_ANIM;
            }
            case SLASH -> {
                actionTime = now + 14;
                animEnd = now + 20;
                anim = SLASH_ANIM;
            }
            case RING -> {
                actionTime = now + 30;
                animEnd = now + 36;
                anim = RING_ANIM;
            }
            case SLAM -> {
                actionTime = now + 11;
                animEnd = now + 26;
                anim = SLAM_ANIM;
            }
            case TELEPORT -> {
                actionTime = now + 6;
                animEnd = now + 16;
                anim = TELEPORT_ANIM;
            }
            case REQUIEM -> {
                actionTime = now + 36;
                animEnd = now + 44;
                anim = REQUIEM_ANIM;
            }
            default -> { // MELEE
                actionTime = now + 10;
                animEnd = now + 22;
                anim = MELEE_ANIM;
            }
        }
        this.activeAttack = new ActiveAttack(kind, anim, actionTime, animEnd);
        // пауза до следующей атаки
        nextAttackTick = actionTime + ATTACK_PAUSE[currentPhase - 1];
    }

    private void executeAttack(AttackKind kind, LivingEntity target) {
        if (!(level() instanceof ServerLevel server)) return;
        // Разворачиваем корпус к цели — иначе LookControl крутит только голову,
        // а кулак/слэш/шоквейв считаются от yaw тела и бьют мимо (задача 15.4).
        faceTarget(target);
        switch (kind) {
            case MELEE -> {
                this.level().playSound(null, this.getX(), this.getY() + 8, this.getZ(),
                        ModSounds.BOSS_PUNCH, SoundSource.HOSTILE, 1.6F, 0.9F);
                // удар кулаком: фронт корпуса + дальность руки колосса.
                // Вектор к цели, а не getLookAngle() — последний зависит от yaw
                // тела, который у стоящего босса не обновляется (задача 15.4).
                Vec3 front = target.position().subtract(this.position()).multiply(1, 0, 1).normalize();
                double reach = 10.0D;
                for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class,
                        this.getBoundingBox().inflate(reach, 4.0, reach),
                        x -> x.isAlive() && x != this && !x.getTags().contains("omega_minion"))) {
                    Vec3 off = e.position().subtract(this.position());
                    if (off.dot(front) > 0.0 || off.length() < 5.0) {
                        e.hurt(level().damageSources().mobAttack(this), 25.0F);
                        e.knockback(0.7, -front.x, -front.z);
                    }
                }
                com.opus.network.ModNetwork.sendOmegaFx(server, this.position(),
                        com.opus.network.ModNetwork.FX_SHAKE_MINOR, 14.0F);
            }
            case VOLLEY -> fireTurretVolley(target);
            case ORBITAL -> spawnOrbitalBlast(target);
            case SLASH -> {
                OmegaSlashEntity slash = new OmegaSlashEntity(ModEntities.OMEGA_SLASH, level());
                slash.setPos(this.getX(), this.getY(), this.getZ());
                slash.setOwner(this);
                slash.aimAt(target.position());
                Vec3 dir = target.position().subtract(this.position());
                slash.centralDir = new Vec3(dir.x, 0.0D, dir.z).normalize();
                level().addFreshEntity(slash);
            }
            case RING -> {
                OmegaRingWaveEntity ring = new OmegaRingWaveEntity(ModEntities.OMEGA_RING_WAVE, level());
                ring.setPos(this.getX(), this.getY(), this.getZ());
                ring.setOwner(this);
                level().addFreshEntity(ring);
                level().playSound(null, this.getX(), this.getY() + 6, this.getZ(),
                        ModSounds.BOSS_RING_BURST, SoundSource.HOSTILE, 1.8F, 0.9F);
                com.opus.network.ModNetwork.sendOmegaFx(server, this.position(),
                        com.opus.network.ModNetwork.FX_SHAKE_MAJOR, 40.0F);
            }
            case SLAM -> {
                // slam → шок-волна у точки: ширина ×2 и высота ×4 (задача 15.2)
                float radius = currentPhase >= 3 ? 22.0F : 14.0F;
                com.opus.item.CombatEffects.shockwave(this, radius * 2.0D, radius * 4.0D, 12.0F, 1.2, false);
                level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        ModSounds.BOSS_SLAM, SoundSource.HOSTILE, 1.8F, 0.8F);
                com.opus.network.ModNetwork.sendOmegaFx(server, this.position(),
                        com.opus.network.ModNetwork.FX_SHAKE_MAJOR, radius * 2.0F);
            }
            case TELEPORT -> {
                // телепорт к цели за спину + хук
                Vec3 behind = target.position().add(
                        Mth.sin((float) Math.toRadians(target.getYRot())) * 2.5,
                        0, -Mth.cos((float) Math.toRadians(target.getYRot())) * 2.5);
                level().playSound(null, this.getX(), this.getY() + 8, this.getZ(),
                        ModSounds.BOSS_TELEPORT, SoundSource.HOSTILE, 1.5F, 1.0F);
                server.sendParticles(ParticleTypes.PORTAL, this.getX(), this.getY() + 12, this.getZ(), 60, 2, 5, 2, 1.0);
                this.teleportTo(behind.x, this.getY(), behind.z);
                level().playSound(null, this.getX(), this.getY() + 8, this.getZ(),
                        ModSounds.BOSS_TELEPORT, SoundSource.HOSTILE, 1.5F, 1.25F);
                // хук сразу
                if (target.distanceTo(this) < 6.0) {
                    target.hurt(level().damageSources().mobAttack(this), 20.0F);
                }
            }
            case REQUIEM -> spawnRequiem(target);
        }
    }

    /**
     * Поворачивает корпус босса к цели. Ванильная формула yaw, где вектор
     * движения = (-sin(yaw), 0, cos(yaw)) — совпадает с OmegaSlashEntity.aimAt.
     */
    private void faceTarget(LivingEntity target) {
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        float yaw = (float) (Math.atan2(-dx, dz) * 180.0D / Math.PI);
        this.setYRot(yaw);
        this.setYBodyRot(yaw);
        this.setYHeadRot(yaw);
    }

    // ---- конкретные атаки -----------------------------------------------------

    private void fireTurretVolley(LivingEntity target) {
        level().playSound(null, this.getX(), this.getY() + 20, this.getZ(),
                ModSounds.BOSS_TURRET_SHOT, SoundSource.HOSTILE, 1.3F, 0.8F);
        int shots = currentPhase >= 3 ? 14 : (currentPhase == 2 ? 9 : 3);
        Vec3 muzzle = new Vec3(this.getX(), this.getY() + 22.0, this.getZ());
        Vec3 baseDir = target.position().add(0, 1.0, 0).subtract(muzzle).normalize();
        int rows = currentPhase == 1 ? 1 : 3;
        for (int i = 0; i < shots; i++) {
            double angle = (i - (shots - 1) / 2.0) * 0.035;
            double rowOff = rows == 1 ? 0 : ((i % 3) - 1) * 0.8;
            Vec3 dir = new Vec3(
                    baseDir.x * Math.cos(angle) - baseDir.z * Math.sin(angle),
                    baseDir.y + rowOff * 0.05,
                    baseDir.x * Math.sin(angle) + baseDir.z * Math.cos(angle));
            OmegaShrapnelEntity shot = new OmegaShrapnelEntity(ModEntities.OMEGA_SHRAPNEL, level());
            shot.setPos(muzzle.x, muzzle.y + rowOff, muzzle.z);
            shot.setOwner(this);
            shot.shoot(dir, 1.6);
            level().addFreshEntity(shot);
        }
    }

    private void spawnOrbitalBlast(LivingEntity target) {
        level().playSound(null, target.getX(), target.getY(), target.getZ(),
                ModSounds.BOSS_ORBITAL_WARN, SoundSource.HOSTILE, 1.3F, 1.0F);
        int count = currentPhase >= 3 ? 8 : 5;
        for (int i = 0; i < count; i++) {
            double t = count == 1 ? 0 : (double) i / (count - 1);
            double ox = Mth.lerp(t, target.getX() - 14.0, target.getX() + 14.0);
            double oz = target.getZ() + (this.random.nextDouble() - 0.5) * 24.0;
            double oy = this.level().getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
                    (int) ox, (int) oz);
            OmegaSkyLaserEntity beam = new OmegaSkyLaserEntity(ModEntities.OMEGA_SKY_LASER, level());
            beam.setPos(ox, oy, oz);
            beam.setOwner(this);
            level().addFreshEntity(beam);
        }
    }

    private void spawnRequiem(LivingEntity target) {
        // Реквием: колонка ударов по всей арене вокруг босса
        level().playSound(null, this.getX(), this.getY() + 15, this.getZ(),
                ModSounds.BOSS_ORBITAL_WARN, SoundSource.HOSTILE, 1.8F, 0.7F);
        int count = 14;
        double r = LEASH_RADIUS * 0.85;
        for (int i = 0; i < count; i++) {
            double a = (Math.PI * 2 * i) / count + this.random.nextDouble() * 0.3;
            double dist = 8.0 + this.random.nextDouble() * (r - 8.0);
            double ox = this.getX() + Math.cos(a) * dist;
            double oz = this.getZ() + Math.sin(a) * dist;
            double oy = this.level().getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
                    (int) ox, (int) oz);
            OmegaSkyLaserEntity beam = new OmegaSkyLaserEntity(ModEntities.OMEGA_SKY_LASER, level());
            beam.setPos(ox, oy, oz);
            beam.setOwner(this);
            level().addFreshEntity(beam);
        }
        com.opus.network.ModNetwork.sendOmegaFx((ServerLevel) level(), this.position(),
                com.opus.network.ModNetwork.FX_REQUIEM, (float) r);
    }

    // ---- миньоны ---------------------------------------------------------------

    /** Возвращает true, если наблюдение за призывом занял слот атаки. */
    private boolean summonMinions() {
        int cap = MINION_CAP[currentPhase - 1];
        long owned = countOwnedMinions();
        if (owned >= cap) return false;
        this.activeAttack = ActiveAttack.pose(SUMMON_ANIM, this.level().getGameTime() + 26);
        int wave = currentPhase == 2 ? 4 : (currentPhase == 3 ? 3 : 2);
        int toSpawn = (int) Math.min(cap - owned, wave);
        for (int i = 0; i < toSpawn; i++) {
            double a = this.random.nextDouble() * Math.PI * 2;
            double d = 8.0 + this.random.nextDouble() * 8.0;
            double sx = this.getX() + Math.cos(a) * d;
            double sz = this.getZ() + Math.sin(a) * d;
            Mob minion;
            float roll = this.random.nextFloat();
            if (currentPhase == 1) {
                // разведка: разведчики
                minion = roll < 0.6F
                        ? new HaikuDroneEntity(ModEntities.HAIKU_DRONE, level())
                        : new Haiku2Entity(ModEntities.HAIKU_2, level());
            } else if (currentPhase == 2) {
                // тяжёлые: Вардены (элитные стражи)
                minion = new Haiku4Entity(ModEntities.HAIKU_4, level());
            } else {
                // ярость: Титаны (колоссы) + Вардены
                minion = roll < 0.5F
                        ? new Haiku5Entity(ModEntities.HAIKU_5, level())
                        : new Haiku4Entity(ModEntities.HAIKU_4, level());
            }
            minion.moveTo(sx, this.getY() + 2.0, sz, this.random.nextFloat() * 360.0F, 0.0F);
            if (minion instanceof PathfinderMob pfm && this.getTarget() != null) {
                pfm.setTarget(this.getTarget());
            }
            minion.addTag("omega_minion");
            level().addFreshEntity(minion);
            ((ServerLevel) level()).sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    sx, this.getY() + 3.0, sz, 20, 0.5, 1.5, 0.5, 0.05);
        }
        level().playSound(null, this.getX(), this.getY() + 10, this.getZ(),
                ModSounds.BOSS_TURRET_SHOT, SoundSource.HOSTILE, 1.3F, 0.5F);
        return true;
    }

    private long countOwnedMinions() {
        return this.level().getEntitiesOfClass(Mob.class,
                this.getBoundingBox().inflate(128.0D),
                e -> e.isAlive() && e.getTags().contains("omega_minion")).size();
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        // свои миньоны — союзники: AoE босса (шок-волны и т.п.) их не задевают
        if (entity != null && entity.getTags().contains("omega_minion")) {
            return true;
        }
        return super.isAlliedTo(entity);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            this.level().playSound(null, this.getX(), this.getY() + 8, this.getZ(),
                    ModSounds.BOSS_PUNCH, SoundSource.HOSTILE, 1.3F, 1.0F);
        }
        return hit;
    }

    // ---- музыка ------------------------------------------------------------------

    private void startBossMusic() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        for (ServerPlayer player : serverLevel.players()) {
            if (player.distanceToSqr(this) <= 4096.0) {
                // Сначала прерываем предыдущий экземпляр трека (например, при
                // переходе в фазу 3 ускоренный трек не должен накладываться на
                // ещё играющий неускоренный).
                player.connection.send(new ClientboundStopSoundPacket(OpusVsExe.id("doom_eternal"), SoundSource.RECORDS));
                player.playNotifySound(ModSounds.DOOM_ETERNAL, SoundSource.RECORDS, 1.0f,
                        currentPhase >= 3 ? 1.25f : 1.0f);
            }
        }
    }

    private void stopBossMusic() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        for (ServerPlayer player : serverLevel.players()) {
            player.connection.send(new ClientboundStopSoundPacket(OpusVsExe.id("doom_eternal"), SoundSource.RECORDS));
        }
    }

    // ---- смерть, дропы -------------------------------------------------------------

    @Override
    public void die(DamageSource source) {
        stopBossMusic();
        musicStarted = false;
        musicRestartTick = 0;
        // Звук падения древней машины слышен через весь Колизей
        this.level().playSound(null, this.getX(), this.getY() + 10, this.getZ(),
                ModSounds.HAIKU_OMEGA_DEATH, SoundSource.HOSTILE, 1.9F, 1.0F);
        // Разрядить оставшихся миньонов: молния (визуально, без урона) по всем
        // в радиусе 128 блоков, затем они исчезают.
        if (this.level() instanceof ServerLevel server) {
            for (Mob m : server.getEntitiesOfClass(Mob.class,
                    this.getBoundingBox().inflate(128.0D),
                    e -> e.isAlive() && e.getTags().contains("omega_minion"))) {
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(server);
                if (bolt != null) {
                    bolt.moveTo(m.getX(), m.getY(), m.getZ());
                    bolt.setVisualOnly(true);
                    server.addFreshEntity(bolt);
                }
                m.remove(Entity.RemovalReason.KILLED);
            }
        }
        super.die(source);
        this.spawnAtLocation(ModItems.CORE_OPUS.getDefaultInstance(), 2.0f);
        this.spawnAtLocation(ModItems.CORE_OPUS.getDefaultInstance(), 1.0f);
        this.spawnAtLocation(ModItems.CORE_OPUS.getDefaultInstance(), 0.25f);
        this.spawnAtLocation(ModItems.OMEGA_FRAME.getDefaultInstance(), 1.5f);
    }

    /**
     * Драконья death-анимация (как у эндер-дракона): 200 тиков (~10с) —
     * босс возносится вверх, в финале по корпусу идут взрывы, а опыт
     * выпадает порциями. Базовый tickDeath (удаление на 20-м тике) не
     * вызывается — сущность убирается только на 200-м тике.
     */
    @Override
    protected void tickDeath() {
        ++this.deathAnimTime;
        // Отключаем гравитацию — босс возносится, как дракон
        if (this.deathAnimTime == 1) {
            this.setNoGravity(true);
        }
        // Взрывы по корпусу в финале (с 9-й по 10-ю секунду) — как у дракона
        if (this.deathAnimTime >= 180 && this.deathAnimTime <= 200) {
            float f = (this.random.nextFloat() - 0.5F) * 8.0F;
            float g = (this.random.nextFloat() - 0.5F) * 4.0F;
            float h = (this.random.nextFloat() - 0.5F) * 8.0F;
            this.level().addParticle(ParticleTypes.EXPLOSION_EMITTER,
                    this.getX() + f, this.getY() + 2.0D + g, this.getZ() + h, 0.0D, 0.0D, 0.0D);
            // Звук взрыва (как у эндер-дракона) — каждые 4 тика
            if (this.deathAnimTime % 4 == 0) {
                this.level().playSound(null, this.getX() + f, this.getY() + 2.0D + g, this.getZ() + h,
                        ModSounds.BOSS_EXPLOSION, SoundSource.HOSTILE, 1.4F,
                        0.8F + this.random.nextFloat() * 0.4F);
            }
        }
        // Вознесение вверх, как дракон
        this.move(MoverType.SELF, new Vec3(0.0D, 0.1D, 0.0D));
        if (this.level().isClientSide) {
            return;
        }
        if (!(this.level() instanceof ServerLevel server)) {
            return;
        }
        boolean shouldDropLoot = this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        // Опыт порциями: каждые 5 тиков с 150-го по 200-й — по 8% от 500 XP
        if (shouldDropLoot && this.deathAnimTime > 150 && this.deathAnimTime % 5 == 0) {
            ExperienceOrb.award(server, this.position(), Mth.floor(500.0F * 0.08F));
        }
        if (this.deathAnimTime >= 200) {
            // Финальный кусок опыта + удаление
            if (shouldDropLoot) {
                ExperienceOrb.award(server, this.position(), Mth.floor(500.0F * 0.2F));
            }
            // Финальный мощный взрыв ядра (как у дракона при исчезновении)
            this.level().playSound(null, this.getX(), this.getY() + 12, this.getZ(),
                    ModSounds.BOSS_EXPLOSION, SoundSource.HOSTILE, 2.0F, 0.6F);
            server.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                    this.getX(), this.getY() + 12, this.getZ(), 3, 2.0, 2.0, 2.0, 0.0);
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(net.minecraft.world.level.gameevent.GameEvent.ENTITY_DIE);
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    // ---- сохранение ------------------------------------------------------------------

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("OmegaPhase", this.currentPhase);
        if (this.leashAnchor != null) {
            tag.putDouble("LeashX", this.leashAnchor.x);
            tag.putDouble("LeashY", this.leashAnchor.y);
            tag.putDouble("LeashZ", this.leashAnchor.z);
        }
        tag.putLong("CoreOpenTick", this.coreOpenUntil - this.level().getGameTime());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.currentPhase = Math.max(1, Math.min(3, tag.getInt("OmegaPhase")));
        if (tag.contains("LeashX")) {
            this.leashAnchor = new Vec3(tag.getDouble("LeashX"), tag.getDouble("LeashY"), tag.getDouble("LeashZ"));
        }
        this.coreOpenUntil = (int) (this.level().getGameTime() + tag.getLong("CoreOpenTick"));
        this.bossEvent.setName(phaseName(this.currentPhase));
    }

    public int getCurrentPhase() {
        return currentPhase;
    }

    /** Привязывает лейш-якорь к позиции алтаря призыва (задача 20). */
    public void setLeashAnchor(Vec3 anchor) {
        this.leashAnchor = anchor;
    }

    public boolean isCoreOpen() {
        return currentPhase == 2 && this.level().getGameTime() < this.coreOpenUntil;
    }

    // ---- вспомогательные типы ----------------------------------------------------------

    private enum AttackKind {
        MELEE, VOLLEY, ORBITAL, SLASH, RING, SLAM, TELEPORT, REQUIEM
    }

    /** Активная одноразовая атака: анимация + тик срабатывания действия. */
    private static class ActiveAttack {
        final AttackKind kind;      // null — чистая поза (переход фазы/призыв)
        final RawAnimation anim;
        final long actionTime;      // gameTime срабатывания эффекта
        final long animEndTick;     // gameTime конца анимации
        boolean done = false;

        ActiveAttack(AttackKind kind, RawAnimation anim, long actionTime, long animEndTick) {
            this.kind = kind;
            this.anim = anim;
            this.actionTime = actionTime;
            this.animEndTick = animEndTick;
        }

        static ActiveAttack pose(RawAnimation anim, long endTick) {
            return new ActiveAttack(null, anim, Long.MAX_VALUE, endTick);
        }
    }
}

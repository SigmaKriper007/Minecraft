package com.opus.darkforest.entity;

import com.opus.darkforest.DarkForestLine;
import com.opus.darkforest.blockentity.MoonFountainCoreBlockEntity;
import com.opus.darkforest.registry.DarkForestEntities;
import com.opus.darkforest.sound.DarkForestSounds;
import com.opus.sound.BossMusicHub;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public final class MossboundEndermanEntity extends Monster implements GeoEntity {
    public enum Action {
        NONE(0,0,"idle"), AWAKEN(1,32,"awaken"), SWEEP(2,46,"antler_sweep"), ROOT(3,52,"root_cast"),
        STEP(4,48,"marked_step"), ORBS(5,48,"orb_cast"), BLOOM(6,58,"bloomfall"), ECHO(7,54,"echo_double"),
        RUSH(8,52,"eclipse_rush"), PHASE(9,36,"phase_shift"), FLOWER(10,64,"flower_open"), DEATH(11,60,"death");
        final int id, duration; final String animation;
        Action(int id, int duration, String animation) { this.id=id; this.duration=duration; this.animation=animation; }
        static Action byId(int id) { for (Action action:values()) if (action.id==id) return action; return NONE; }
    }

    private static final EntityDataAccessor<Boolean> AWAKENED=SynchedEntityData.defineId(MossboundEndermanEntity.class,EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> PHASE=SynchedEntityData.defineId(MossboundEndermanEntity.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ACTION=SynchedEntityData.defineId(MossboundEndermanEntity.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ACTION_TICK=SynchedEntityData.defineId(MossboundEndermanEntity.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FLOWER_OPEN=SynchedEntityData.defineId(MossboundEndermanEntity.class,EntityDataSerializers.BOOLEAN);
    private static final DustParticleOptions CYAN=new DustParticleOptions(new Vector3f(.34F,.9F,.92F),1F);
    private static final DustParticleOptions MOSS=new DustParticleOptions(new Vector3f(.28F,.4F,.16F),1F);
    private final AnimatableInstanceCache cache=GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossBar=new ServerBossEvent(Component.translatable("entity.opusvsexe.mossbound_enderman"),BossEvent.BossBarColor.PURPLE,BossEvent.BossBarOverlay.PROGRESS);
    private BlockPos arenaAnchor;
    private UUID challengerId;
    private int attackCooldown=16, projectileTeleportCooldown, lastAttack=-1, consecutiveSweeps;
    private final java.util.Set<UUID> musicAudience=new java.util.HashSet<>();

    public MossboundEndermanEntity(EntityType<? extends Monster> type, Level level) {
        super(type,level); xpReward=650; setPersistenceRequired(); bossBar.setVisible(false); updateBossName();
    }
    @Override protected void defineSynchedData(){super.defineSynchedData();entityData.define(AWAKENED,false);entityData.define(PHASE,1);entityData.define(ACTION,0);entityData.define(ACTION_TICK,0);entityData.define(FLOWER_OPEN,false);}
    @Override protected void registerGoals(){goalSelector.addGoal(5,new WaterAvoidingRandomStrollGoal(this,.45));goalSelector.addGoal(6,new LookAtPlayerGoal(this,Player.class,36));goalSelector.addGoal(7,new RandomLookAroundGoal(this));}
    public static AttributeSupplier.Builder createAttributes(){return Mob.createMobAttributes().add(Attributes.MAX_HEALTH,480).add(Attributes.ARMOR,12).add(Attributes.ATTACK_DAMAGE,21).add(Attributes.MOVEMENT_SPEED,.8).add(Attributes.FOLLOW_RANGE,64).add(Attributes.KNOCKBACK_RESISTANCE,1);}
    @Override protected float getStandingEyeHeight(Pose pose,EntityDimensions dimensions){return 4.55F;}
    @Override public boolean removeWhenFarAway(double distance){return false;}
    public boolean isAwakened(){return entityData.get(AWAKENED);} public int getPhase(){return entityData.get(PHASE);} public Action getAction(){return Action.byId(entityData.get(ACTION));} public int getActionTick(){return entityData.get(ACTION_TICK);} public boolean isFlowerOpen(){return entityData.get(FLOWER_OPEN);}
    public void setArenaAnchor(BlockPos pos){arenaAnchor=pos.immutable();}

    @Override public boolean hurt(DamageSource source,float amount){
        if(level().isClientSide)return false;
        if(!isAwakened()){Entity attacker=source.getEntity();if(attacker instanceof LivingEntity living)awaken(living);return false;}
        if(source.is(DamageTypeTags.IS_PROJECTILE)&&projectileTeleportCooldown<=0&&tryMarkedTeleport(projectileEvadePoint(source.getEntity()))){projectileTeleportCooldown=30;return false;}
        return super.hurt(source,isFlowerOpen()?amount*1.5F:amount);
    }

    private void awaken(LivingEntity challenger){entityData.set(AWAKENED,true);challengerId=challenger.getUUID();setTarget(challenger);setAction(Action.AWAKEN);bossBar.setVisible(true);level().playSound(null,this,SoundEvents.ENDERMAN_SCREAM,SoundSource.HOSTILE,2.2F,.58F);
        if(level() instanceof ServerLevel server){BossMusicHub.startForAll(server,DarkForestSounds.ENDERMAN_THEME);for(ServerPlayer p:server.players())musicAudience.add(p.getUUID());}}
    private void setAction(Action action){entityData.set(ACTION,action.id);entityData.set(ACTION_TICK,0);entityData.set(FLOWER_OPEN,action==Action.FLOWER);navigation.stop();}

    @Override protected void customServerAiStep(){
        super.customServerAiStep();if(projectileTeleportCooldown>0)projectileTeleportCooldown--;if(!isAwakened())return;
        bossBar.setProgress(getHealth()/getMaxHealth());updatePhase();enforceArena();tickAction();tickMovement();tickMusic();
    }
    /** Battle-scoped theme: every player on the level hears it, walking away never stops it. */
    private void tickMusic(){
        if(!(level() instanceof ServerLevel server)||tickCount%40!=0)return;
        for(ServerPlayer p:server.players()) if(musicAudience.add(p.getUUID())){
            p.connection.send(new net.minecraft.network.protocol.game.ClientboundStopSoundPacket(DarkForestSounds.ENDERMAN_THEME.getLocation(),SoundSource.RECORDS));
            p.playNotifySound(DarkForestSounds.ENDERMAN_THEME,SoundSource.RECORDS,1.0F,1.0F);
        }
    }
    private void updatePhase(){int desired=getHealth()<=getMaxHealth()*.30F?3:getHealth()<=getMaxHealth()*.65F?2:1;if(desired>getPhase()){entityData.set(PHASE,desired);updateBossName();setAction(Action.PHASE);level().playSound(null,this,SoundEvents.SCULK_SHRIEKER_SHRIEK,SoundSource.HOSTILE,1.8F,desired==3?.55F:.75F);}}
    private void updateBossName(){bossBar.setName(Component.translatable("entity.opusvsexe.mossbound_enderman.phase"+getPhase()));}

    private void enforceArena(){
        if(arenaAnchor==null)arenaAnchor=blockPosition();Vec3 center=Vec3.atCenterOf(arenaAnchor);
        if(position().distanceToSqr(center)>22*22)tryMarkedTeleport(center.add(0,1,0));
        if(level() instanceof ServerLevel server&&server.getGameTime()%5==0){double angle=server.getGameTime()*.06;for(int i=0;i<4;i++){double a=angle+i*Math.PI/2;server.sendParticles(MOSS,center.x+Math.cos(a)*20,center.y+.5,center.z+Math.sin(a)*20,1,0,.25,0,0);}}
    }

    private void tickAction(){
        Action action=getAction();int tick=getActionTick();
        if(action!=Action.NONE){runAction(action,tick);tick++;entityData.set(ACTION_TICK,tick);if(tick>=action.duration){entityData.set(ACTION,0);entityData.set(ACTION_TICK,0);entityData.set(FLOWER_OPEN,false);attackCooldown=getPhase()==1?16:getPhase()==2?11:7;if(action!=Action.SWEEP)consecutiveSweeps=0;}return;}
        if(attackCooldown>0){attackCooldown--;return;}LivingEntity target=getTarget();if(target==null||!target.isAlive()){target=findTarget();setTarget(target);}if(target==null)return;setAction(selectAttack());
    }
    private LivingEntity findTarget(){if(challengerId!=null&&level() instanceof ServerLevel server&&server.getEntity(challengerId) instanceof LivingEntity living&&living.isAlive())return living;return level().getNearestPlayer(this,42);}
    private Action selectAttack(){Action[] pool=getPhase()==1?new Action[]{Action.SWEEP,Action.SWEEP,Action.ROOT,Action.STEP}:getPhase()==2?new Action[]{Action.SWEEP,Action.SWEEP,Action.SWEEP,Action.ROOT,Action.STEP,Action.ORBS,Action.BLOOM,Action.ECHO}:new Action[]{Action.SWEEP,Action.SWEEP,Action.ROOT,Action.STEP,Action.ORBS,Action.BLOOM,Action.ECHO,Action.RUSH,Action.FLOWER};int index=random.nextInt(pool.length);if(index==lastAttack&&pool[index]!=Action.SWEEP)index=(index+1)%pool.length;lastAttack=index;return pool[index];}

    private void runAction(Action action,int tick){LivingEntity target=getTarget();switch(action){
        case AWAKEN->{if(level() instanceof ServerLevel server&&tick%4==0)server.sendParticles(tick<20?MOSS:CYAN,getX(),getY()+2.5,getZ(),10,1.1,2.2,1.1,.03);}
        case SWEEP->{if(tick==22&&target!=null)sweep(target);}
        case ROOT->{if(tick==12&&target!=null)spawn(DarkForestEntities.ROOT_SNARE,target.position(),Vec3.ZERO);}
        case STEP->{if(tick==9&&target!=null)spawn(DarkForestEntities.MARKED_STEP,target.position(),Vec3.ZERO);if(tick==28&&target!=null)tryMarkedTeleport(target.position().subtract(target.getLookAngle().scale(2.5)));}
        case ORBS->{if(tick==16&&target!=null){int count=getPhase()==2?3:5;for(int i=0;i<count;i++){Vec3 from=position().add(0,3.4,0);Vec3 aim=target.getEyePosition().subtract(from).normalize();double spread=(i-(count-1)/2D)*.09;spawn(DarkForestEntities.MOONWELL_ORB,from,new Vec3(aim.x+spread,aim.y,aim.z-spread).normalize().scale(.58));}}}
        case BLOOM->{if(tick==12&&target!=null){int count=getPhase()==2?3:5;for(int i=0;i<count;i++){double a=Math.PI*2*i/count+random.nextDouble()*.35;double r=i==0?0:2.8+random.nextDouble()*3.8;spawn(DarkForestEntities.BLOOMFALL,target.position().add(Math.cos(a)*r,0,Math.sin(a)*r),Vec3.ZERO);}}}
        case ECHO->{if(tick==12&&target!=null){Vec3 side=target.position().subtract(position()).normalize().cross(new Vec3(0,1,0));spawn(DarkForestEntities.ECHO_DOUBLE,target.position().add(side.scale(3.4)),Vec3.ZERO);spawn(DarkForestEntities.ECHO_DOUBLE,target.position().subtract(side.scale(3.4)),Vec3.ZERO);}}
        case RUSH->{if(tick==8&&target!=null){Vec3 start=position();Vec3 direction=target.position().subtract(start);direction=new Vec3(direction.x,0,direction.z).normalize();spawn(DarkForestEntities.ECLIPSE_RUSH,start,direction.scale(.95));}}
        case FLOWER->{if(tick==1&&level() instanceof ServerLevel server)server.sendParticles(CYAN,getX(),getY()+5.1,getZ(),32,.7,.4,.7,.045);}
        default->{ }
    }}
    private void sweep(LivingEntity focus){boolean launcher=++consecutiveSweeps%2==0;Vec3 facing=focus.position().subtract(position());facing=new Vec3(facing.x,0,facing.z).normalize();for(LivingEntity living:level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(5,2,5),this::validTarget)){Vec3 delta=living.position().subtract(position());Vec3 flat=new Vec3(delta.x,0,delta.z);if(flat.lengthSqr()<=25&&flat.normalize().dot(facing)>.15){living.hurt(damageSources().mobAttack(this),21);double power=launcher?2.4:.8,lift=launcher?.62:.25;living.push(flat.normalize().x*power,lift,flat.normalize().z*power);if(launcher)living.hurtMarked=true;}}}
    private boolean validTarget(LivingEntity living){return living.isAlive()&&living!=this&&!(living instanceof MossboundEndermanEntity);}

    private MossboundAttackEntity spawn(EntityType<MossboundAttackEntity> type,Vec3 pos,Vec3 motion){MossboundAttackEntity effect=type.create(level());if(effect==null)throw new IllegalStateException("Unable to create Mossbound attack");effect.setPos(pos);effect.configure(this);effect.setDeltaMovement(motion);if(!level().addFreshEntity(effect))throw new IllegalStateException("Unable to add Mossbound attack");return effect;}
    private Vec3 projectileEvadePoint(Entity attacker){Vec3 center=arenaAnchor==null?position():Vec3.atCenterOf(arenaAnchor);Vec3 away=attacker==null?getLookAngle():position().subtract(attacker.position()).normalize();return center.add(away.x*8,1,away.z*8);}
    private boolean tryMarkedTeleport(Vec3 wanted){
        if(!(level() instanceof ServerLevel server))return false;Vec3 old=position();
        for(int radius=0;radius<=5;radius++)for(int dx=-radius;dx<=radius;dx++)for(int dz=-radius;dz<=radius;dz++)for(int dy=3;dy>=-3;dy--){BlockPos feet=BlockPos.containing(wanted).offset(dx,dy,dz);if(!server.getBlockState(feet.below()).isFaceSturdy(server,feet.below(),net.minecraft.core.Direction.UP))continue;AABB moved=getBoundingBox().move(feet.getX()+.5-getX(),feet.getY()-getY(),feet.getZ()+.5-getZ());if(server.noCollision(this,moved)){server.sendParticles(CYAN,old.x,old.y+2.4,old.z,18,.6,1.7,.6,.04);teleportTo(feet.getX()+.5,feet.getY(),feet.getZ()+.5);server.sendParticles(CYAN,getX(),getY()+2.4,getZ(),24,.7,1.8,.7,.05);server.playSound(null,blockPosition(),SoundEvents.ENDERMAN_TELEPORT,SoundSource.HOSTILE,1.4F,.7F);return true;}}return false;
    }
    private void tickMovement(){LivingEntity target=getTarget();if(getAction()==Action.NONE&&target!=null&&distanceToSqr(target)>20)navigation.moveTo(target,1);}
    void evaluatePhaseForQa(){updatePhase();}
    void beginActionForQa(Action action){setAction(action);}
    boolean teleportForQa(Vec3 destination){return tryMarkedTeleport(destination);}

    @Override public void die(DamageSource source){
        if(level() instanceof ServerLevel server){
            BossMusicHub.stop(server, DarkForestSounds.ENDERMAN_THEME);
            server.playSound(null,getX(),getY()+getBbHeight()*.5,getZ(),DarkForestSounds.MOSSBOUND_DEATH,SoundSource.HOSTILE,1.4F,1.0F);
        }
        setAction(Action.DEATH);if(level() instanceof ServerLevel server&&arenaAnchor!=null&&server.getBlockEntity(arenaAnchor) instanceof MoonFountainCoreBlockEntity core)core.markDefeated();super.die(source);}
    @Override protected void tickDeath(){deathTime++;if(deathTime==Action.DEATH.duration&&!level().isClientSide()&&!isRemoved()){level().broadcastEntityEvent(this,(byte)60);remove(RemovalReason.KILLED);}}
    @Override protected net.minecraft.resources.ResourceLocation getDefaultLootTable(){return DarkForestLine.id("entities/mossbound_enderman");}
    @Override public void startSeenByPlayer(ServerPlayer player){super.startSeenByPlayer(player);bossBar.addPlayer(player);}
    @Override public void stopSeenByPlayer(ServerPlayer player){super.stopSeenByPlayer(player);bossBar.removePlayer(player);}
    @Override public void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putBoolean("Awakened",isAwakened());tag.putInt("Phase",getPhase());tag.putInt("Action",getAction().id);tag.putInt("ActionTick",getActionTick());tag.putBoolean("FlowerOpen",isFlowerOpen());tag.putInt("AttackCooldown",attackCooldown);tag.putInt("ProjectileTeleportCooldown",projectileTeleportCooldown);if(arenaAnchor!=null)tag.put("ArenaAnchor",NbtUtils.writeBlockPos(arenaAnchor));if(challengerId!=null)tag.putUUID("Challenger",challengerId);}
    @Override public void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);entityData.set(AWAKENED,tag.getBoolean("Awakened"));entityData.set(PHASE,Mth.clamp(tag.getInt("Phase"),1,3));entityData.set(ACTION,tag.getInt("Action"));entityData.set(ACTION_TICK,tag.getInt("ActionTick"));entityData.set(FLOWER_OPEN,tag.getBoolean("FlowerOpen"));attackCooldown=tag.getInt("AttackCooldown");projectileTeleportCooldown=tag.getInt("ProjectileTeleportCooldown");if(tag.contains("ArenaAnchor"))arenaAnchor=NbtUtils.readBlockPos(tag.getCompound("ArenaAnchor"));if(tag.hasUUID("Challenger"))challengerId=tag.getUUID("Challenger");bossBar.setVisible(isAwakened());updateBossName();}
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers){
        controllers.add(new AnimationController<>(this,"mossbound",2,state->{
            Action action=getAction();
            String name=!isAwakened()?"dormant":action!=Action.NONE?action.animation:hurtTime>0?"hurt":state.isMoving()?"walk":"idle";
            boolean loop=!isAwakened()||(action==Action.NONE&&!name.equals("hurt"))||action==Action.FLOWER;
            state.getController().setAnimation(loop?RawAnimation.begin().thenLoop(name):RawAnimation.begin().thenPlay(name));
            return PlayState.CONTINUE;
        }));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache(){return cache;}
}

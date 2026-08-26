package com.opus.paradise.entity;

import com.opus.paradise.ParadiseLine;
import com.opus.paradise.blockentity.AngelDaisBlockEntity;
import com.opus.paradise.registry.ParadiseEntities;
import com.opus.paradise.sound.ParadiseSounds;
import com.opus.sound.BossMusicHub;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public final class AngelBoyEntity extends Monster implements GeoEntity {
    public enum Action {
        NONE(0,0,"idle_ground"), REBUFF(1,32,"awaken_rebuff"), HALO(2,56,"halo_lances"), CROSSWIND(3,72,"crosswind"),
        FEATHERS(4,44,"feather_verdict"), WINGBEAT(5,58,"wingbeat"), ASCENSION(6,64,"ascension_grip"),
        DESCENT(7,70,"ruby_descent"), PHASE(8,40,"phase_shift"), PUNCH(9,30,"punch"), DEATH(10,60,"death");
        final int id,duration; final String animation;
        Action(int id,int duration,String animation){this.id=id;this.duration=duration;this.animation=animation;}
        static Action byId(int id){for(Action a:values())if(a.id==id)return a;return NONE;}
    }
    private static final EntityDataAccessor<Boolean> AWAKENED=SynchedEntityData.defineId(AngelBoyEntity.class,EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> PHASE=SynchedEntityData.defineId(AngelBoyEntity.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ACTION=SynchedEntityData.defineId(AngelBoyEntity.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ACTION_TICK=SynchedEntityData.defineId(AngelBoyEntity.class,EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache=GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossBar=new ServerBossEvent(Component.translatable("entity.opusvsexe.angel_boy"),BossEvent.BossBarColor.YELLOW,BossEvent.BossBarOverlay.PROGRESS);
    private BlockPos arenaAnchor;
    private UUID challengerId;
    private int attackCooldown=50;
    private int lastAttack=-1;
    private final java.util.Set<UUID> musicAudience=new java.util.HashSet<>();

    public AngelBoyEntity(EntityType<? extends Monster> type,Level level){super(type,level);xpReward=500;setPersistenceRequired();bossBar.setVisible(false);updateBossName();}
    @Override protected void defineSynchedData(){super.defineSynchedData();entityData.define(AWAKENED,false);entityData.define(PHASE,1);entityData.define(ACTION,0);entityData.define(ACTION_TICK,0);}
    @Override protected void registerGoals(){goalSelector.addGoal(5,new WaterAvoidingRandomStrollGoal(this,.55));goalSelector.addGoal(6,new LookAtPlayerGoal(this,Player.class,32));goalSelector.addGoal(7,new RandomLookAroundGoal(this));}
    public static AttributeSupplier.Builder createAttributes(){return Mob.createMobAttributes().add(Attributes.MAX_HEALTH,420).add(Attributes.ARMOR,14).add(Attributes.ATTACK_DAMAGE,16).add(Attributes.MOVEMENT_SPEED,.34).add(Attributes.FLYING_SPEED,.44).add(Attributes.FOLLOW_RANGE,48).add(Attributes.KNOCKBACK_RESISTANCE,1);}
    @Override protected float getStandingEyeHeight(Pose pose,net.minecraft.world.entity.EntityDimensions dimensions){return 2.15F;}
    @Override public boolean removeWhenFarAway(double distance){return false;}
    public boolean isAwakened(){return entityData.get(AWAKENED);} public int getPhase(){return entityData.get(PHASE);} public Action getAction(){return Action.byId(entityData.get(ACTION));} public int getActionTick(){return entityData.get(ACTION_TICK);}
    public void setArenaAnchor(BlockPos pos){arenaAnchor=pos.immutable();}

    @Override public boolean hurt(DamageSource source,float amount){
        if(level().isClientSide)return false;
        if(!isAwakened()){
            Entity attacker=source.getEntity(); if(!(attacker instanceof LivingEntity living))return false;
            awaken(living); return false;
        }
        return super.hurt(source,amount);
    }

    private void awaken(LivingEntity challenger){
        entityData.set(AWAKENED,true);challengerId=challenger.getUUID();setTarget(challenger);setAction(Action.REBUFF);bossBar.setVisible(true);
        Vec3 out=challenger.position().subtract(position());out=new Vec3(out.x,0,out.z);if(out.lengthSqr()<.01)out=new Vec3(1,0,0);else out=out.normalize();
        challenger.setDeltaMovement(out.scale(2.25).add(0,.82,0));challenger.hurtMarked=true;challenger.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,120,0,false,false));
        level().playSound(null,this,SoundEvents.TOTEM_USE,SoundSource.HOSTILE,2.2F,.72F);
        BossMusicHub.startForAll((ServerLevel) level(), ParadiseSounds.ANGEL_BOY_THEME);
        musicAudience.clear();for(ServerPlayer p:((ServerLevel)level()).players())musicAudience.add(p.getUUID());
    }

    private void setAction(Action action){entityData.set(ACTION,action.id);entityData.set(ACTION_TICK,0);navigation.stop();}
    @Override protected void customServerAiStep(){super.customServerAiStep();if(!isAwakened())return;bossBar.setProgress(getHealth()/getMaxHealth());updatePhase();enforceArena();tickAction();tickMovement();tickMusic();}

    /** Battle-scoped theme: every player on the level hears it, walking away never stops it. */
    private void tickMusic(){
        if(!(level() instanceof ServerLevel server)||tickCount%40!=0)return;
        for(ServerPlayer p:server.players()) if(musicAudience.add(p.getUUID())){
            p.connection.send(new net.minecraft.network.protocol.game.ClientboundStopSoundPacket(ParadiseSounds.ANGEL_BOY_THEME.getLocation(), net.minecraft.sounds.SoundSource.RECORDS));
            p.playNotifySound(ParadiseSounds.ANGEL_BOY_THEME, net.minecraft.sounds.SoundSource.RECORDS, 1.0F, 1.0F);
        }
    }

    private void updatePhase(){int desired=getHealth()<=getMaxHealth()*.35F?3:getHealth()<=getMaxHealth()*.70F?2:1;if(desired>getPhase()){entityData.set(PHASE,desired);updateBossName();setAction(Action.PHASE);level().playSound(null,this,SoundEvents.BEACON_ACTIVATE,SoundSource.HOSTILE,2F,desired==3?.65F:.9F);}}
    private void updateBossName(){bossBar.setName(Component.translatable("entity.opusvsexe.angel_boy.phase"+getPhase()));}
    private void enforceArena(){
        if(arenaAnchor==null)arenaAnchor=blockPosition();double distance=Vec3.atCenterOf(arenaAnchor).distanceTo(position());
        if(distance>19){Vec3 back=Vec3.atCenterOf(arenaAnchor).subtract(position()).normalize();setDeltaMovement(getDeltaMovement().scale(.5).add(back.scale(.22)));heal(.5F);}
        if(level() instanceof ServerLevel server&&server.getGameTime()%4==0){double a=(server.getGameTime()%120)/120D*Math.PI*2;for(int i=0;i<4;i++){double q=a+i*Math.PI/2;server.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,arenaAnchor.getX()+.5+Math.cos(q)*17.5,arenaAnchor.getY()+1.2,arenaAnchor.getZ()+.5+Math.sin(q)*17.5,1,0,.5,0,0);}}
        for(Player p:level().getEntitiesOfClass(Player.class,new AABB(arenaAnchor).inflate(22,10,22),q->q.isAlive()&&!q.isSpectator())){Vec3 d=p.position().subtract(Vec3.atCenterOf(arenaAnchor));double h=Math.sqrt(d.x*d.x+d.z*d.z);if(h>18){Vec3 inward=new Vec3(-d.x,0,-d.z).normalize();p.push(inward.x*.18,.04,inward.z*.18);}}
    }

    private void tickAction(){
        Action action=getAction();int tick=getActionTick();
        if(action!=Action.NONE){runAction(action,tick);tick++;entityData.set(ACTION_TICK,tick);if(tick>=action.duration){entityData.set(ACTION,0);entityData.set(ACTION_TICK,0);attackCooldown=action==Action.PUNCH?14:new int[]{0,65,46,32}[getPhase()];}return;}
        if(attackCooldown>0){attackCooldown--;return;} LivingEntity target=getTarget();if(target==null||!target.isAlive()){target=findTarget();setTarget(target);}if(target==null)return;
        Action next=selectAttack();setAction(next);
    }

    private LivingEntity findTarget(){if(challengerId!=null&&level() instanceof ServerLevel s&&s.getEntity(challengerId) instanceof LivingEntity l&&l.isAlive())return l;return level().getNearestPlayer(this,32);}
    private Action selectAttack(){LivingEntity close=getTarget();if(close!=null&&close.isAlive()&&distanceToSqr(close)<20.25&&random.nextFloat()<.6F)return Action.PUNCH;Action[] pool=getPhase()==1?new Action[]{Action.HALO,Action.FEATHERS,Action.WINGBEAT,Action.CROSSWIND}:getPhase()==2?new Action[]{Action.HALO,Action.CROSSWIND,Action.FEATHERS,Action.WINGBEAT,Action.ASCENSION}:new Action[]{Action.HALO,Action.CROSSWIND,Action.FEATHERS,Action.WINGBEAT,Action.ASCENSION,Action.DESCENT};int index=random.nextInt(pool.length);if(index==lastAttack)index=(index+1)%pool.length;lastAttack=index;return pool[index];}

    private void runAction(Action action,int tick){LivingEntity target=getTarget();if(target==null)return;switch(action){
        case HALO->{if(tick==14){int count=getPhase()*2+1;for(int i=0;i<count;i++){double a=Math.PI*2*i/count;Vec3 p=target.position().add(Math.cos(a)*3.5,0,Math.sin(a)*3.5);spawn(ParadiseEntities.HALO_LANCE,p,0,0);}}}
        case CROSSWIND->{if(tick==18){Vec3 c=Vec3.atCenterOf(arenaAnchor);spawnWall(c,0);spawnWall(c,90);}}
        case FEATHERS->{if(tick==15){int count=5+getPhase()*2;Vec3 start=position().add(0,1.8,0);Vec3 base=target.getEyePosition().subtract(start).normalize();for(int i=0;i<count;i++){double spread=(i-(count-1)/2D)*.075;Vec3 dir=new Vec3(base.x+spread,base.y,base.z-spread).normalize();AngelAttackEntity e=spawn(ParadiseEntities.SERAPHIC_FEATHER,start,0,(float)((i%2==0?1:-1)*(7+getPhase()*2)));e.setDeltaMovement(dir.scale(.75));}}}
        case WINGBEAT->{if(tick==14||tick==26||tick==38)spawn(ParadiseEntities.WINGBEAT_RING,new Vec3(getX(),arenaAnchor.getY()+.2,getZ()),0,0);}
        case ASCENSION->{if(tick==18)spawn(ParadiseEntities.ANGEL_ASCENSION,target.position(),0,0);}
        case DESCENT->{if(tick==8)spawn(ParadiseEntities.RUBY_DESCENT,target.position(),0,0);if(tick==42){AngelAttackEntity marker=level().getEntitiesOfClass(AngelAttackEntity.class,getBoundingBox().inflate(32),e->e.kind()==AngelAttackEntity.Kind.DESCENT).stream().findFirst().orElse(null);if(marker!=null)teleportTo(marker.getX(),marker.getY()+.2,marker.getZ());}}
        case PUNCH->{if(tick==6||tick==16)level().playSound(null,this,SoundEvents.PLAYER_ATTACK_SWEEP,SoundSource.HOSTILE,1.6F,tick==6?1F:.88F);if(tick==10||tick==20)strikePunch();}
        default->{}
    }}

    private AngelAttackEntity spawn(EntityType<AngelAttackEntity> type,Vec3 pos,float yaw,float parameter){AngelAttackEntity e=type.create(level());if(e==null)throw new IllegalStateException("Unable to create Angel attack");e.setPos(pos);e.setYRot(yaw);e.configure(this,parameter);level().addFreshEntity(e);return e;}
    private void spawnWall(Vec3 center,float yaw){double a=Math.toRadians(yaw);Vec3 forward=new Vec3(-Math.sin(a),0,Math.cos(a));AngelAttackEntity wall=spawn(ParadiseEntities.SERAPHIC_CROSSWIND,center.subtract(forward.scale(10)),yaw,0);wall.setDeltaMovement(forward.scale(.42));}

    /** Basic one-two: heavy damage with a strong horizontal knockback launch. */
    private void strikePunch(){LivingEntity target=getTarget();Vec3 facing=target!=null?target.position().subtract(position()):getLookAngle();facing=new Vec3(facing.x,0,facing.z);if(facing.lengthSqr()<.01)facing=new Vec3(0,0,-1);else facing=facing.normalize();
        for(LivingEntity living:level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(2.6,1.6,2.6),this::isValidVictim)){Vec3 delta=living.position().subtract(position());Vec3 flat=new Vec3(delta.x,0,delta.z);double dist=flat.lengthSqr();if(dist>16)continue;Vec3 knock=dist<.01?facing:flat.normalize();if(dist>=.01&&knock.dot(facing)<.1)continue;
            living.hurt(damageSources().mobAttack(this),16F);living.setDeltaMovement(living.getDeltaMovement().scale(.4).add(knock.x*2.3,.58,knock.z*2.3));living.hurtMarked=true;}
        level().playSound(null,this,SoundEvents.PLAYER_ATTACK_CRIT,SoundSource.HOSTILE,1.4F,.8F);}
    private boolean isValidVictim(LivingEntity living){return living.isAlive()&&living!=this&&!(living instanceof AngelBoyEntity);}

    private void tickMovement(){LivingEntity target=getTarget();boolean flying=getPhase()>=2;if(flying){setNoGravity(true);if(getAction()==Action.NONE&&target!=null){Vec3 wanted=target.position().add(0,5,0).subtract(position());if(wanted.lengthSqr()>4)setDeltaMovement(getDeltaMovement().scale(.72).add(wanted.normalize().scale(.09)));}}else setNoGravity(false);if(!flying&&getAction()==Action.NONE&&target!=null&&distanceToSqr(target)>36)navigation.moveTo(target,.9);}

    @Override public void die(DamageSource source){
        setAction(Action.DEATH);bossBar.setProgress(0);level().playSound(null,this,SoundEvents.ENDER_DRAGON_DEATH,SoundSource.HOSTILE,2.4F,.78F);
        if(level() instanceof ServerLevel server) BossMusicHub.stop(server, ParadiseSounds.ANGEL_BOY_THEME);
        if(level() instanceof ServerLevel server&&arenaAnchor!=null){for(int x=-2;x<=2;x++)for(int y=-2;y<=2;y++)for(int z=-2;z<=2;z++)if(server.getBlockEntity(arenaAnchor.offset(x,y,z)) instanceof AngelDaisBlockEntity dais)dais.markDefeated();}super.die(source);}

    /** Dragon-style passing: the seraph ascends in light, bursts, then grants 12000 XP in portions. */
    @Override protected void tickDeath(){
        ++deathTime;
        if(deathTime==1)setNoGravity(true);
        setDeltaMovement(getDeltaMovement().multiply(1,0,1));
        move(net.minecraft.world.entity.MoverType.SELF,new Vec3(0,.115,0));
        if(!(level() instanceof ServerLevel server))return;
        if(deathTime<48&&deathTime%2==0){double a=deathTime*.5;for(int i=0;i<3;i++){double q=a+i*Math.PI*2/3;server.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,getX()+Math.cos(q)*1.6,getY()+random.nextDouble()*3.4,getZ()+Math.sin(q)*1.6,2,0,.06,0,.004);}}
        if(deathTime%10==0&&deathTime<=40)server.sendParticles(net.minecraft.core.particles.ParticleTypes.FLASH,getX(),getY()+1.6,getZ(),1,0,0,0,0);
        if(deathTime>=48){float fx=(random.nextFloat()-.5F)*4F,fy=(random.nextFloat()-.5F)*2.4F,fz=(random.nextFloat()-.5F)*4F;server.sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER,getX()+fx,getY()+1.6+fy,getZ()+fz,1,0,0,0,0);if(deathTime%2==0)level().playSound(null,getX()+fx,getY()+1.6+fy,getZ()+fz,SoundEvents.GENERIC_EXPLODE,SoundSource.HOSTILE,1.3F,.8F+random.nextFloat()*.4F);}
        if(level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOMOBLOOT)){
            if(deathTime>=36&&deathTime<=56&&deathTime%4==0)net.minecraft.world.entity.ExperienceOrb.award(server,position().add(0,1.2,0),500);
            if(deathTime>=60){net.minecraft.world.entity.ExperienceOrb.award(server,position().add(0,1.2,0),9000);server.sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER,getX(),getY()+2,getZ(),3,1.6,1.6,1.6,0);level().playSound(null,getX(),getY()+2,getZ(),SoundEvents.GENERIC_EXPLODE,SoundSource.HOSTILE,2F,.6F);}
        }
        if(deathTime>=60&&!isRemoved()){level().broadcastEntityEvent(this,(byte)60);remove(RemovalReason.KILLED);gameEvent(net.minecraft.world.level.gameevent.GameEvent.ENTITY_DIE);}
    }
    @Override protected void dropExperience(){}
    @Override protected net.minecraft.resources.ResourceLocation getDefaultLootTable(){return ParadiseLine.id("entities/angel_boy");}
    @Override public void startSeenByPlayer(ServerPlayer player){super.startSeenByPlayer(player);bossBar.addPlayer(player);} @Override public void stopSeenByPlayer(ServerPlayer player){super.stopSeenByPlayer(player);bossBar.removePlayer(player);}
    @Override public void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putBoolean("Awakened",isAwakened());tag.putInt("Phase",getPhase());tag.putInt("Action",getAction().id);tag.putInt("ActionTick",getActionTick());tag.putInt("AttackCooldown",attackCooldown);if(arenaAnchor!=null)tag.put("ArenaAnchor",net.minecraft.nbt.NbtUtils.writeBlockPos(arenaAnchor));if(challengerId!=null)tag.putUUID("Challenger",challengerId);}
    @Override public void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);entityData.set(AWAKENED,tag.getBoolean("Awakened"));entityData.set(PHASE,Mth.clamp(tag.getInt("Phase"),1,3));entityData.set(ACTION,tag.getInt("Action"));entityData.set(ACTION_TICK,tag.getInt("ActionTick"));attackCooldown=tag.getInt("AttackCooldown");if(tag.contains("ArenaAnchor"))arenaAnchor=net.minecraft.nbt.NbtUtils.readBlockPos(tag.getCompound("ArenaAnchor"));if(tag.hasUUID("Challenger"))challengerId=tag.getUUID("Challenger");bossBar.setVisible(isAwakened());updateBossName();}
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar c){c.add(new AnimationController<>(this,"angel",2,s->{Action a=getAction();String name=!isAwakened()?"dormant":a!=Action.NONE?a.animation:(getPhase()>=2?"idle_flight":s.isMoving()?"walk":"idle_ground");s.getController().setAnimation(a==Action.NONE?RawAnimation.begin().thenLoop(name):RawAnimation.begin().thenPlay(name));return PlayState.CONTINUE;}));}
    @Override public AnimatableInstanceCache getAnimatableInstanceCache(){return cache;}
}

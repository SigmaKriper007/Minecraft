package com.opus.darkforest.entity;

import com.opus.darkforest.registry.DarkForestEntities;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class MossboundAttackEntity extends Entity implements GeoEntity {
    public enum Kind { ROOT, STEP, ORB, BLOOM, ECHO, RUSH }
    private static final DustParticleOptions CYAN=new DustParticleOptions(new Vector3f(.32F,.88F,.94F),1F);
    private static final DustParticleOptions MOSS=new DustParticleOptions(new Vector3f(.31F,.45F,.18F),1F);
    private final AnimatableInstanceCache cache=GeckoLibUtil.createInstanceCache(this);
    private final Set<UUID> hit=new HashSet<>();
    private UUID ownerId;

    public MossboundAttackEntity(EntityType<? extends MossboundAttackEntity> type,Level level){super(type,level);noPhysics=true;setNoGravity(true);}
    public Kind kind(){if(getType()==DarkForestEntities.ROOT_SNARE)return Kind.ROOT;if(getType()==DarkForestEntities.MARKED_STEP)return Kind.STEP;if(getType()==DarkForestEntities.MOONWELL_ORB)return Kind.ORB;if(getType()==DarkForestEntities.BLOOMFALL)return Kind.BLOOM;if(getType()==DarkForestEntities.ECHO_DOUBLE)return Kind.ECHO;return Kind.RUSH;}
    public MossboundAttackEntity configure(MossboundEndermanEntity owner){ownerId=owner.getUUID();return this;}
    @Override protected void defineSynchedData(){ }
    @Override public void tick(){super.tick();if(level().isClientSide){clientVfx();return;}switch(kind()){case ROOT->tickBurst(24,34,2.5,14);case STEP->tickBurst(24,31,2.4,17);case BLOOM->tickBurst(32,42,2.3,17);case ECHO->tickBurst(22,32,2.2,16);case ORB->tickOrb();case RUSH->tickRush();}}

    private void tickBurst(int impact,int expiry,double radius,float damage){if(tickCount==impact)for(LivingEntity living:targets(radius,2.5))applyImpact(living,damage);if(tickCount>expiry)discard();}
    private void tickOrb(){Vec3 motion=getDeltaMovement();if(!level().noCollision(this,getBoundingBox().move(motion))){discard();return;}setPos(getX()+motion.x,getY()+motion.y,getZ()+motion.z);for(LivingEntity living:targets(.75,.75))if(applyImpact(living,13)){discard();break;}if(tickCount>80)discard();}
    private void tickRush(){if(tickCount>=18){Vec3 motion=getDeltaMovement();setPos(getX()+motion.x,getY()+motion.y,getZ()+motion.z);for(LivingEntity living:targets(1.35,2.4))applyImpact(living,23);}if(tickCount>42)discard();}
    private java.util.List<LivingEntity> targets(double horizontal,double vertical){return level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(horizontal,vertical,horizontal),living->living.isAlive()&&!living.getUUID().equals(ownerId)&&!(living instanceof MossboundEndermanEntity));}
    boolean applyImpact(LivingEntity living,float damage){if(!hit.add(living.getUUID()))return false;MossboundEndermanEntity owner=owner();DamageSource source=owner==null?damageSources().magic():damageSources().mobAttack(owner);living.hurt(source,damage);if(kind()==Kind.ROOT)living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,50,4,false,true));if(kind()==Kind.STEP||kind()==Kind.BLOOM)living.push(0,.35,0);return true;}
    private MossboundEndermanEntity owner(){return ownerId!=null&&level() instanceof ServerLevel server&&server.getEntity(ownerId) instanceof MossboundEndermanEntity boss?boss:null;}

    private void clientVfx(){int cadence=kind()==Kind.ORB||kind()==Kind.RUSH?1:2;if(tickCount%cadence!=0)return;double radius=switch(kind()){case ROOT->2.5;case STEP->2.4;case BLOOM->2.3;case ECHO->1.25;default->.35;};int points=kind()==Kind.ORB?2:kind()==Kind.RUSH?4:10;for(int i=0;i<points;i++){double a=Math.PI*2*i/points+tickCount*.08;double r=(kind()==Kind.ROOT||kind()==Kind.STEP||kind()==Kind.BLOOM)?radius:radius*(.7+random.nextDouble()*.3);level().addParticle((i&2)==0?CYAN:MOSS,getX()+Math.cos(a)*r,getY()+.12+(kind()==Kind.ECHO?random.nextDouble()*4:0),getZ()+Math.sin(a)*r,0,.015,0);}if((kind()==Kind.STEP&&tickCount==24)||(kind()==Kind.BLOOM&&tickCount==32))level().addParticle(ParticleTypes.FLASH,getX(),getY()+.2,getZ(),0,0,0);}

    @Override protected void addAdditionalSaveData(CompoundTag tag){if(ownerId!=null)tag.putUUID("Owner",ownerId);ListTag hits=new ListTag();for(UUID id:hit)hits.add(NbtUtils.createUUID(id));tag.put("Hits",hits);}
    @Override protected void readAdditionalSaveData(CompoundTag tag){ownerId=tag.hasUUID("Owner")?tag.getUUID("Owner"):null;hit.clear();ListTag hits=tag.getList("Hits",Tag.TAG_INT_ARRAY);for(Tag value:hits)hit.add(NbtUtils.loadUUID(value));}
    @Override public PushReaction getPistonPushReaction(){return PushReaction.IGNORE;}
    @Override public boolean isPickable(){return false;}
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers){
        controllers.add(new AnimationController<>(this,"effect",0,state->{
            String animation=switch(kind()){
                case ROOT->"root_snare";
                case STEP->"marked_step";
                case ORB->"moonwell_orb";
                case BLOOM->"bloomfall_effect";
                case ECHO->"echo_double_effect";
                case RUSH->"eclipse_rush_effect";
            };
            state.getController().setAnimation(kind()==Kind.ORB
                ?RawAnimation.begin().thenLoop(animation)
                :RawAnimation.begin().thenPlay(animation));
            return PlayState.CONTINUE;
        }));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache(){return cache;}
}

package com.opus.paradise.entity;

import com.opus.paradise.registry.ParadiseEntities;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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

public final class AngelAttackEntity extends Entity implements GeoEntity {
    public enum Kind { LANCE, CROSSWIND, FEATHER, RING, ASCENSION, DESCENT }
    private static final DustParticleOptions CYAN = new DustParticleOptions(new Vector3f(.25F, .92F, 1F), 1F);
    private static final DustParticleOptions GOLD = new DustParticleOptions(new Vector3f(1F, .76F, .2F), 1F);
    private static final DustParticleOptions RUBY = new DustParticleOptions(new Vector3f(.88F, .12F, .18F), 1.15F);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Set<UUID> hit = new HashSet<>();
    private final Set<UUID> pulled = new HashSet<>();
    private UUID ownerId;
    private float parameter;
    private boolean triggered;

    public AngelAttackEntity(EntityType<? extends AngelAttackEntity> type, Level level) {
        super(type, level); noPhysics = true; setNoGravity(true);
    }

    public Kind kind() {
        if (getType() == ParadiseEntities.HALO_LANCE) return Kind.LANCE;
        if (getType() == ParadiseEntities.SERAPHIC_CROSSWIND) return Kind.CROSSWIND;
        if (getType() == ParadiseEntities.SERAPHIC_FEATHER) return Kind.FEATHER;
        if (getType() == ParadiseEntities.WINGBEAT_RING) return Kind.RING;
        if (getType() == ParadiseEntities.ANGEL_ASCENSION) return Kind.ASCENSION;
        return Kind.DESCENT;
    }

    public AngelAttackEntity configure(AngelBoyEntity owner, float value) {
        ownerId = owner.getUUID(); parameter = value; return this;
    }

    @Override protected void defineSynchedData() { }

    @Override public void tick() {
        super.tick();
        if (level().isClientSide) { clientVfx(); return; }
        switch (kind()) {
            case LANCE -> tickLance();
            case CROSSWIND -> tickCrosswind();
            case FEATHER -> tickFeather();
            case RING -> tickRing();
            case ASCENSION -> tickAscension();
            case DESCENT -> tickDescent();
        }
    }

    private void tickLance() {
        if (tickCount == 23 || tickCount == 30) damagePlayers(new AABB(getX()-3.4,getY()-1,getZ()-3.4,getX()+3.4,getY()+10,getZ()+3.4),16F,0.3,true);
        if (tickCount > 36) discard();
    }

    private void tickCrosswind() {
        Vec3 motion = getDeltaMovement(); setPos(getX()+motion.x,getY()+motion.y,getZ()+motion.z);
        double yaw = Math.toRadians(getYRot()); Vec3 forward = new Vec3(-Math.sin(yaw),0,Math.cos(yaw));
        Vec3 side = new Vec3(forward.z,0,-forward.x);
        if (tickCount % 12 == 0) hit.clear();
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(13,4,13),this::validTarget)) {
            Vec3 d=target.position().subtract(position());
            if (Math.abs(d.dot(side))<=11 && Math.abs(d.dot(forward))<=1.6 && Math.abs(d.y)<=4.5 && hit.add(target.getUUID())) {
                target.hurt(damageSource(),13F); target.push(-forward.x*.62,.24,-forward.z*.62);
            }
        }
        if (tickCount > 48) discard();
    }

    private void tickFeather() {
        Vec3 motion=getDeltaMovement();
        if (tickCount==15) {
            double a=Math.toRadians(parameter); double x=motion.x*Math.cos(a)-motion.z*Math.sin(a); double z=motion.x*Math.sin(a)+motion.z*Math.cos(a);
            motion=new Vec3(x,motion.y,z); setDeltaMovement(motion);
        }
        setPos(getX()+motion.x,getY()+motion.y,getZ()+motion.z);
        for(LivingEntity p:level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(.7),this::validTarget)) if(hit.add(p.getUUID())) {
            p.hurt(damageSource(),12F);
            damagePlayers(getBoundingBox().inflate(2.2,1.4,2.2),8F,.3);
            discard();
        }
        if(tickCount>70) discard();
    }

    private void tickRing() {
        double radius=.35+tickCount*.43;
        for(LivingEntity p:level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(radius+1,1.6,radius+1),this::validTarget)) {
            double d=Math.sqrt(p.distanceToSqr(getX(),p.getY(),getZ()));
            if(Math.abs(d-radius)<.9 && Math.abs(p.getY()-getY())<1.5 && hit.add(p.getUUID())) {
                p.hurt(damageSource(),14F);
                Vec3 out=new Vec3(p.getX()-getX(),0,p.getZ()-getZ());
                if(out.lengthSqr()>.01) p.push(out.normalize().x*.35,.18,out.normalize().z*.35);
            }
        }
        if(tickCount>26) discard();
    }

    private void tickAscension() {
        if(tickCount>=20&&tickCount<50) for(LivingEntity p:level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(7.5,6,7.5),this::validTarget)) {
            Vec3 inward=position().subtract(p.position()); double hd=Math.sqrt(inward.x*inward.x+inward.z*inward.z); if(hd>7.5)continue;
            inward=new Vec3(inward.x,0,inward.z).normalize(); Vec3 tangent=new Vec3(-inward.z,0,inward.x);
            p.setDeltaMovement(p.getDeltaMovement().scale(.68).add(inward.scale(.08)).add(tangent.scale(.12)).add(0,.065,0)); p.hurtMarked=true; pulled.add(p.getUUID());
        }
        if(tickCount==30||tickCount==40) damagePlayers(getBoundingBox().inflate(7.5,6,7.5),6F,0,true);
        if(tickCount==50&&level() instanceof ServerLevel server) for(UUID id:Set.copyOf(pulled)) if(server.getEntity(id) instanceof LivingEntity p&&validTarget(p)) {
            Vec3 inward=position().subtract(p.position()); inward=new Vec3(inward.x,0,inward.z); if(inward.lengthSqr()<.01)inward=new Vec3(1,0,0); else inward=inward.normalize();
            p.hurt(damageSource(),14F); p.setDeltaMovement(inward.scale(.85).add(0,.55,0)); p.hurtMarked=true;
        }
        if(tickCount>60) discard();
    }

    private void tickDescent() {
        if(tickCount==35&&!triggered){triggered=true; damagePlayers(getBoundingBox().inflate(7.5,3.5,7.5),20F,.8,true);}
        if(tickCount==42) damagePlayers(getBoundingBox().inflate(8.5,4,8.5),12F,.95,true);
        if(tickCount>50) discard();
    }

    private void damagePlayers(AABB box,float damage,double push) {damagePlayers(box,damage,push,false);}
    private void damagePlayers(AABB box,float damage,double push,boolean refresh) {
        for(LivingEntity p:level().getEntitiesOfClass(LivingEntity.class,box,this::validTarget)) if(hit.add(p.getUUID())) {
            p.hurt(damageSource(),damage);
            if(refresh) p.invulnerableTime=0;
            Vec3 out=p.position().subtract(position()); out=new Vec3(out.x,0,out.z); if(out.lengthSqr()>.01)out=out.normalize();
            if(push>0) p.push(out.x*push,.25,out.z*push);
        }
    }

    private boolean validTarget(LivingEntity target) {
        return target.isAlive() && (ownerId == null || !ownerId.equals(target.getUUID()))
            && (!(target instanceof Player player) || !player.isSpectator());
    }

    private DamageSource damageSource() {
        Entity owner=ownerId!=null&&level() instanceof ServerLevel s?s.getEntity(ownerId):null;
        return owner instanceof LivingEntity living?damageSources().mobAttack(living):damageSources().magic();
    }

    private void clientVfx() {
        if(level().getNearestPlayer(this,48)==null)return;
        var particle=kind()==Kind.DESCENT?RUBY:((tickCount&3)==0?GOLD:CYAN);
        int count=kind()==Kind.FEATHER?1:2;
        for(int i=0;i<count;i++){double a=tickCount*.3+i*Math.PI;double r=.4+random.nextDouble()*Math.min(5,Math.max(1,tickCount*.08));level().addParticle(particle,getX()+Math.cos(a)*r,getY()+random.nextDouble()*2,getZ()+Math.sin(a)*r,0,.02,0);}
        if((tickCount&7)==0)level().addParticle(ParticleTypes.END_ROD,getX(),getY()+1,getZ(),0,.02,0);
    }

    @Override protected void readAdditionalSaveData(CompoundTag tag){tickCount=tag.getInt("Age");parameter=tag.getFloat("Parameter");triggered=tag.getBoolean("Triggered");if(tag.hasUUID("Owner"))ownerId=tag.getUUID("Owner");hit.clear();ListTag l=tag.getList("Hit",Tag.TAG_INT_ARRAY);for(Tag t:l)hit.add(NbtUtils.loadUUID(t));}
    @Override protected void addAdditionalSaveData(CompoundTag tag){tag.putInt("Age",tickCount);tag.putFloat("Parameter",parameter);tag.putBoolean("Triggered",triggered);if(ownerId!=null)tag.putUUID("Owner",ownerId);ListTag l=new ListTag();for(UUID id:hit)l.add(NbtUtils.createUUID(id));tag.put("Hit",l);}
    @Override public boolean isPickable(){return false;} @Override public boolean isPushable(){return false;} @Override public boolean hurt(DamageSource source,float amount){return false;} @Override public PushReaction getPistonPushReaction(){return PushReaction.IGNORE;}
    @Override public AABB getBoundingBoxForCulling(){return getBoundingBox().inflate(14);}
    private RawAnimation clip() {return RawAnimation.begin().thenLoop(switch (kind()) {
        case LANCE -> "halo_lance_active"; case CROSSWIND -> "crosswind_active"; case FEATHER -> "feather_active";
        case RING -> "ring_active"; case ASCENSION -> "ascension_active"; default -> "descent_active";
    });}
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar c){c.add(new AnimationController<>(this,"attack",0,s->{s.getController().setAnimation(clip());return PlayState.CONTINUE;}));}
    @Override public AnimatableInstanceCache getAnimatableInstanceCache(){return cache;}
}

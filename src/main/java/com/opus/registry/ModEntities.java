package com.opus.registry;

import com.opus.OpusVsExe;
import com.opus.entity.BlasterBeamEntity;
import com.opus.entity.ExplosionEntity;
import com.opus.entity.ExtraLaserBeamEntity;
import com.opus.entity.HeavyLaserBeamEntity;
import com.opus.entity.LaserEntity;
import com.opus.entity.KatanaSlashEntity;
import com.opus.entity.PunchShockwaveEntity;
import com.opus.entity.SkyLaserEntity;
import com.opus.entity.haiku.*;
import com.opus.entity.omega.OmegaRingWaveEntity;
import com.opus.entity.omega.OmegaShrapnelEntity;
import com.opus.entity.omega.OmegaSkyLaserEntity;
import com.opus.entity.omega.OmegaSlashEntity;
import com.opusvsexe.entity.custom.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {
    // Haiku mobs
    public static final EntityType<Haiku15Entity> HAIKU_1_5 = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("haiku_1_5"),
        EntityType.Builder.of(Haiku15Entity::new, MobCategory.MONSTER).sized(0.6f, 1.8f).clientTrackingRange(8).build("haiku_1_5"));

    public static final EntityType<Haiku2Entity> HAIKU_2 = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("haiku_2"),
        EntityType.Builder.of(Haiku2Entity::new, MobCategory.MONSTER).sized(0.55f, 0.8f).clientTrackingRange(10).build("haiku_2"));

    public static final EntityType<Haiku3Entity> HAIKU_3 = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("haiku_3"),
        EntityType.Builder.of(Haiku3Entity::new, MobCategory.MONSTER).sized(0.9f, 2.7f).clientTrackingRange(10).build("haiku_3"));

    public static final EntityType<Haiku4Entity> HAIKU_4 = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("haiku_4"),
        EntityType.Builder.of(Haiku4Entity::new, MobCategory.MONSTER).sized(1.4f, 4.2f).clientTrackingRange(12).build("haiku_4"));

    public static final EntityType<Haiku5Entity> HAIKU_5 = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("haiku_5"),
        EntityType.Builder.of(Haiku5Entity::new, MobCategory.MONSTER).sized(3.0f, 9.0f).clientTrackingRange(16).build("haiku_5"));

    public static final EntityType<HaikuOmegaEntity> HAIKU_OMEGA = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("haiku_omega"),
        EntityType.Builder.of(HaikuOmegaEntity::new, MobCategory.MONSTER).sized(8.3f, 25.0f).clientTrackingRange(32).build("haiku_omega"));

    // Haiku drones (airborne branch)
    public static final EntityType<HaikuDroneEntity> HAIKU_DRONE = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("haiku_drone"),
        EntityType.Builder.of(HaikuDroneEntity::new, MobCategory.MONSTER).sized(0.6f, 0.5f).clientTrackingRange(10).build("haiku_drone"));

    public static final EntityType<HaikuDronePlusEntity> HAIKU_DRONE_PLUS = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("haiku_drone_plus"),
        EntityType.Builder.of(HaikuDronePlusEntity::new, MobCategory.MONSTER).sized(1.0f, 0.9f).clientTrackingRange(12).build("haiku_drone_plus"));

    // EXO suits
    public static final EntityType<Exo1Sentinel> EXO_1_SENTINEL = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("exo_1_sentinel"),
        EntityType.Builder.of(Exo1Sentinel::new, MobCategory.MISC).sized(1.0f, 3.0f).clientTrackingRange(10).build("exo_1_sentinel"));

    public static final EntityType<Exo2Hunter> EXO_2_HUNTER = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("exo_2_hunter"),
        EntityType.Builder.of(Exo2Hunter::new, MobCategory.MISC).sized(1.2f, 3.6f).clientTrackingRange(10).build("exo_2_hunter"));

    public static final EntityType<Exo3Vanguard> EXO_3_VANGUARD = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("exo_3_vanguard"),
        EntityType.Builder.of(Exo3Vanguard::new, MobCategory.MISC).sized(1.5f, 4.5f).clientTrackingRange(12).build("exo_3_vanguard"));

    public static final EntityType<Exo4Titan> EXO_4_TITAN = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("exo_4_titan"),
        EntityType.Builder.of(Exo4Titan::new, MobCategory.MISC).sized(2.0f, 6.0f).clientTrackingRange(14).build("exo_4_titan"));

    public static final EntityType<Exo5Vengeance> EXO_5_VENGEANCE = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("exo_5_vengeance"),
        EntityType.Builder.of(Exo5Vengeance::new, MobCategory.MISC).sized(3.0f, 9.0f).clientTrackingRange(16).build("exo_5_vengeance"));

    // Laser beam
    public static final EntityType<LaserEntity> LASER = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("laser"),
        EntityType.Builder.of(LaserEntity::new, MobCategory.MISC).sized(1.0f, 1.0f).clientTrackingRange(32).build("laser"));

    // Sky laser beam (huge beam from the sky)
    public static final EntityType<SkyLaserEntity> SKY_LASER = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("sky_laser"),
        EntityType.Builder.of(SkyLaserEntity::new, MobCategory.MISC).sized(1.0f, 1.0f).clientTrackingRange(32).build("sky_laser"));

    // Explosion
    public static final EntityType<ExplosionEntity> EXPLOSION = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("explosion"),
        EntityType.Builder.of(ExplosionEntity::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(32).build("explosion"));

    // Blaster beam
    public static final EntityType<BlasterBeamEntity> BLASTER_BEAM = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("blaster_beam"),
        EntityType.Builder.of(BlasterBeamEntity::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(32).build("blaster_beam"));

    // Heavy blaster beam (plasma lance, own model)
    public static final EntityType<HeavyLaserBeamEntity> HEAVY_BLASTER_BEAM = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("heavy_blaster_beam"),
        EntityType.Builder.of(HeavyLaserBeamEntity::new, MobCategory.MISC).sized(1.0f, 1.0f).clientTrackingRange(32).build("heavy_blaster_beam"));

    // Extra laser (wide 2.5x beam that explodes blocks like TNT)
    public static final EntityType<ExtraLaserBeamEntity> EXTRA_LASER_BEAM = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("extra_laser_beam"),
        EntityType.Builder.of(ExtraLaserBeamEntity::new, MobCategory.MISC).sized(1.0f, 1.0f).clientTrackingRange(32).build("extra_laser_beam"));

    public static final EntityType<KatanaSlashEntity> KATANA_SLASH = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("katana_slash"),
        EntityType.Builder.<KatanaSlashEntity>of(KatanaSlashEntity::new, MobCategory.MISC)
            .sized(1.2F, 2.2F).clientTrackingRange(32).updateInterval(1).build("katana_slash"));

    public static final EntityType<PunchShockwaveEntity> PUNCH_SHOCKWAVE = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("punch_shockwave"),
        EntityType.Builder.<PunchShockwaveEntity>of(PunchShockwaveEntity::new, MobCategory.MISC)
            .sized(1.0F, 1.0F).clientTrackingRange(32).updateInterval(1).build("punch_shockwave"));

    // Боёвые снаряды финального босса Омеги (задача 13)
    public static final EntityType<OmegaShrapnelEntity> OMEGA_SHRAPNEL = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("omega_shrapnel"),
        EntityType.Builder.of(OmegaShrapnelEntity::new, MobCategory.MISC).sized(0.7f, 0.7f).clientTrackingRange(16).build("omega_shrapnel"));

    public static final EntityType<OmegaRingWaveEntity> OMEGA_RING_WAVE = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("omega_ring_wave"),
        EntityType.Builder.of(OmegaRingWaveEntity::new, MobCategory.MISC).sized(1.0f, 1.5f).clientTrackingRange(48).build("omega_ring_wave"));

    public static final EntityType<OmegaSlashEntity> OMEGA_SLASH = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("omega_slash"),
        EntityType.Builder.of(OmegaSlashEntity::new, MobCategory.MISC).sized(5.5f, 3.5f).clientTrackingRange(32).build("omega_slash"));

    public static final EntityType<OmegaSkyLaserEntity> OMEGA_SKY_LASER = Registry.register(BuiltInRegistries.ENTITY_TYPE, OpusVsExe.id("omega_sky_laser"),
        EntityType.Builder.of(OmegaSkyLaserEntity::new, MobCategory.MISC).sized(4.0f, 1.0f).clientTrackingRange(48).build("omega_sky_laser"));

    public static void init() {
    }
}

package com.opus.registry;

import com.opus.OpusVsExe;
import com.opus.entity.haiku.*;
import com.opusvsexe.entity.custom.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, OpusVsExe.MOD_ID);
    
    // Haiku mobs
    public static final RegistryObject<EntityType<Haiku15Entity>> HAIKU_1_5 = ENTITIES.register("haiku_1_5",
        () -> EntityType.Builder.of(Haiku15Entity::new, MobCategory.MONSTER).sized(0.6f, 1.7f).clientTrackingRange(8).build("haiku_1_5"));
    
    public static final RegistryObject<EntityType<Haiku2Entity>> HAIKU_2 = ENTITIES.register("haiku_2",
        () -> EntityType.Builder.of(Haiku2Entity::new, MobCategory.MONSTER).sized(0.5f, 0.8f).clientTrackingRange(10).build("haiku_2"));
    
    public static final RegistryObject<EntityType<Haiku3Entity>> HAIKU_3 = ENTITIES.register("haiku_3",
        () -> EntityType.Builder.of(Haiku3Entity::new, MobCategory.MONSTER).sized(0.7f, 2.8f).clientTrackingRange(10).build("haiku_3"));
    
    public static final RegistryObject<EntityType<Haiku4Entity>> HAIKU_4 = ENTITIES.register("haiku_4",
        () -> EntityType.Builder.of(Haiku4Entity::new, MobCategory.MONSTER).sized(0.9f, 4.3f).clientTrackingRange(12).build("haiku_4"));
    
    public static final RegistryObject<EntityType<Haiku5Entity>> HAIKU_5 = ENTITIES.register("haiku_5",
        () -> EntityType.Builder.of(Haiku5Entity::new, MobCategory.MONSTER).sized(1.5f, 9.0f).clientTrackingRange(16).build("haiku_5"));
    
    public static final RegistryObject<EntityType<HaikuOmegaEntity>> HAIKU_OMEGA = ENTITIES.register("haiku_omega",
        () -> EntityType.Builder.of(HaikuOmegaEntity::new, MobCategory.MONSTER).sized(3.0f, 25.0f).clientTrackingRange(32).build("haiku_omega"));
    
    // EXO suits
    public static final RegistryObject<EntityType<Exo1Sentinel>> EXO_1_SENTINEL = ENTITIES.register("exo_1_sentinel",
        () -> EntityType.Builder.of(Exo1Sentinel::new, MobCategory.MISC).sized(1.2f, 3.0f).clientTrackingRange(10).build("exo_1_sentinel"));
    
    public static final RegistryObject<EntityType<Exo2Hunter>> EXO_2_HUNTER = ENTITIES.register("exo_2_hunter",
        () -> EntityType.Builder.of(Exo2Hunter::new, MobCategory.MISC).sized(1.3f, 3.5f).clientTrackingRange(10).build("exo_2_hunter"));
    
    public static final RegistryObject<EntityType<Exo3Vanguard>> EXO_3_VANGUARD = ENTITIES.register("exo_3_vanguard",
        () -> EntityType.Builder.of(Exo3Vanguard::new, MobCategory.MISC).sized(1.5f, 4.5f).clientTrackingRange(12).build("exo_3_vanguard"));
    
    public static final RegistryObject<EntityType<Exo4Titan>> EXO_4_TITAN = ENTITIES.register("exo_4_titan",
        () -> EntityType.Builder.of(Exo4Titan::new, MobCategory.MISC).sized(2.0f, 6.0f).clientTrackingRange(14).build("exo_4_titan"));
    
    public static final RegistryObject<EntityType<Exo5Vengeance>> EXO_5_VENGEANCE = ENTITIES.register("exo_5_vengeance",
        () -> EntityType.Builder.of(Exo5Vengeance::new, MobCategory.MISC).sized(2.5f, 8.0f).clientTrackingRange(16).build("exo_5_vengeance"));
}

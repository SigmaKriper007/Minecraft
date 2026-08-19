package com.opus.registry;

import com.opus.OpusVsExe;
import com.opus.block.AltarHeartBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModEffects {
    
    public static final MobEffect FLASH_BLINDNESS = register("flash_blindness", 
        new com.opus.item.FlashBlindnessEffect());
    
    private static MobEffect register(String name, MobEffect effect) {
        return Registry.register(BuiltInRegistries.MOB_EFFECT, OpusVsExe.id(name), effect);
    }
    
    public static void init() {
    }
}

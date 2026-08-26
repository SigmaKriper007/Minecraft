package com.opus.mixin;

import com.opus.darkforest.DarkForestLine;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Replaces only the positive-weirdness half of vanilla Dark Forest climate cells. */
@Mixin(OverworldBiomeBuilder.class)
public abstract class DarkForestBiomeBuilderMixin {
    @Inject(method="pickMiddleBiome",at=@At("RETURN"),cancellable=true)
    private void opusvsexe$splitMiddleDarkForest(int temperature,int humidity,Climate.Parameter weirdness,CallbackInfoReturnable<ResourceKey<Biome>> cir){replacePositiveDarkForest(weirdness,cir);}
    @Inject(method="pickPlateauBiome",at=@At("RETURN"),cancellable=true)
    private void opusvsexe$splitPlateauDarkForest(int temperature,int humidity,Climate.Parameter weirdness,CallbackInfoReturnable<ResourceKey<Biome>> cir){replacePositiveDarkForest(weirdness,cir);}
    private static void replacePositiveDarkForest(Climate.Parameter weirdness,CallbackInfoReturnable<ResourceKey<Biome>> cir){if(cir.getReturnValue()==Biomes.DARK_FOREST&&weirdness.max()>=0)cir.setReturnValue(DarkForestLine.DARK_FOREST);}
}

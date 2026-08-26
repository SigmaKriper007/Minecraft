package com.opus.client.mixin;

import com.opus.client.CrimsonMoonClient;
import com.opus.darkforest.client.DarkForestClient;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * «Кроваво-красный» градиент неба. Перехватывает уже вычисленный ванильный
 * цвет неба (LevelRenderer.renderSky получает vec3 из ClientLevel.getSkyColor)
 * и плавно линeriesает его к кровавым точкам, пока активна кровавая луна.
 */
@Mixin(LevelRenderer.class)
public abstract class CrimsonSkyMixin {

    @ModifyVariable(method = "renderSky", at = @At(value = "STORE"), ordinal = 0)
    private Vec3 opusvsexe$bloodSkyColor(Vec3 original) {
        return DarkForestClient.tintSky(CrimsonMoonClient.tintSky(original));
    }
}

package com.opus.client.mixin;

import com.opus.client.CrimsonMoonClient;
import com.opus.darkforest.client.DarkForestClient;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * «Кровавый туман» (задача 2026-08-22): цвет тумана в ночи окрашивается в
 * приглушённо-красный, пока активна кровавая луна. Туман остаётся лёгким —
 * меняется только цвет, не густота.
 */
@Mixin(FogRenderer.class)
public abstract class CrimsonFogMixin {

    @Accessor("fogRed") private static float opusvsexe_fogRed() { throw new AssertionError(); }
    @Accessor("fogRed") private static void opusvsexe_fogRed(float value) { throw new AssertionError(); }
    @Accessor("fogGreen") private static float opusvsexe_fogGreen() { throw new AssertionError(); }
    @Accessor("fogGreen") private static void opusvsexe_fogGreen(float value) { throw new AssertionError(); }
    @Accessor("fogBlue") private static float opusvsexe_fogBlue() { throw new AssertionError(); }
    @Accessor("fogBlue") private static void opusvsexe_fogBlue(float value) { throw new AssertionError(); }

    @Inject(method = "setupColor", at = @At("TAIL"))
    private static void opusvsexe$bloodFogColor(Camera camera, float partialTick, ClientLevel level,
                                                int renderDistance, float darknessFactor, CallbackInfo ci) {
        Vec3 moonFog=DarkForestClient.tintFog(new Vec3(opusvsexe_fogRed(),opusvsexe_fogGreen(),opusvsexe_fogBlue()));
        opusvsexe_fogRed((float)moonFog.x);opusvsexe_fogGreen((float)moonFog.y);opusvsexe_fogBlue((float)moonFog.z);
        float s = CrimsonMoonClient.strength();
        if (s <= 0.0F) return;
        Vec3 target = CrimsonMoonClient.bloodFogColor();
        opusvsexe_fogRed(mixTo(opusvsexe_fogRed(), (float) target.x, s));
        opusvsexe_fogGreen(mixTo(opusvsexe_fogGreen(), (float) target.y, s));
        opusvsexe_fogBlue(mixTo(opusvsexe_fogBlue(), (float) target.z, s));
    }

    private static float mixTo(float current, float target, float strength) {
        return current + (target - current) * strength;
    }
}

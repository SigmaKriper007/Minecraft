package com.opus.fire.client.particle;

import com.opus.fire.registry.FireParticles;
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/**
 * Частица «эмбер» — тлеющий уголёк: поднимается вверх, мерцает, медленно гаснет.
 * Палитра эссенции (#FF5A00 → #8A2E00), белое ядро только в центре.
 */
public class EmberParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private final RandomSource random;

    protected EmberParticle(ClientLevel level, double x, double y, double z,
                            double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = sprites;
        this.random = level.random;
        this.xd = vx + (random.nextDouble() - 0.5) * 0.02;
        this.yd = vy + 0.03 + random.nextDouble() * 0.03;
        this.zd = vz + (random.nextDouble() - 0.5) * 0.02;
        this.lifetime = 40 + random.nextInt(30);
        this.quadSize = 0.10f + random.nextFloat() * 0.08f;
        this.gravity = -0.01f;
        this.alpha = 0.9f;
        pickSprite();
    }

    private void pickSprite() {
        this.setSprite(sprites.get(random));
    }

    @Override
    public void tick() {
        super.tick();
        // Мерцание: слегка меняем размер/яркость
        if (random.nextInt(6) == 0) {
            this.quadSize *= random.nextBoolean() ? 1.15f : 0.85f;
            this.quadSize = Math.max(0.04f, Math.min(0.24f, this.quadSize));
        }
        if (this.age > this.lifetime - 20) {
            this.alpha = Math.max(0.0f, this.alpha - 0.05f);
        }
        // Уголёк то вспыхивает, то гаснет
        this.setColor(1.0f, 0.45f + 0.2f * (float) Math.sin(this.age * 0.4), 0.1f);
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new EmberParticle(level, x, y, z, vx, vy, vz, sprites);
        }
    }
}

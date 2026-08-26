package com.opus.fire.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/**
 * Частица «пепел» — серо-коричневый хлопок, медленно падает и покачивается.
 */
public class AshParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private final RandomSource random;

    protected AshParticle(ClientLevel level, double x, double y, double z,
                          double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = sprites;
        this.random = level.random;
        this.xd = vx + (random.nextDouble() - 0.5) * 0.03;
        this.yd = vy - 0.01 - random.nextDouble() * 0.015;
        this.zd = vz + (random.nextDouble() - 0.5) * 0.03;
        this.lifetime = 60 + random.nextInt(50);
        this.quadSize = 0.08f + random.nextFloat() * 0.06f;
        this.gravity = -0.005f;
        this.alpha = 0.75f;
        this.setSprite(sprites.get(random));
    }

    @Override
    public void tick() {
        super.tick();
        // Покачивание
        this.xd += (random.nextDouble() - 0.5) * 0.005;
        this.zd += (random.nextDouble() - 0.5) * 0.005;
        if (this.age > this.lifetime - 25) {
            this.alpha = Math.max(0.0f, this.alpha - 0.03f);
        }
        this.setColor(0.35f, 0.3f, 0.3f);
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
            return new AshParticle(level, x, y, z, vx, vy, vz, sprites);
        }
    }
}

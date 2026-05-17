package mrkartoshki.rawlands.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;

@Environment(EnvType.CLIENT)
public class BioluminescentAlgaeParticle extends SingleQuadParticle {

    protected BioluminescentAlgaeParticle(ClientLevel level, double x, double y, double z,
                                           TextureAtlasSprite sprite, RandomSource random) {
        super(level, x, y, z, sprite);
        this.lifetime = 60 + random.nextInt(60);
        this.xd = (random.nextDouble() - 0.5) * 0.008;
        this.yd = 0.008 + random.nextDouble() * 0.008;
        this.zd = (random.nextDouble() - 0.5) * 0.008;
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public int getLightCoords(float partialTick) {
        return LightCoordsUtil.withBlock(super.getLightCoords(partialTick), 15);
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Factory(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                        double x, double y, double z,
                                        double vx, double vy, double vz, RandomSource random) {
            return new BioluminescentAlgaeParticle(level, x, y, z, sprites.first(), random);
        }
    }
}

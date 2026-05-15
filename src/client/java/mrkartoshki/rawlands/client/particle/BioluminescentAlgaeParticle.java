package mrkartoshki.rawlands.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

@Environment(EnvType.CLIENT)
public class BioluminescentAlgaeParticle extends Particle {

    protected BioluminescentAlgaeParticle(ClientLevel level, double x, double y, double z,
                                           RandomSource random) {
        super(level, x, y, z);
        this.lifetime = 60 + random.nextInt(60);
        this.xd = (random.nextDouble() - 0.5) * 0.008;
        this.yd = 0.008 + random.nextDouble() * 0.008;
        this.zd = (random.nextDouble() - 0.5) * 0.008;
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.SINGLE_QUADS;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Factory(SpriteSet sprites) {
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                        double x, double y, double z,
                                        double vx, double vy, double vz, RandomSource random) {
            return new BioluminescentAlgaeParticle(level, x, y, z, random);
        }
    }
}

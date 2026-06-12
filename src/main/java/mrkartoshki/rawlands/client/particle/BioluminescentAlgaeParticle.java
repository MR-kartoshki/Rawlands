package mrkartoshki.rawlands.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;

@Environment(EnvType.CLIENT)
public class BioluminescentAlgaeParticle extends SingleQuadParticle {

    // driftX/driftZ: shared group direction; each particle adds small random variation on top.
    protected BioluminescentAlgaeParticle(ClientLevel level, double x, double y, double z,
                                           double driftX, double driftZ,
                                           TextureAtlasSprite sprite, RandomSource random) {
        super(level, x, y, z, sprite);
        this.lifetime = 60 + random.nextInt(60);
        this.xd = driftX + (random.nextDouble() - 0.5) * 0.004;
        this.yd = 0.003 + random.nextDouble() * 0.005;
        this.zd = driftZ + (random.nextDouble() - 0.5) * 0.004;
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    //getLightCoords doesnt really appear to exist in 1.21.11
    //correct if necessary

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
            if (y >= 60 || !level.getFluidState(BlockPos.containing(x, y, z)).is(FluidTags.WATER)) return null;
            // Linear fade: full rate below y=40, zero at y=60.
            if (y > 40 && random.nextDouble() > (60.0 - y) / 20.0) return null;

            // vx/vz carry the shared group direction on recursive (extra) calls; 0,0 means initial ambient call.
            boolean isExtra = vx != 0.0 || vz != 0.0;
            double driftX = isExtra ? vx : Math.cos(random.nextDouble() * Math.PI * 2) * 0.012;
            double driftZ = isExtra ? vz : Math.sin(random.nextDouble() * Math.PI * 2) * 0.012;

            // Only the initial call spawns extras — extras never spawn more extras, preventing recursion.
            if (!isExtra) {
                int extras = 2 + random.nextInt(3);
                for (int i = 0; i < extras; i++) {
                    double ox = x + (random.nextDouble() - 0.5) * 3.0;
                    double oy = y + (random.nextDouble() - 0.5) * 1.5;
                    double oz = z + (random.nextDouble() - 0.5) * 3.0;
                    level.addParticle(type, ox, oy, oz, driftX, 0.0, driftZ);
                }
            }

            return new BioluminescentAlgaeParticle(level, x, y, z, driftX, driftZ, sprites.first(), random);
        }
    }
}

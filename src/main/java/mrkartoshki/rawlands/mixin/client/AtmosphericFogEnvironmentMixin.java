package mrkartoshki.rawlands.mixin.client;

import mrkartoshki.rawlands.world.biome.ModBiomes;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.AtmosphericFogEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin (AtmosphericFogEnvironment.class)
public abstract class AtmosphericFogEnvironmentMixin {
    @Unique
    private static final Map<ResourceKey<Biome>, Float> sd =new HashMap<>();
    @Unique
    private static final Map<ResourceKey<Biome>,Float>cd = new HashMap<>();
    @Unique
    private int t = 0;
    @Inject(method = "setupFog", at = @At("TAIL"))
    public void setupFog(FogData fd, Camera c, ClientLevel cl, float f, DeltaTracker d, CallbackInfo ci) {
        t++;
        biomeFog(ModBiomes.DEAD_FOREST, c, fd, cl, 20,30, 60, 100,false);
        biomeFog(ModBiomes.MIST_COAST, c, fd, cl, 10,20, 60, 90, false);
    }

    /**
     * Configures rich fog environments for a given biome
     * @param biome a ResourceKey for the biome
     * @param c camera to be used as the point of reference
     * @param fd fog data to be modified
     * @param cl client level for accessing nearby blocks
     * @param r layers of scanning
     * @param e minimum view distance in blocks
     *
     * <br> Optional for vertical dynamics
     * @param yb bottom fog boundary
     * @param yt top fog boundary
     * @param i invert scanning; <br>false = thicker near the bottom<br>true = thicker near the top
     */
    @Unique
    private void biomeFog(ResourceKey<Biome> biome, Camera c, FogData fd, ClientLevel cl, int r, int e, @Nullable Integer yb, @Nullable Integer yt, @Nullable Boolean i) {

        if (cl.getBiome(c.blockPosition()).is(biome)) {
            if (t >= 10) { t = 0;
                cd.put(biome, 0f);
                if (c.entity().level().getBiome(c.blockPosition()).is(biome)) {
                    float xz = (float) checkXZ(biome, cl, c, r) / (r * 8 + 1);
                    float y = 1;
                    if (yb != null && yt != null) {
                        y = Math.min(checkY(c, yb, yt, i), 1);
                    }
                    cd.put(biome, xz * y);
                }


            }
        } else {
            sd.put(biome, Mth.lerp(0.05f, sd.getOrDefault(biome, 0f), 0f));

        }
        sd.put(biome, Mth.lerp(0.05f, sd.getOrDefault(biome, 0f), cd.getOrDefault(biome, 0f)));
        float s = sd.getOrDefault(biome, 0f);
        if (s > 0.001f) {
            fd.environmentalStart = Mth.lerp(s, fd.environmentalStart, 0f);
            fd.environmentalEnd = Mth.lerp(s, fd.environmentalEnd, e);
        }
    }

    /**
     * Checks lateral area for biome depth.<br>It scans in 8 lines from the camera (N,E,S,W,NE,SE,NW,SW), each of which is {@code layers} blocks long.
     * @param biome biome to look for
     * @param clientLevel client level for block scanning
     * @param camera camera for point of reference
     * @param layers how many passes to do
     * @return Amount of blocks found to be within the specified biome. Divide by {@code (layers*8) + 1} to get a ratio.
     */
    @Unique
    private int checkXZ(ResourceKey<Biome> biome, ClientLevel clientLevel, Camera camera, int layers) {
        int count = 0;

        BlockPos camPos = camera.blockPosition();
        if (clientLevel.getBiome(camPos).is(biome)) count++;
        for (int i = 1; i <= layers; i++) {
            if (clientLevel.getBiome(camPos.north(i)).is(biome)) count++;
            if (clientLevel.getBiome(camPos.east(i)).is(biome)) count++;
            if (clientLevel.getBiome(camPos.south(i)).is(biome)) count++;
            if (clientLevel.getBiome(camPos.west(i)).is(biome)) count++;
            if (clientLevel.getBiome(camPos.north(i).east(i)).is(biome)) count++;
            if (clientLevel.getBiome(camPos.south(i).east(i)).is(biome)) count++;
            if (clientLevel.getBiome(camPos.south(i).west(i)).is(biome)) count++;
            if (clientLevel.getBiome(camPos.north(i).west(i)).is(biome)) count++;
        }
        return count;
    }

    /**
     * Checks vertical depth
     * @param camera camera for point of reference
     * @param yb bottom boundary
     * @param yt upper boundary
     * @param i invert (true=thicker near top, false=thicker near bottom)
     * @return ratio for multiplying fog or similar effects, 1f = full potency, 0f = disabled
     */
    @Unique
    private float checkY(Camera camera, int yb, int yt, boolean i) {
        float y = (float) camera.position().y;
        float r = 0;
        if (i) {
            if (y > yb && y < yt) {
                float t = (float) (y - yb) / (yt - yb);
                r = (float) Math.pow(t, 3);
            }
            if (y >= yt) {
                r = 1;
            }
        } else {
            if (y > yb && y < yt) {
                float t = (float) (y - yb) / (yt - yb);
                r =1- (float) Math.pow(t, 3);
            }
            if (y <= yb) {
                r = 1;
            }
        }

        return r;
    }


}

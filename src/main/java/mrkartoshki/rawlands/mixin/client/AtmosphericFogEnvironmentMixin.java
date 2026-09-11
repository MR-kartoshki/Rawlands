package mrkartoshki.rawlands.mixin.client;

import mrkartoshki.rawlands.client.fog.BiomeFogState;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(AtmosphericFogEnvironment.class)
public abstract class AtmosphericFogEnvironmentMixin {
    @Unique
    private final Map<ResourceKey<Biome>, BiomeFogState> rawlands$fogStates = new HashMap<>();

    @Unique
    private int rawlands$sampleTicks;

    @Inject(method = "setupFog", at = @At("TAIL"))
    private void rawlands$setupFog(
        FogData fogData,
        Camera camera,
        ClientLevel level,
        float renderDistance,
        DeltaTracker deltaTracker,
        CallbackInfo callbackInfo
    ) {
        boolean sample = ++rawlands$sampleTicks >= 10;
        if (sample) {
            rawlands$sampleTicks = 0;
        }

        rawlands$applyBiomeFog(ModBiomes.DEAD_FOREST, camera, fogData, level, 20, 30, 60, 100, sample);
        rawlands$applyBiomeFog(ModBiomes.MIST_COAST, camera, fogData, level, 10, 20, 60, 90, sample);
        rawlands$applyBiomeFog(ModBiomes.ALPS, camera, fogData, level, 15, 50, 140, 200, sample);
        rawlands$applyBiomeFog(ModBiomes.FJORDS, camera, fogData, level, 12, 30, 55, 95, sample);
        rawlands$applyBiomeFog(ModBiomes.SEQUOIA_FOREST, camera, fogData, level, 12, 25, 40, 100, sample);
    }

    @Unique
    private void rawlands$applyBiomeFog(
        ResourceKey<Biome> biome,
        Camera camera,
        FogData fogData,
        ClientLevel level,
        int radius,
        int fogEnd,
        int lowerBoundary,
        int upperBoundary,
        boolean sample
    ) {
        BiomeFogState state = rawlands$fogStates.computeIfAbsent(biome, ignored -> new BiomeFogState(0.05f));
        boolean insideBiome = level.getBiome(camera.blockPosition()).is(biome);

        if (!insideBiome) {
            state.setTarget(0.0f);
        } else if (sample) {
            float horizontalDepth = (float) rawlands$countBiomeSamples(biome, level, camera, radius) / (radius * 8 + 1);
            state.setTarget(horizontalDepth * rawlands$verticalDepth(camera, lowerBoundary, upperBoundary));
        }

        float strength = state.tick();
        if (strength > 0.001f) {
            fogData.environmentalStart = Mth.lerp(strength, fogData.environmentalStart, 0.0f);
            fogData.environmentalEnd = Mth.lerp(strength, fogData.environmentalEnd, fogEnd);
        }
    }

    @Unique
    private int rawlands$countBiomeSamples(
        ResourceKey<Biome> biome,
        ClientLevel level,
        Camera camera,
        int radius
    ) {
        int count = 0;
        BlockPos cameraPos = camera.blockPosition();

        if (level.getBiome(cameraPos).is(biome)) {
            count++;
        }
        for (int distance = 1; distance <= radius; distance++) {
            if (level.getBiome(cameraPos.north(distance)).is(biome)) count++;
            if (level.getBiome(cameraPos.east(distance)).is(biome)) count++;
            if (level.getBiome(cameraPos.south(distance)).is(biome)) count++;
            if (level.getBiome(cameraPos.west(distance)).is(biome)) count++;
            if (level.getBiome(cameraPos.north(distance).east(distance)).is(biome)) count++;
            if (level.getBiome(cameraPos.south(distance).east(distance)).is(biome)) count++;
            if (level.getBiome(cameraPos.south(distance).west(distance)).is(biome)) count++;
            if (level.getBiome(cameraPos.north(distance).west(distance)).is(biome)) count++;
        }
        return count;
    }

    @Unique
    private float rawlands$verticalDepth(Camera camera, int lowerBoundary, int upperBoundary) {
        float y = (float) camera.position().y;
        if (y <= lowerBoundary) {
            return 1.0f;
        }
        if (y >= upperBoundary) {
            return 0.0f;
        }
        float progress = (y - lowerBoundary) / (upperBoundary - lowerBoundary);
        return 1.0f - progress * progress * progress;
    }
}

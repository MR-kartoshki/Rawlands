package mrkartoshki.rawlands.mixin.client;

import mrkartoshki.rawlands.world.biome.ModBiomes;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class MistCoastFogMixin {

    private static float smoothFraction = 0.0f;
    private static float smoothHeightFade = 1.0f;
    private static float cachedTarget = 0.0f;
    private static int tickCounter = 0;
    private static ClientLevel lastLevel = null;

    private static final float FOG_START = 3.0f;
    private static final float FOG_END   = 14.0f;

    // #9ab8c8 normalized
    private static final float FOG_R = 0.604f;
    private static final float FOG_G = 0.722f;
    private static final float FOG_B = 0.784f;

    @Inject(method = "setupFog", at = @At("RETURN"), require = 0)
    private void rawlands$mistCoastFog(
            Camera camera, int renderDistance, DeltaTracker deltaTracker,
            float partialTick, ClientLevel level,
            CallbackInfoReturnable<FogData> cir) {

        Entity entity = camera.entity();
        if (entity == null) return;

        if (level != lastLevel) {
            lastLevel = level;
            smoothFraction = 0.0f;
            smoothHeightFade = 1.0f;
            cachedTarget = 0.0f;
            tickCounter = 0;
        }

        if (++tickCounter >= 5) {
            tickCounter = 0;
            BlockPos center = entity.blockPosition();
            float totalWeight = 0.0f;
            float mistWeight = 0.0f;
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    float weight = 1.0f / (1.0f + dx * dx + dz * dz);
                    totalWeight += weight;
                    if (level.getBiome(center.offset(dx * 12, 0, dz * 12)).is(ModBiomes.MIST_COAST))
                        mistWeight += weight;
                }
            }
            cachedTarget = mistWeight / totalWeight;
        }

        smoothFraction = Mth.lerp(0.05f, smoothFraction, cachedTarget);
        if (smoothFraction < 0.001f) return;

        float targetHeightFade = Mth.clamp((110.0f - (float) entity.getY()) / 40.0f, 0.0f, 1.0f);
        smoothHeightFade = Mth.lerp(0.05f, smoothHeightFade, targetHeightFade);

        float strength = smoothFraction * smoothHeightFade;
        if (level.isThundering()) strength = Math.min(strength * 1.5f, 1.0f);
        else if (level.isRaining()) strength = Math.min(strength * 1.25f, 1.0f);

        if (strength < 0.001f) return;

        FogData fog = cir.getReturnValue();
        fog.environmentalStart  = Mth.lerp(strength, fog.environmentalStart,  FOG_START);
        fog.environmentalEnd    = Mth.lerp(strength, fog.environmentalEnd,    FOG_END);
        fog.renderDistanceStart = Mth.lerp(strength, fog.renderDistanceStart, FOG_START);
        fog.renderDistanceEnd   = Mth.lerp(strength, fog.renderDistanceEnd,   FOG_END);
    }
}

package mrkartoshki.rawlands.world.densityfunction;

import com.mojang.serialization.MapCodec;
import mrkartoshki.rawlands.Rawlands;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.DensityFunction;

/**
 * Registers Rawlands' custom density function types. Any future custom terrain shape is one new
 * {@link DensityFunction} record plus one {@code register(...)} line here; the shape itself is
 * then composed and tuned entirely in worldgen JSON under
 * {@code data/rawlands/worldgen/density_function/}.
 */
public final class RawlandsDensityFunctionTypes {

    private RawlandsDensityFunctionTypes() {}

    public static void register() {
        register("climate_gate", ClimateGateDensityFunction.DATA_CODEC);
        register("directional_ridge", DirectionalRidgeDensityFunction.DATA_CODEC);
    }

    private static void register(String name, MapCodec<? extends DensityFunction> codec) {
        Registry.register(
            BuiltInRegistries.DENSITY_FUNCTION_TYPE,
            Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, name),
            codec
        );
    }
}

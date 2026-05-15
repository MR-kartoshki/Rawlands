package mrkartoshki.rawlands.world.biome;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class RawlandsRegion extends Region {

    // Vanilla overworld region uses weight 8. Rawlands region uses weight 6, just to be safe.
    private static final int WEIGHT = 6;

    public RawlandsRegion(Identifier name) {
        super(name, RegionType.OVERWORLD, WEIGHT);
    }

    @Override
    public void addBiomes(Registry<Biome> registry,
                          Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {

        addModifiedVanillaOverworldBiomes(mapper, builder -> {
            builder.replaceBiome(
                    net.minecraft.world.level.biome.Biomes.PLAINS,
                    ModBiomes.ROCKY_FIELDS
            );
            builder.replaceBiome(
                    net.minecraft.world.level.biome.Biomes.DESERT,
                    ModBiomes.SALT_FLAT
            );
            builder.replaceBiome(
                    net.minecraft.world.level.biome.Biomes.MANGROVE_SWAMP,
                    ModBiomes.FLOODED_DELTA
            );
            builder.replaceBiome(
                    net.minecraft.world.level.biome.Biomes.WARM_OCEAN,
                    ModBiomes.CORAL_FOREST
            );
        });

        // SALT_FLAT
        addBiome(mapper, Climate.parameters(
                Climate.Parameter.span(0.2f, 2.0f),      // temperature
                Climate.Parameter.span(-1.0f, 0.2f),     // humidity
                Climate.Parameter.span(-0.2f, 1.0f),     // continentalness
                Climate.Parameter.span(0.2f, 1.0f),      // erosion
                Climate.Parameter.point(0.0f),      // depth
                Climate.Parameter.span(-0.2f, 0.2f),     // weirdness
                0.0f
        ), ModBiomes.SALT_FLAT);

        // SHRUBLAND
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(0.2f, 0.85f),   // temperature
            Climate.Parameter.span(-0.8f, -0.2f),  // humidity
            Climate.Parameter.span(0.1f, 0.8f),   // continentalness
            Climate.Parameter.span(0.45f, 1.0f),    // erosion
            Climate.Parameter.point(0.0f),     // depth
            Climate.Parameter.span(-0.2f, 0.2f),    // weirdness
            0.0f
        ), ModBiomes.SHRUBLAND);

        // MEDITERRANEAN_SCRUBLAND
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(0.4f, 0.75f),    // temperature
            Climate.Parameter.span(-0.1f, 0.3f),    // humidity
            Climate.Parameter.span(0.03f, 1.0f),    // continentalness
            Climate.Parameter.span(-0.2225f, 0.45f),// erosion: hilly
            Climate.Parameter.point(0.0f),     // depth
            Climate.Parameter.span(-1.0f, 1.0f),    // weirdness
            0.0f
        ), ModBiomes.MEDITERRANEAN_SCRUBLAND);

        // SUBALPINE_MEADOW
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(-0.15f, 0.2f),   // temperature
            Climate.Parameter.span(0.1f, 0.8f),     // humidity
            Climate.Parameter.span(0.03f, 1.0f),    // continentalness
            Climate.Parameter.span(-0.78f, -0.2225f),// erosion
            Climate.Parameter.point(0.0f),      // depth
            Climate.Parameter.span(-1.0f, 1.0f),    // weirdness
            0.0f
        ), ModBiomes.SUBALPINE_MEADOW);

        // FLOODED_DELTA
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(0.0f, 0.6f),     // temperature
            Climate.Parameter.span(0.4f, 1.0f),     // humidity
            Climate.Parameter.span(-1.0f, 0.5f),    // continentalness
            Climate.Parameter.span(0.2f, 1.0f),     // erosion
            Climate.Parameter.point(0.0f),     // depth
            Climate.Parameter.span(-1.0f, 1.0f),    // weirdness
            0.0f
        ), ModBiomes.FLOODED_DELTA);

        // DEAD_FOREST
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(-0.4f, 0.15f),   // temperature
            Climate.Parameter.span(-0.35f, -0.18f), // humidity
            Climate.Parameter.span(0.15f, 1.0f),    // continentalness
            Climate.Parameter.span(-0.375f, 0.45f), // erosion
            Climate.Parameter.point(0.0f),     // depth
            Climate.Parameter.span(-0.3f, 0.3f),    // weirdness
            0.0f
        ), ModBiomes.DEAD_FOREST);

        // TEMPERATE_RAINFOREST
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(-0.15f, 0.2f),    // temperature
            Climate.Parameter.span(0.5f, 1.0f),      // humidity
            Climate.Parameter.span(0.03f, 1.0f),     // continentalness
            Climate.Parameter.span(-0.2225f, 0.45f), // erosion
            Climate.Parameter.point(0.0f),      // depth
            Climate.Parameter.span(-1.0f, 1.0f),     // weirdness
            0.0f
        ), ModBiomes.TEMPERATE_RAINFOREST);

        // GRAVEL_FLATS
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(-0.3f, 0.1f),    // temperature
            Climate.Parameter.span(-0.3f, 0.1f),    // humidity
            Climate.Parameter.span(0f, 1.0f),       // continentalness
            Climate.Parameter.span(0.65f, 1.0f),    // erosion
            Climate.Parameter.point(0.0f),     // depth
            Climate.Parameter.span(-0.2f, 0.2f),    // weirdness
            0.0f
        ), ModBiomes.GRAVEL_FLATS);

        // ROCKY_FIELDS
        addBiome(mapper, Climate.parameters(
                Climate.Parameter.span(-0.45f, 0.1f),   // temperature
                Climate.Parameter.span(-0.35f, -0.1f),  // humidity
                Climate.Parameter.span(-0.11f, 1.0f),   // continentalness
                Climate.Parameter.span(0.45f, 1.0f),    // erosion
                Climate.Parameter.point(0.0f),      // depth
                Climate.Parameter.span(-1.0f, 1.0f),    // weirdness
                0.0f
        ), ModBiomes.ROCKY_FIELDS);

        // ABYSSAL_TRENCHES
        addBiome(mapper, Climate.parameters(
                Climate.Parameter.span(-1.0f, 0.1f),    // temperature
                Climate.Parameter.span(-1.0f, 1.0f),    // humidity
                Climate.Parameter.span(-1.0f, -0.4f),   // continentalness
                Climate.Parameter.span(-1.0f, 1.0f),    // erosion
                Climate.Parameter.point(0.0f),     // depth
                Climate.Parameter.span(-0.3f, 0.3f),    // weirdness
                0.0f
        ), ModBiomes.ABYSSAL_TRENCHES);

        // ALPINE_FOREST
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(-0.45f, -0.15f),  // temperature
            Climate.Parameter.span(-0.1f, 0.5f),     // humidity
            Climate.Parameter.span(0.03f, 1.0f),     // continentalness
            Climate.Parameter.span(-0.78f, 0.05f),   // erosion
            Climate.Parameter.point(0.0f),      // depth
            Climate.Parameter.span(-1.0f, 1.0f),     // weirdness
            0.0f
        ), ModBiomes.ALPINE_FOREST);
    }
}

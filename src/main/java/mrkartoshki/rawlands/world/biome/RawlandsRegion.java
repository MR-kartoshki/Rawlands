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

    // This is how much of the world Rawlands occupies. Vanilla overworld region uses weight 8. This means that if you play with ONLY Rawlands it will occupy roughly half of the World, HOWEVER that does NOT mean that half of the world is Rawlands biomes, it means that half of the world is Rawlands region, and then biomes are distributed inside the region according to the climate parameters. So if you have a lot of biomes with rare climate parameters, they will be rarer than biomes with common climate parameters, even if they are in the same region.
    private static final int WEIGHT = 8;

    public RawlandsRegion(Identifier name) {
        super(name, RegionType.OVERWORLD, WEIGHT);
    }

    @Override
    public void addBiomes(Registry<Biome> registry,
                          Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        // These are replaceBiome functions, they replace Vanilla biomes in the mod's region.
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

        // Continentalness bands (non-overlapping) so each biome group owns a clear slice.
        final Climate.Parameter oceanBand = Climate.Parameter.span(-1.0f, -0.55f);
        final Climate.Parameter coastalBand = Climate.Parameter.span(-0.55f, -0.15f);
        final Climate.Parameter lowlandBand = Climate.Parameter.span(-0.15f, 0.25f);
        final Climate.Parameter temperateBand = Climate.Parameter.span(0.25f, 0.65f);
        final Climate.Parameter highBand = Climate.Parameter.span(0.65f, 1.0f);

        // OCEAN BAND
        // ABYSSAL_TRENCHES
        addBiome(mapper, Climate.parameters(
                Climate.Parameter.span(-0.35f, 0.15f),  // temperature
                Climate.Parameter.span(-1.0f, 1.0f),    // humidity
                oceanBand,                               // continentalness
                Climate.Parameter.span(-1.0f, -0.1f),   // erosion
                Climate.Parameter.point(0.0f),          // depth
                Climate.Parameter.span(-0.3f, 0.3f),    // weirdness
                0.0f
        ), ModBiomes.ABYSSAL_TRENCHES);

        // COASTAL BAND
        // FLOODED_DELTA
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(0.15f, 0.55f),    // temperature
            Climate.Parameter.span(0.4f, 1.0f),      // humidity
            coastalBand,                              // continentalness
            Climate.Parameter.span(0.05f, 0.55f),    // erosion
            Climate.Parameter.point(0.0f),           // depth
            Climate.Parameter.span(-1.0f, 1.0f),     // weirdness
            0.0f
        ), ModBiomes.FLOODED_DELTA);

        // CORAL_FOREST
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(0.55f, 1.0f),     // temperature
            Climate.Parameter.span(0.3f, 1.0f),      // humidity
            coastalBand,                              // continentalness
            Climate.Parameter.span(-0.45f, 0.15f),   // erosion
            Climate.Parameter.point(0.0f),           // depth
            Climate.Parameter.span(-1.0f, 1.0f),     // weirdness
            0.0f
        ), ModBiomes.CORAL_FOREST);

        // LOWLAND BAND
        // SALT_FLAT
        addBiome(mapper, Climate.parameters(
                Climate.Parameter.span(0.45f, 1.0f),     // temperature
                Climate.Parameter.span(-1.0f, 0.2f),     // humidity
                lowlandBand,                              // continentalness
                Climate.Parameter.span(0.25f, 0.8f),     // erosion
                Climate.Parameter.point(0.0f),      // depth
                Climate.Parameter.span(-0.2f, 0.2f),     // weirdness
                0.0f
        ), ModBiomes.SALT_FLAT);

        // GRAVEL_FLATS
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(-0.2f, 0.05f),   // temperature
            Climate.Parameter.span(-0.3f, 0.1f),    // humidity
            lowlandBand,                             // continentalness
            Climate.Parameter.span(0.65f, 1.0f),    // erosion
            Climate.Parameter.point(0.0f),          // depth
            Climate.Parameter.span(-0.2f, 0.2f),    // weirdness
            0.0f
        ), ModBiomes.GRAVEL_FLATS);

        // ROCKY_FIELDS
        addBiome(mapper, Climate.parameters(
                Climate.Parameter.span(-0.35f, 0.0f),   // temperature
                Climate.Parameter.span(-0.35f, -0.1f),  // humidity
                lowlandBand,                             // continentalness
                Climate.Parameter.span(0.4f, 0.75f),    // erosion
                Climate.Parameter.point(0.0f),          // depth
                Climate.Parameter.span(-1.0f, 1.0f),    // weirdness
                0.0f
        ), ModBiomes.ROCKY_FIELDS);

        // TEMPERATE BAND
        // SHRUBLAND
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(0.25f, 0.7f),   // temperature
            Climate.Parameter.span(-0.8f, -0.2f),  // humidity
            temperateBand,                          // continentalness
            Climate.Parameter.span(0.5f, 0.85f),   // erosion
            Climate.Parameter.point(0.0f),     // depth
            Climate.Parameter.span(-0.2f, 0.2f),    // weirdness
            0.0f
        ), ModBiomes.SHRUBLAND);

        // MEDITERRANEAN_SCRUBLAND
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(0.45f, 0.7f),    // temperature
            Climate.Parameter.span(-0.1f, 0.3f),    // humidity
            temperateBand,                           // continentalness
            Climate.Parameter.span(-0.15f, 0.25f),  // erosion: hilly
            Climate.Parameter.point(0.0f),     // depth
            Climate.Parameter.span(-1.0f, 1.0f),    // weirdness
            0.0f
        ), ModBiomes.MEDITERRANEAN_SCRUBLAND);

        // DEAD_FOREST
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(-0.3f, 0.0f),    // temperature
            Climate.Parameter.span(-0.35f, -0.18f), // humidity
            temperateBand,                           // continentalness
            Climate.Parameter.span(-0.2f, 0.2f),    // erosion
            Climate.Parameter.point(0.0f),     // depth
            Climate.Parameter.span(-0.3f, 0.3f),    // weirdness
            0.0f
        ), ModBiomes.DEAD_FOREST);

        // TEMPERATE_RAINFOREST
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(-0.05f, 0.2f),    // temperature
            Climate.Parameter.span(0.5f, 1.0f),      // humidity
            temperateBand,                            // continentalness
            Climate.Parameter.span(-0.18f, 0.2f),    // erosion
            Climate.Parameter.point(0.0f),      // depth
            Climate.Parameter.span(-1.0f, 1.0f),     // weirdness
            0.0f
        ), ModBiomes.TEMPERATE_RAINFOREST);

        // HIGH BAND
        // SUBALPINE_MEADOW
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(-0.2f, 0.05f),   // temperature
            Climate.Parameter.span(0.1f, 0.8f),     // humidity
            highBand,                                // continentalness
            Climate.Parameter.span(-0.75f, -0.35f), // erosion
            Climate.Parameter.point(0.0f),          // depth
            Climate.Parameter.span(-1.0f, 1.0f),    // weirdness
            0.0f
        ), ModBiomes.SUBALPINE_MEADOW);

        // ALPINE_FOREST
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(-0.45f, -0.2f),   // temperature
            Climate.Parameter.span(-0.1f, 0.5f),     // humidity
            highBand,                                 // continentalness
            Climate.Parameter.span(-0.7f, -0.2f),    // erosion
            Climate.Parameter.point(0.0f),      // depth
            Climate.Parameter.span(-1.0f, 1.0f),     // weirdness
            0.0f
        ), ModBiomes.ALPINE_FOREST);
    }
}

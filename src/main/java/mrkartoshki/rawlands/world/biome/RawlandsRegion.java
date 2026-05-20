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

    // This is how much of the world Rawlands occupies. Vanilla overworld region uses weight 8. This means that if you play with ONLY Rawlands it will occupy roughly half of the World, HOWEVER, that does NOT mean that half of the world is Rawlands biomes, it means that half of the world is the Rawlands region, and then biomes are distributed inside the region according to the climate parameters. So if you have a lot of biomes with rare climate parameters, they will be rarer than biomes with common climate parameters, even if they are in the same region.
    private static final int WEIGHT = 8;

    public RawlandsRegion(Identifier name) {
        super(name, RegionType.OVERWORLD, WEIGHT);
    }

    @Override
    public void addBiomes(Registry<Biome> registry,
                          Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        // Replace vanilla overworld biomes with Rawlands biomes in the Rawlands region.
        addModifiedVanillaOverworldBiomes(mapper, builder -> {
            builder.replaceBiome(net.minecraft.world.level.biome.Biomes.PLAINS,        ModBiomes.ROCKY_FIELDS);
            builder.replaceBiome(net.minecraft.world.level.biome.Biomes.DESERT,        ModBiomes.SALT_FLAT);
            builder.replaceBiome(net.minecraft.world.level.biome.Biomes.MANGROVE_SWAMP,ModBiomes.FLOODED_DELTA);
            builder.replaceBiome(net.minecraft.world.level.biome.Biomes.WARM_OCEAN,    ModBiomes.CORAL_FOREST);
            builder.replaceBiome(net.minecraft.world.level.biome.Biomes.STONY_SHORE,   ModBiomes.MIST_COAST);
        });

        // Continentalness
        final Climate.Parameter oceanBand     = Climate.Parameter.span(-1.0f, -0.55f); // ocean
        final Climate.Parameter coastalBand   = Climate.Parameter.span(-0.55f, -0.15f);// coastal
        final Climate.Parameter lowlandBand   = Climate.Parameter.span(-0.15f,  0.25f);// lowland
        final Climate.Parameter temperateBand = Climate.Parameter.span( 0.25f,  0.65f);// temperate
        final Climate.Parameter highBand      = Climate.Parameter.span( 0.65f,  1.0f); // high

        // Temperature
        final Climate.Parameter coldTemp     = Climate.Parameter.span(-1.0f, -0.5f);  // arctic/alpine
        final Climate.Parameter coolTemp     = Climate.Parameter.span(-0.5f,  0.0f);  // cool temperate
        final Climate.Parameter mildTemp     = Climate.Parameter.span( 0.0f,  0.25f); // mild
        final Climate.Parameter warmTemp     = Climate.Parameter.span( 0.25f,  0.55f);// warm
        final Climate.Parameter hotTemp      = Climate.Parameter.span( 0.55f,  0.8f); // hot
        final Climate.Parameter scaldingTemp = Climate.Parameter.span( 0.8f,  1.0f);  // scorching
        final Climate.Parameter coldHalfTemp = Climate.Parameter.span(-1.0f,   0.0f); // cold half of the temperature range

        // Humidity
        final Climate.Parameter aridHumid    = Climate.Parameter.span(-1.0f, -0.35f); // bone dry
        final Climate.Parameter dryHumid     = Climate.Parameter.span(-0.35f, -0.05f);// dry
        final Climate.Parameter moderateHumid= Climate.Parameter.span(-0.05f,  0.3f); // moderate
        final Climate.Parameter wetHumid     = Climate.Parameter.span( 0.3f,   0.6f); // wet
        final Climate.Parameter soakingHumid = Climate.Parameter.span( 0.6f,   1.0f); // soaking
        final Climate.Parameter fullRangeHumid= Climate.Parameter.span(-1.0f,  1.0f); // full range for ocean biomes

        // Erosion
        final Climate.Parameter peakErosion  = Climate.Parameter.span(-1.0f, -0.5f);  // mountain peaks
        final Climate.Parameter hillErosion  = Climate.Parameter.span(-0.5f, -0.15f); // hilly
        final Climate.Parameter slopeErosion = Climate.Parameter.span(-0.15f,  0.2f); // gentle slopes
        final Climate.Parameter rollErosion  = Climate.Parameter.span( 0.2f,   0.5f); // rolling terrain
        final Climate.Parameter plainsErosion= Climate.Parameter.span( 0.5f,   0.75f);// flat plains
        final Climate.Parameter flatErosion  = Climate.Parameter.span( 0.75f,  1.0f); // very flat
        final Climate.Parameter fullErosion  = Climate.Parameter.span(-1.0f,   1.0f); // full range for ocean biomes

        // Weirdness
        final Climate.Parameter fullWeird   = Climate.Parameter.span(-1.0f, 1.0f);    // full range
        final Climate.Parameter normalWeird = Climate.Parameter.span(-0.3f, 0.3f);    // makes biome rarer

        // Depth
        final Climate.Parameter surface    = Climate.Parameter.point(0.0f);      // surface
        final Climate.Parameter underground= Climate.Parameter.span(0.2f, 0.9f);      // underground


        // ABYSSAL_TRENCHES
        addBiome(mapper, Climate.parameters(
            coldHalfTemp, fullRangeHumid, oceanBand, fullErosion, surface, normalWeird, 0.0f
        ), ModBiomes.ABYSSAL_TRENCHES);

        // FLOODED_DELTA
        addBiome(mapper, Climate.parameters(
            warmTemp, soakingHumid, coastalBand, rollErosion, surface, fullWeird, 0.0f
        ), ModBiomes.FLOODED_DELTA);

        // CORAL_FOREST
        addBiome(mapper, Climate.parameters(
            scaldingTemp, soakingHumid, coastalBand, slopeErosion, surface, fullWeird, 0.0f
        ), ModBiomes.CORAL_FOREST);

        // SALT_FLAT
        addBiome(mapper, Climate.parameters(
            scaldingTemp, aridHumid, lowlandBand, plainsErosion, surface, normalWeird, 0.0f
        ), ModBiomes.SALT_FLAT);

        // GRAVEL_FLATS
        addBiome(mapper, Climate.parameters(
            coolTemp, dryHumid, lowlandBand, flatErosion, surface, normalWeird, 0.0f
        ), ModBiomes.GRAVEL_FLATS);

        // ROCKY_FIELDS
        addBiome(mapper, Climate.parameters(
            coolTemp, dryHumid, lowlandBand, plainsErosion, surface, fullWeird, 0.0f
        ), ModBiomes.ROCKY_FIELDS);

        // SHRUBLAND
        addBiome(mapper, Climate.parameters(
            warmTemp, aridHumid, temperateBand, plainsErosion, surface, normalWeird, 0.0f
        ), ModBiomes.SHRUBLAND);

        // MEDITERRANEAN_SCRUBLAND
        addBiome(mapper, Climate.parameters(
            hotTemp, moderateHumid, temperateBand, slopeErosion, surface, fullWeird, 0.0f
        ), ModBiomes.MEDITERRANEAN_SCRUBLAND);

        // DEAD_FOREST
        addBiome(mapper, Climate.parameters(
            coolTemp, dryHumid, temperateBand, slopeErosion, surface, normalWeird, 0.0f
        ), ModBiomes.DEAD_FOREST);

        // TEMPERATE_RAINFOREST
        addBiome(mapper, Climate.parameters(
            mildTemp, soakingHumid, temperateBand, slopeErosion, surface, fullWeird, 0.0f
        ), ModBiomes.TEMPERATE_RAINFOREST);

        // MOSSWOOD_FOREST
        addBiome(mapper, Climate.parameters(
            warmTemp, wetHumid, temperateBand, slopeErosion, surface, fullWeird, 0.0f
        ), ModBiomes.MOSSWOOD_FOREST);

        // ROCKY_SHRUBLAND
        addBiome(mapper, Climate.parameters(
            warmTemp, moderateHumid, temperateBand, rollErosion, surface, fullWeird, 0.0f
        ), ModBiomes.ROCKY_SHRUBLAND);

        // AZALEA_FOREST
        addBiome(mapper, Climate.parameters(
            hotTemp, wetHumid, temperateBand, hillErosion, surface, fullWeird, 0.0f
        ), ModBiomes.AZALEA_FOREST);

        // SUBALPINE_MEADOW
        addBiome(mapper, Climate.parameters(
            coolTemp, wetHumid, highBand, hillErosion, surface, fullWeird, 0.0f
        ), ModBiomes.SUBALPINE_MEADOW);

        // ALPINE_FOREST — cold elevated terrain, any humidity, peaks + hills, upper-temperate to high continentalness
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(-1.0f, -0.45f),  // cold (slightly looser than coldTemp)
            Climate.Parameter.span(-0.5f,  0.55f),  // dry-to-wet (was moderateHumid only)
            Climate.Parameter.span( 0.4f,  1.0f),   // upper temperate + high (was highBand only)
            Climate.Parameter.span(-1.0f, -0.1f),   // peaks + hills (was peakErosion only)
            surface, fullWeird, 0.0f
        ), ModBiomes.ALPINE_FOREST);

        // LUSH_CAVES under AZALEA_FOREST
        addBiome(mapper, Climate.parameters(
            hotTemp, wetHumid, temperateBand, hillErosion, underground, fullWeird, 0.0f
        ), net.minecraft.world.level.biome.Biomes.LUSH_CAVES);

        // GLACIAL_FLATS — cold + any flat-to-rolling terrain, anywhere from coastal to temperate, arid-to-moderate humidity
        addBiome(mapper, Climate.parameters(
            coldTemp,                                // cold (unchanged)
            Climate.Parameter.span(-1.0f,  0.15f),  // arid to just-moderate (was dryHumid only)
            Climate.Parameter.span(-0.25f,  0.55f), // coastal-lowland-temperate (was lowlandBand only)
            Climate.Parameter.span( 0.35f,  1.0f),  // rolling to completely flat (was flatErosion 0.75-1.0 only)
            surface, fullWeird, 0.0f
        ), ModBiomes.GLACIAL_FLATS);

        // AMBER_STEPPE — hot dry inland steppe, flat-to-rolling, wider temp and humidity ranges
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span( 0.4f,  0.9f),   // warm-to-very-hot (was hotTemp 0.55-0.8 only)
            Climate.Parameter.span(-1.0f, -0.05f),  // bone-dry to nearly moderate (was aridHumid -1.0 to -0.35 only)
            Climate.Parameter.span(-0.15f,  0.7f),  // lowland to upper temperate (was temperateBand 0.25-0.65 only)
            Climate.Parameter.span( 0.1f,  0.65f),  // gentle slopes to flat plains (was rollErosion 0.2-0.5 only)
            surface, fullWeird, 0.0f
        ), ModBiomes.AMBER_STEPPE);

        // MONSOON_FOREST — hot+very humid, now reaches inland lowlands, hilly to gentle slopes
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span( 0.4f,  1.0f),   // warm-hot-scorching (was hotTemp 0.55-0.8 only)
            Climate.Parameter.span( 0.45f,  1.0f),  // wet-to-soaking (was soakingHumid 0.6-1.0 only)
            Climate.Parameter.span(-0.1f,  0.4f),   // lowland to temperate (avoid ocean/coastal zones)
            Climate.Parameter.span(-0.5f,  0.35f),  // hills to rolling (was hillErosion -0.5 to -0.15 only)
            surface, fullWeird, 0.0f
        ), ModBiomes.MONSOON_FOREST);

        // PRAIRIE
        addBiome(mapper, Climate.parameters(
            mildTemp, dryHumid, lowlandBand, plainsErosion, surface, fullWeird, 0.0f
        ), ModBiomes.PRAIRIE);
    }
}

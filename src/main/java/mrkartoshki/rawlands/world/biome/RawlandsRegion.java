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
            builder.replaceBiome(net.minecraft.world.level.biome.Biomes.OLD_GROWTH_PINE_TAIGA, ModBiomes.SEQUOIA_FOREST);
            // ALPS takes over vanilla's cold extreme-peak slots. It inherits vanilla's exact
            // parameter points, which sidesteps the two failure modes a custom addBiome box hit:
            // losing distance ties against vanilla points (biome never generates), and biome
            // boundaries decoupled from terrain height (biome flipping partway up a mountain,
            // or matching mid-air when given a negative depth span).
            builder.replaceBiome(net.minecraft.world.level.biome.Biomes.JAGGED_PEAKS,  ModBiomes.ALPS);
            builder.replaceBiome(net.minecraft.world.level.biome.Biomes.FROZEN_PEAKS,  ModBiomes.ALPS);
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

        // ALPINE_FOREST: cold elevated terrain, any humidity, hills and moderate peaks,
        // upper-temperate to high continentalness. The deepest erosion band (-1.0 to -0.6)
        // is left to ALPS so the two boxes stay disjoint (ties in the nearest-point climate
        // search are unpredictable).
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(-1.0f, -0.45f),  // cold (slightly looser than coldTemp)
            Climate.Parameter.span(-0.5f,  0.55f),  // dry-to-wet (was moderateHumid only)
            Climate.Parameter.span( 0.4f,  1.0f),   // upper temperate + high (was highBand only)
            Climate.Parameter.span(-0.6f, -0.1f),   // hills + moderate peaks (deepest band ceded to ALPS)
            surface, fullWeird, 0.0f
        ), ModBiomes.ALPINE_FOREST);

        // LUSH_CAVES under AZALEA_FOREST
        addBiome(mapper, Climate.parameters(
            hotTemp, wetHumid, temperateBand, hillErosion, underground, fullWeird, 0.0f
        ), net.minecraft.world.level.biome.Biomes.LUSH_CAVES);

        // GLACIAL_FLATS: cold + any flat-to-rolling terrain, anywhere from coastal to temperate, arid-to-moderate humidity
        addBiome(mapper, Climate.parameters(
            coldTemp,                                // cold (unchanged)
            Climate.Parameter.span(-1.0f,  0.15f),  // arid to just-moderate (was dryHumid only)
            Climate.Parameter.span(-0.25f,  0.55f), // coastal-lowland-temperate (was lowlandBand only)
            Climate.Parameter.span( 0.35f,  1.0f),  // rolling to completely flat (was flatErosion 0.75-1.0 only)
            surface, fullWeird, 0.0f
        ), ModBiomes.GLACIAL_FLATS);

        // AMBER_STEPPE: hot dry inland steppe, flat-to-rolling, wider temp and humidity ranges.
        // Temp capped at 0.8: the scalding band above is ceded to DUNE_SEA.
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span( 0.4f,  0.8f),   // warm-to-hot (scalding ceded to DUNE_SEA)
            Climate.Parameter.span(-1.0f, -0.05f),  // bone-dry to nearly moderate (was aridHumid -1.0 to -0.35 only)
            Climate.Parameter.span(-0.15f,  0.7f),  // lowland to upper temperate (was temperateBand 0.25-0.65 only)
            Climate.Parameter.span( 0.1f,  0.65f),  // gentle slopes to flat plains (was rollErosion 0.2-0.5 only)
            surface, fullWeird, 0.0f
        ), ModBiomes.AMBER_STEPPE);

        // MONSOON_FOREST: hot+very humid, now reaches inland lowlands, hilly to gentle slopes
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

        // Depth span used by the custom-shaped/gated biomes below instead of the usual
        // surface point(0). Two constraints have to be satisfied simultaneously:
        //
        //  1. STRICT WIN at surface: everywhere inside an addBiome box the climate search
        //     would otherwise tie at distance 0 with one of the copied vanilla points
        //     (they tile the whole space, all pinned at depth point 0). Vanilla's
        //     Climate.RTree breaks exact ties via a ThreadLocal "last result", i.e. by
        //     worker-thread history, which is non-deterministic across chunk generations.
        //     The biome_gate density function re-queries biomes while shaping terrain, so
        //     tie flicker showed up as broken, corruption-looking chunks at chunk borders.
        //  2. NO BIOME IN AIR: the biome must NOT win in air columns above its own
        //     terrain. Climate depth goes negative for Y above the terrain baseline, so a
        //     span that extends into negative depth (e.g. our earlier span(-0.3, 0.1))
        //     matches sky columns and paints the biome into empty air.
        //
        // A strictly-positive narrow span solves both: near surface (real depth ~ +0.02)
        // it strict-wins against vanilla's depth-0 points; above the baseline (real depth
        // negative) it is FARTHER from the query than vanilla's depth 0, so vanilla wins;
        // and it is narrow enough not to trespass on vanilla's cave-biome depth region
        // (~0.2-0.9), which retains cave biomes underground.
        //
        // The gate sampler's constant depth in Rawlands.java is set inside this span for
        // the same strict-win reason. ALPS doesn't need any of this: replaceBiome inherits
        // vanilla's own parameter points, which are unique and tie-free.
        final Climate.Parameter strictWinDepth = Climate.Parameter.span(0.005f, 0.05f);

        // Tighter strict-win depth for surface-only biomes (Dune Sea, Fungi Forest). With the
        // shared span (0.005, 0.05) they stayed the nearest match against vanilla cave biomes
        // (DRIPSTONE_CAVES depth ~ (0.2, 0.9)) down to depth ≈ 0.125, i.e. ~10 blocks below the
        // surface, so the biome name (and its features) reached into caves. Capping the max at
        // 0.02 moves the crossover with the cave biomes up close to the true surface, handing
        // shallow caves back to the vanilla cave biomes. Fjords keeps the wider span because
        // it needs to cover the water column down into deep channels its own shape carves.
        final Climate.Parameter surfaceOnlyDepth = Climate.Parameter.span(0.005f, 0.02f);

        // FUNGI_FOREST: mild + soaking humid lowland with gentle terrain. Deliberately disjoint
        // from its wet neighbours: TEMPERATE_RAINFOREST/MOSSWOOD sit in the temperate cont band
        // with slope erosion, MONSOON_FOREST starts at temp 0.4, FLOODED_DELTA is coastal.
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span( 0.0f,  0.35f),  // mild to mildly-warm
            Climate.Parameter.span( 0.55f, 1.0f),   // soaking
            Climate.Parameter.span(-0.15f, 0.3f),   // lowland
            Climate.Parameter.span( 0.2f,  0.75f),  // rolling to flat plains
            surfaceOnlyDepth, fullWeird, 0.0f
        ), ModBiomes.FUNGI_FOREST);

        // DUNE_SEA: scalding arid inland erg. Interior-disjoint from its hot neighbours:
        // SALT_FLAT sits in the coastal-lowland cont band (-0.15..0.25), AMBER_STEPPE caps at
        // temp 0.8 (band ceded above), MEDITERRANEAN_SCRUBLAND is moderate-humidity. The
        // asymmetric dune shape (gentle windward slope, steep slip face) is applied by
        // rawlands:dune_sea/gated_shape via the biome_gate density function.
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span( 0.8f,  1.0f),   // scalding
            Climate.Parameter.span(-1.0f, -0.35f),  // arid
            Climate.Parameter.span( 0.25f,  0.7f),  // inland (SALT_FLAT owns the lowland band)
            Climate.Parameter.span( 0.2f,  0.75f),  // rolling to flat; dunes supply the relief
            surfaceOnlyDepth, fullWeird, 0.0f
        ), ModBiomes.DUNE_SEA);

        // FJORDS: cold rainy coast. The drowned-channel + steep-wall terrain is applied by
        // rawlands:fjords/gated_shape via biome_gate (a -0.15 base offset carves navigable
        // channels below sea level; the asymmetric ridge raises the walls through it).
        // Disjoint from GLACIAL_FLATS by erosion (it starts at 0.35) and from FLOODED_DELTA /
        // CORAL_FOREST by temperature.
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(-1.0f, -0.4f),   // cold
            Climate.Parameter.span( 0.0f,  1.0f),   // moderate-to-soaking (rainy coast)
            Climate.Parameter.span(-0.55f, -0.1f),  // coastal
            Climate.Parameter.span(-0.5f,  0.3f),   // hills to gentle; walls come from our shape
            strictWinDepth, fullWeird, 0.0f
        ), ModBiomes.FJORDS);

        // ALPS is placed via replaceBiome(JAGGED_PEAKS / FROZEN_PEAKS) above, not a custom
        // climate box (see the comment there). Its asymmetric cliff/moss terrain shape is
        // applied by rawlands:alps/gated_shape (data/rawlands/worldgen/density_function/alps/),
        // whose climate gate covers the vanilla peak-biome climate (deep erosion, inland).

        // SEQUOIA_FOREST is placed via replaceBiome(OLD_GROWTH_PINE_TAIGA) above, not a custom
        // climate box: inheriting vanilla's exact parameter points sidesteps the depth/tie-break
        // issues a custom addBiome box needs surfaceOnlyDepth/strictWinDepth workarounds for.
    }
}

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

    // Vanilla overworld region uses weight 8.
    private static final int WEIGHT = 8;

    public RawlandsRegion(Identifier name) {
        super(name, RegionType.OVERWORLD, WEIGHT);
    }

    @Override
    public void addBiomes(Registry<Biome> registry,
                          Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {

        // Preserve all vanilla biomes within this region's share of the world.
        // An empty builder means no vanilla biome is replaced — all vanilla points
        // pass through unchanged. addBiome() below INSERTS new points alongside them
        // rather than replacing existing ones.
        addModifiedVanillaOverworldBiomes(mapper, builder -> {});

        // SALT_FLAT — desert-hot, arid, inland terrain.
        // Dramatically widened ranges to ensure viable spawn frequency.
        // Continentalness: -0.2–1.0 covers coastal and all inland (needed for any spawn)
        // Erosion: 0.2–1.0 spans mid-to-flat terrain (levels 3–6)
        // Weirdness: ±1.0 unrestricted to maximize compatibility
        addBiome(mapper, Climate.parameters(
                Climate.Parameter.span(0.2f, 2.0f),      // temperature: desert-hot
                Climate.Parameter.span(-1.0f, 0.2f),     // humidity: arid to dry
                Climate.Parameter.span(-0.2f, 1.0f),     // continentalness: WIDE — all continental zones
                Climate.Parameter.span(0.2f, 1.0f),      // erosion: WIDE — mid to flat terrain (levels 3–6)
                Climate.Parameter.point(0.0f),           // depth: surface only
                Climate.Parameter.span(-1.0f, 1.0f),     // weirdness: UNRESTRICTED — all terrain types
                0.0f
        ), ModBiomes.SALT_FLAT);

        // SHRUBLAND — warm (not too hot), dry (drier half), flat inland.
        // Continentalness capped at 0.7: excludes deep far-inland zones to reduce overall footprint.
        // Was 0.03–1.0 before, which spanned nearly all inland terrain and made it too common.
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(0.15f, 0.55f),   // temperature: warm (capped below hot)
            Climate.Parameter.span(-0.4f, -0.15f),  // humidity: dry — drier half only
            Climate.Parameter.span(0.12f, 0.75f),   // continentalness: inland only (avoid coastal floodplains)
            Climate.Parameter.span(0.45f, 1.0f),    // erosion: flat plains (levels 5–6)
            Climate.Parameter.point(0.0f),           // depth: surface only
            Climate.Parameter.span(-0.2f, 0.2f),    // weirdness: calm terrain only
            0.0f
        ), ModBiomes.SHRUBLAND);

        // MEDITERRANEAN_SCRUBLAND — warm-hot, moderate humidity, hilly terrain.
        // Temperature narrowed to 0.4–0.75: was 0.3–1.0 (70% of the hot range) which made it
        // enormous. Now targets the warm-to-moderate-hot band only.
        // Humidity and erosion constraints stay the same — they already separate it from shrubland.
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(0.4f, 0.75f),    // temperature: warm-hot (narrowed from 0.3–1.0)
            Climate.Parameter.span(-0.1f, 0.3f),    // humidity: moderate — less dry than shrubland
            Climate.Parameter.span(0.03f, 1.0f),    // continentalness: inland
            Climate.Parameter.span(-0.2225f, 0.45f),// erosion: hilly (levels 2–4)
            Climate.Parameter.point(0.0f),           // depth: surface only
            Climate.Parameter.span(-1.0f, 1.0f),    // weirdness: all — erosion already constrains well
            0.0f
        ), ModBiomes.MEDITERRANEAN_SCRUBLAND);

        // SUBALPINE_MEADOW — cool-normal temperature, normal-wet humidity, high elevation.
        // Erosion levels 1–2 lock placement to mountain/plateau terrain — no further tuning needed.
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(-0.15f, 0.2f),   // temperature: normal (cool edge)
            Climate.Parameter.span(0.1f, 0.8f),     // humidity: normal to wet
            Climate.Parameter.span(0.03f, 1.0f),    // continentalness: inland
            Climate.Parameter.span(-0.78f, -0.2225f),// erosion: high plateau/mountain (levels 1–2)
            Climate.Parameter.point(0.0f),           // depth: surface only
            Climate.Parameter.span(-1.0f, 1.0f),    // weirdness: all — erosion already constrains well
            0.0f
        ), ModBiomes.SUBALPINE_MEADOW);

        // FLOODED_DELTA — warm, wet-humid, near-coastal, flat.
        // Dramatically widened to ensure spawn viability in warm-wet zones.
        // Temperature: 0.0–0.8 covers most warm biomes.
        // Continentalness: -1.0–0.5 covers coastal and near-inland.
        // Weirdness: ±1.0 unrestricted.
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(0.0f, 0.8f),     // temperature: warm (expanded)
            Climate.Parameter.span(0.2f, 1.0f),     // humidity: wet to humid
            Climate.Parameter.span(-1.0f, 0.5f),    // continentalness: WIDE — coastal to near-inland
            Climate.Parameter.span(0.2f, 1.0f),     // erosion: WIDE — mid to flat (levels 3–6)
            Climate.Parameter.point(0.0f),           // depth: surface only
            Climate.Parameter.span(-1.0f, 1.0f),    // weirdness: UNRESTRICTED
            0.0f
        ), ModBiomes.FLOODED_DELTA);

        // DEAD_FOREST — cold-normal temperature, strictly dry, hilly inland terrain.
        // Humidity tightened to -0.35 to -0.15 to avoid overlap with normal cold forests.
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(-0.4f, 0.15f),   // temperature: cold to cool-normal
            Climate.Parameter.span(-0.35f, -0.18f), // humidity: strictly dry
            Climate.Parameter.span(0.15f, 1.0f),    // continentalness: inland
            Climate.Parameter.span(-0.375f, 0.45f), // erosion: hilly (levels 2–4)
            Climate.Parameter.point(0.0f),           // depth: surface only
            Climate.Parameter.span(-0.3f, 0.3f),    // weirdness: moderate, avoids extreme terrain
            0.0f
        ), ModBiomes.DEAD_FOREST);

        // TEMPERATE_RAINFOREST — cool-normal, very wet, hilly terrain.
        // Humidity starts at 0.5 (above subalpine's 0.1–0.8 for hilly erosion — those are mountains,
        // this targets hilly erosion only). Prevents overlap with subalpine meadow (which uses mountain erosion).
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(-0.15f, 0.2f),    // temperature: cool-normal
            Climate.Parameter.span(0.5f, 1.0f),      // humidity: very wet
            Climate.Parameter.span(0.03f, 1.0f),     // continentalness: inland
            Climate.Parameter.span(-0.2225f, 0.45f), // erosion: hilly (levels 2–4), NOT mountain
            Climate.Parameter.point(0.0f),            // depth: surface only
            Climate.Parameter.span(-1.0f, 1.0f),     // weirdness: all
            0.0f
        ), ModBiomes.TEMPERATE_RAINFOREST);

        // ALPINE_FOREST — cold, moderate humidity, mountain+hilly terrain.
        // Dead forest occupies humidity -0.35 to -0.15 (dry). Alpine forest starts at -0.1, no overlap.
        addBiome(mapper, Climate.parameters(
            Climate.Parameter.span(-0.45f, -0.15f),  // temperature: cold
            Climate.Parameter.span(-0.1f, 0.5f),     // humidity: dry-normal (moderate)
            Climate.Parameter.span(0.03f, 1.0f),     // continentalness: inland
            Climate.Parameter.span(-0.78f, 0.05f),   // erosion: mountain+hilly (levels 0–3)
            Climate.Parameter.point(0.0f),            // depth: surface only
            Climate.Parameter.span(-1.0f, 1.0f),     // weirdness: all
            0.0f
        ), ModBiomes.ALPINE_FOREST);
    }
}

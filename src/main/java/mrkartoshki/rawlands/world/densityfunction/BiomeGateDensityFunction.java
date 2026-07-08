package mrkartoshki.rawlands.world.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Scales {@code input} by how much of the surrounding surface belongs to one of the given biomes:
 * 1 deep inside the biome, fading smoothly to 0 across the biome border (blurred over
 * {@code blurRadius} quarts in each direction, i.e. a border feather of roughly
 * {@code blurRadius * 8} blocks).
 *
 * <p>Terrain density functions cannot see biome identity (their context is only x/y/z), so this
 * works by querying the overworld's biome source directly with a <em>constant-depth</em> climate
 * sampler — the column's surface biome. A constant depth is what breaks the circular dependency:
 * the real sampler's depth includes {@code offset}, which contains this very function. All other
 * climate parameters (temperature, humidity, continentalness, erosion, weirdness) are 2D and
 * never reference {@code offset}, so they are used as-is from the real wired router.
 *
 * <p><b>Determinism requirement:</b> gated biomes placed via {@code addBiome} must use a depth
 * <em>span</em> straddling the sampler's constant (see {@code strictWinDepth} in
 * {@code RawlandsRegion}) so every lookup is a strict nearest-point win. Exact distance ties are
 * broken by a ThreadLocal in {@code Climate.RTree} — i.e. by worker-thread history — and a gate
 * built on tied lookups returns different masks for the same column on different threads, which
 * manifests as corrupted-looking chunk seams in the shaped terrain.
 * TerraBlender's region dispatch is positional (its uniqueness comes from block coordinates via
 * its {@code Climate.ParameterList} mixin, not from the sampler), so region resolution stays
 * correct with the replacement sampler.
 *
 * <p>The biome source and sampler are captured when the overworld loads (see the
 * {@code ServerWorldEvents.LOAD} hook in {@link mrkartoshki.rawlands.Rawlands}); until then the
 * gate outputs 0 (no shape). This type is the biome-precise alternative to
 * {@link ClimateGateDensityFunction}: use this when the shape must match a biome's footprint
 * exactly, and the climate gate when an approximate climate-band region is enough.
 */
public final class BiomeGateDensityFunction implements DensityFunction {

    public static final MapCodec<BiomeGateDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            DensityFunction.CODEC.fieldOf("input").forGetter(f -> f.input),
            ResourceKey.codec(Registries.BIOME).listOf().fieldOf("biomes").forGetter(f -> f.biomes),
            Codec.intRange(0, 8).optionalFieldOf("blur_radius", 2).forGetter(f -> f.blurRadius)
        ).apply(instance, BiomeGateDensityFunction::new)
    );
    public static final KeyDispatchDataCodec<BiomeGateDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

    /** Overworld biome source + depth-zeroed sampler, captured on world load. */
    private record GateContext(BiomeSource biomeSource, Climate.Sampler sampler) {}

    @Nullable
    private static volatile GateContext gateContext;

    public static void setContext(BiomeSource biomeSource, Climate.Sampler depthZeroedSampler) {
        gateContext = new GateContext(biomeSource, depthZeroedSampler);
    }

    public static void clearContext() {
        gateContext = null;
    }

    private final DensityFunction input;
    private final List<ResourceKey<Biome>> biomes;
    private final int blurRadius;

    public BiomeGateDensityFunction(DensityFunction input, List<ResourceKey<Biome>> biomes, int blurRadius) {
        this.input = input;
        this.biomes = biomes;
        this.blurRadius = blurRadius;
    }

    @Override
    public double compute(DensityFunction.FunctionContext context) {
        GateContext gate = gateContext;
        if (gate == null) {
            return 0.0;
        }
        int quartX = QuartPos.fromBlock(context.blockX());
        int quartZ = QuartPos.fromBlock(context.blockZ());

        int matches = 0;
        int total = 0;
        for (int dx = -this.blurRadius; dx <= this.blurRadius; dx++) {
            for (int dz = -this.blurRadius; dz <= this.blurRadius; dz++) {
                total++;
                Holder<Biome> biome = gate.biomeSource().getNoiseBiome(quartX + dx, 0, quartZ + dz, gate.sampler());
                for (ResourceKey<Biome> key : this.biomes) {
                    if (biome.is(key)) {
                        matches++;
                        break;
                    }
                }
            }
        }
        if (matches == 0) {
            return 0.0;
        }
        return Mth.smoothstep((double) matches / total) * this.input.compute(context);
    }

    @Override
    public void fillArray(double[] output, DensityFunction.ContextProvider contextProvider) {
        contextProvider.fillAllDirectly(output, this);
    }

    @Override
    public DensityFunction mapChildren(DensityFunction.Visitor visitor) {
        return new BiomeGateDensityFunction(visitor.apply(this.input), this.biomes, this.blurRadius);
    }

    @Override
    public double minValue() {
        return Math.min(0.0, this.input.minValue());
    }

    @Override
    public double maxValue() {
        return Math.max(0.0, this.input.maxValue());
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}

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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Scales {@code input} by how much of the surrounding surface belongs to one of the given biomes:
 * 1 deep inside the biome, fading smoothly to 0 across the biome border (blurred over
 * {@code blurRadius} quarts in each direction, i.e. a border feather of roughly
 * {@code blurRadius * 8} blocks).
 *
 * <p>Terrain density functions cannot see biome identity (their context is only x/y/z), so this
 * works by querying the overworld's biome source directly with a <em>constant-depth</em> climate
 * sampler, giving the column's surface biome. A constant depth breaks the circular dependency:
 * the real sampler's depth includes {@code offset}, which contains this very function. All other
 * climate parameters (temperature, humidity, continentalness, erosion, weirdness) are 2D and
 * never reference {@code offset}, so they are used as-is from the real wired router.
 *
 * <p><b>Determinism requirement:</b> gated biomes placed via {@code addBiome} must use a depth
 * <em>span</em> straddling the sampler's constant (see {@code strictWinDepth} in
 * {@code RawlandsRegion}) so every lookup is a strict nearest-point win. Exact distance ties are
 * broken by a ThreadLocal in {@code Climate.RTree}, i.e. by worker-thread history, and a gate
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

    /**
     * Overworld biome source + depth-zeroed sampler, captured on world load. {@code generation}
     * identifies the capture, so {@link QuartBiomeCache} can tell whose entries it is holding. It
     * belongs in the record rather than a separate static: one volatile read of
     * {@link #gateContext} then yields the sampler and its identity together, leaving no window in
     * which a cache is tagged with one generation but filled from another's biome source.
     */
    private record GateContext(BiomeSource biomeSource, Climate.Sampler sampler, int generation) {}

    private static final AtomicInteger GENERATION = new AtomicInteger();

    @Nullable
    private static volatile GateContext gateContext;

    public static void setContext(BiomeSource biomeSource, Climate.Sampler depthZeroedSampler) {
        gateContext = new GateContext(biomeSource, depthZeroedSampler, GENERATION.incrementAndGet());
    }

    public static void clearContext() {
        gateContext = null;
    }

    // -----------------------------------------------------------------------
    // Biome lookup memoisation
    // -----------------------------------------------------------------------

    /**
     * {@code getNoiseBiome} dominates the cost of this class: each call evaluates five uncached
     * climate density functions through a {@code SinglePointContext}, then descends the biome
     * parameter R-tree. The blur loop below wants {@code (2r+1)²} of them per {@code compute},
     * {@code NoiseChunk.FlatCache} drives {@code compute} across a 5x5 grid of columns for every
     * chunk, and several gate instances are summed into {@code minecraft:overworld/offset}. Those
     * kernels overlap almost completely, so the same few columns are sampled over and over.
     *
     * <p>For a fixed {@link GateContext} the lookup is a pure function of the quart column, since
     * {@code context.blockY()} is never read. Memoising it therefore cannot change output.
     */
    private static final int CACHE_BITS = 6;
    private static final int CACHE_AXIS_MASK = (1 << CACHE_BITS) - 1;
    private static final int CACHE_SIZE = 1 << (CACHE_BITS * 2);

    /**
     * Direct-mapped, thread-confined memo of {@code (quartX, quartZ) -> biome}.
     *
     * <p>Density functions are shared across the worldgen worker pool, so this is a
     * {@link ThreadLocal}: no locking, no contention, and no allocation after warm-up. Indexing by
     * the low bits of each axis separately keeps a 64x64-quart window collision-free, which covers
     * every column one chunk can touch. The full key is still compared, so a collision from a
     * distant chunk is a miss rather than a wrong answer.
     *
     * <p>Only {@code Holder}s are retained, and those belong to the biome registry, so an unloaded
     * world's biome source is never pinned by a worker thread's cache.
     */
    private static final class QuartBiomeCache {
        private final long[] keys = new long[CACHE_SIZE];
        private final Holder<Biome>[] values = newValueArray();
        /** Generation of the {@link GateContext} these entries were filled from. */
        private int generation = -1;

        @SuppressWarnings("unchecked")
        private static Holder<Biome>[] newValueArray() {
            return (Holder<Biome>[]) new Holder<?>[CACHE_SIZE];
        }

        private void reset(int newGeneration) {
            Arrays.fill(this.values, null);
            this.generation = newGeneration;
        }

        private Holder<Biome> get(GateContext gate, int quartX, int quartZ) {
            long key = ((long) quartX << 32) | (quartZ & 0xFFFFFFFFL);
            int slot = ((quartX & CACHE_AXIS_MASK) << CACHE_BITS) | (quartZ & CACHE_AXIS_MASK);

            // A null value is the empty marker: getNoiseBiome never returns null.
            Holder<Biome> cached = this.values[slot];
            if (cached != null && this.keys[slot] == key) {
                return cached;
            }

            Holder<Biome> biome = gate.biomeSource().getNoiseBiome(quartX, 0, quartZ, gate.sampler());
            this.keys[slot] = key;
            this.values[slot] = biome;
            return biome;
        }
    }

    private static final ThreadLocal<QuartBiomeCache> BIOME_CACHE =
        ThreadLocal.withInitial(QuartBiomeCache::new);

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

        QuartBiomeCache cache = BIOME_CACHE.get();
        if (cache.generation != gate.generation()) {
            cache.reset(gate.generation());
        }

        int matches = 0;
        int total = 0;
        for (int dx = -this.blurRadius; dx <= this.blurRadius; dx++) {
            for (int dz = -this.blurRadius; dz <= this.blurRadius; dz++) {
                total++;
                Holder<Biome> biome = cache.get(gate, quartX + dx, quartZ + dz);
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

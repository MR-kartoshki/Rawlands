package mrkartoshki.rawlands.world.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.List;

/**
 * Scales {@code input} by a smooth 0..1 mask that is 1 only where every gate's function value
 * lies inside its {@code [min, max]} band, fading to 0 over {@code margin} at each band edge.
 *
 * <p>This is the generic "apply a custom terrain shape only in this climate region" building
 * block: gate on the vanilla climate density functions ({@code minecraft:overworld/continents},
 * {@code .../erosion}, {@code .../ridges} — optionally wrapped in {@code minecraft:abs} for
 * symmetric weirdness bands) using the same parameter ranges the biome occupies in
 * {@link mrkartoshki.rawlands.world.biome.RawlandsRegion}. Because density functions cannot see
 * biome identity (only x/y/z), gating by climate parameters is what ties a terrain shape to a
 * biome's territory — and the smoothstep margin guarantees a seam-free blend into neighbouring
 * terrain.
 */
public record ClimateGateDensityFunction(DensityFunction input, List<Gate> gates) implements DensityFunction {

    /** One climate dimension of the gate: full strength inside [min+margin, max-margin], zero outside [min, max]. */
    public record Gate(DensityFunction function, double min, double max, double margin) {
        public static final Codec<Gate> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                DensityFunction.CODEC.fieldOf("argument").forGetter(Gate::function),
                Codec.DOUBLE.fieldOf("min").forGetter(Gate::min),
                Codec.DOUBLE.fieldOf("max").forGetter(Gate::max),
                Codec.DOUBLE.fieldOf("margin").forGetter(Gate::margin)
            ).apply(instance, Gate::new)
        );

        double factor(DensityFunction.FunctionContext context) {
            double value = this.function.compute(context);
            if (value <= this.min || value >= this.max) {
                return 0.0;
            }
            if (this.margin <= 0.0) {
                return 1.0;
            }
            double edge = Math.min(value - this.min, this.max - value) / this.margin;
            return edge >= 1.0 ? 1.0 : Mth.smoothstep(edge);
        }
    }

    public static final MapCodec<ClimateGateDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            DensityFunction.CODEC.fieldOf("input").forGetter(ClimateGateDensityFunction::input),
            Gate.CODEC.listOf().fieldOf("gates").forGetter(ClimateGateDensityFunction::gates)
        ).apply(instance, ClimateGateDensityFunction::new)
    );
    public static final KeyDispatchDataCodec<ClimateGateDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

    @Override
    public double compute(DensityFunction.FunctionContext context) {
        double factor = 1.0;
        for (Gate gate : this.gates) {
            factor *= gate.factor(context);
            if (factor == 0.0) {
                return 0.0;
            }
        }
        return factor * this.input.compute(context);
    }

    @Override
    public void fillArray(double[] output, DensityFunction.ContextProvider contextProvider) {
        contextProvider.fillAllDirectly(output, this);
    }

    @Override
    public DensityFunction mapChildren(DensityFunction.Visitor visitor) {
        return new ClimateGateDensityFunction(
            visitor.apply(this.input),
            this.gates.stream()
                .map(gate -> new Gate(visitor.apply(gate.function()), gate.min(), gate.max(), gate.margin()))
                .toList());
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

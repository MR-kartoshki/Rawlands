package mrkartoshki.rawlands.world.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;

/**
 * A reusable asymmetric-landform primitive: an asymmetric height profile applied across the
 * zero-crossings of a signed ridge noise field.
 *
 * <p>The ridge crest lies wherever the noise crosses zero. The negative-noise flank rises over
 * {@code steepWidth} (in noise-value space) — small values give a sheer cliff — while the
 * positive-noise flank falls away over the much larger {@code gentleWidth}, giving a smooth
 * walkable slope. Because which world direction is "negative side" depends on the local noise
 * gradient, the cliff facing varies organically from ridge to ridge with no explicit orientation
 * field needed.
 *
 * <p>Optionally, past the foot of the gentle slope, a shallow basin dip of {@code basinDepth}
 * over {@code basinWidth} carves depressions that the aquifer system fills into small tarns.
 * Set {@code basinDepth} to 0 to disable.
 *
 * <p>Output is in terrain-offset density units (roughly: surface Y ≈ 128 + 128 × offset), ranging
 * over {@code [-basinDepth, amplitude]}. Intended to be added into the overworld {@code offset}
 * graph, gated by a {@link ClimateGateDensityFunction}. Other directional landforms (canyons,
 * mesas, escarpments) can reuse this type with different width/amplitude constants, or negative
 * amplitude for incised shapes.
 */
public record DirectionalRidgeDensityFunction(
    DensityFunction.NoiseHolder ridgeNoise,
    double xzScale,
    double steepWidth,
    double gentleWidth,
    double amplitude,
    double basinWidth,
    double basinDepth
) implements DensityFunction {

    public static final MapCodec<DirectionalRidgeDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            DensityFunction.NoiseHolder.CODEC.fieldOf("ridge_noise").forGetter(DirectionalRidgeDensityFunction::ridgeNoise),
            Codec.DOUBLE.fieldOf("xz_scale").forGetter(DirectionalRidgeDensityFunction::xzScale),
            Codec.DOUBLE.fieldOf("steep_width").forGetter(DirectionalRidgeDensityFunction::steepWidth),
            Codec.DOUBLE.fieldOf("gentle_width").forGetter(DirectionalRidgeDensityFunction::gentleWidth),
            Codec.DOUBLE.fieldOf("amplitude").forGetter(DirectionalRidgeDensityFunction::amplitude),
            Codec.DOUBLE.optionalFieldOf("basin_width", 0.0).forGetter(DirectionalRidgeDensityFunction::basinWidth),
            Codec.DOUBLE.optionalFieldOf("basin_depth", 0.0).forGetter(DirectionalRidgeDensityFunction::basinDepth)
        ).apply(instance, DirectionalRidgeDensityFunction::new)
    );
    public static final KeyDispatchDataCodec<DirectionalRidgeDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

    @Override
    public double compute(DensityFunction.FunctionContext context) {
        double r = this.ridgeNoise.getValue(
            context.blockX() * this.xzScale, 0.0, context.blockZ() * this.xzScale);

        if (r < 0.0) {
            // Cliff side: full height at the crest (r = 0), plunging to 0 over steepWidth.
            if (r <= -this.steepWidth) {
                return 0.0;
            }
            return this.amplitude * Mth.smoothstep(1.0 + r / this.steepWidth);
        }
        if (r <= this.gentleWidth) {
            // Gentle side: long smooth descent from the crest.
            return this.amplitude * Mth.smoothstep(1.0 - r / this.gentleWidth);
        }
        if (this.basinDepth > 0.0 && r <= this.gentleWidth + this.basinWidth) {
            // Tarn basin: shallow bump-shaped dip just past the foot of the gentle slope.
            double t = (r - this.gentleWidth) / this.basinWidth;
            double bump = 4.0 * t * (1.0 - t);
            return -this.basinDepth * bump * bump;
        }
        return 0.0;
    }

    @Override
    public void fillArray(double[] output, DensityFunction.ContextProvider contextProvider) {
        contextProvider.fillAllDirectly(output, this);
    }

    @Override
    public DensityFunction mapChildren(DensityFunction.Visitor visitor) {
        return new DirectionalRidgeDensityFunction(
            visitor.visitNoise(this.ridgeNoise),
            this.xzScale, this.steepWidth, this.gentleWidth,
            this.amplitude, this.basinWidth, this.basinDepth);
    }

    @Override
    public double minValue() {
        return Math.min(0.0, -this.basinDepth);
    }

    @Override
    public double maxValue() {
        return Math.max(0.0, this.amplitude);
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}

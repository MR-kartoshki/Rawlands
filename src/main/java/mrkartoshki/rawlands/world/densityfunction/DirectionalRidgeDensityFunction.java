package mrkartoshki.rawlands.world.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;

public record DirectionalRidgeDensityFunction(
    DensityFunction.NoiseHolder ridgeNoise,
    double xzScale,
    double steepWidth,
    double gentleWidth,
    double amplitude,
    double basinWidth,
    double basinDepth
) implements DensityFunction {
    private static final Codec<Double> POSITIVE_DOUBLE = Codec.DOUBLE.validate(value ->
        Double.isFinite(value) && value > 0.0
            ? DataResult.success(value)
            : DataResult.error(() -> "value must be finite and greater than zero")
    );
    private static final Codec<Double> FINITE_DOUBLE = Codec.DOUBLE.validate(value ->
        Double.isFinite(value)
            ? DataResult.success(value)
            : DataResult.error(() -> "value must be finite")
    );
    private static final Codec<Double> NON_NEGATIVE_DOUBLE = Codec.DOUBLE.validate(value ->
        Double.isFinite(value) && value >= 0.0
            ? DataResult.success(value)
            : DataResult.error(() -> "value must be finite and non-negative")
    );

    public static final MapCodec<DirectionalRidgeDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            DensityFunction.NoiseHolder.CODEC.fieldOf("ridge_noise").forGetter(DirectionalRidgeDensityFunction::ridgeNoise),
            POSITIVE_DOUBLE.fieldOf("xz_scale").forGetter(DirectionalRidgeDensityFunction::xzScale),
            POSITIVE_DOUBLE.fieldOf("steep_width").forGetter(DirectionalRidgeDensityFunction::steepWidth),
            POSITIVE_DOUBLE.fieldOf("gentle_width").forGetter(DirectionalRidgeDensityFunction::gentleWidth),
            FINITE_DOUBLE.fieldOf("amplitude").forGetter(DirectionalRidgeDensityFunction::amplitude),
            NON_NEGATIVE_DOUBLE.optionalFieldOf("basin_width", 0.0).forGetter(DirectionalRidgeDensityFunction::basinWidth),
            NON_NEGATIVE_DOUBLE.optionalFieldOf("basin_depth", 0.0).forGetter(DirectionalRidgeDensityFunction::basinDepth)
        ).apply(instance, DirectionalRidgeDensityFunction::new)
    );
    public static final KeyDispatchDataCodec<DirectionalRidgeDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

    public DirectionalRidgeDensityFunction {
        requirePositiveFinite(xzScale, "xzScale");
        requirePositiveFinite(steepWidth, "steepWidth");
        requirePositiveFinite(gentleWidth, "gentleWidth");
        requireFinite(amplitude, "amplitude");
        requireNonNegativeFinite(basinWidth, "basinWidth");
        requireNonNegativeFinite(basinDepth, "basinDepth");
        if (basinDepth > 0.0 && basinWidth == 0.0) {
            throw new IllegalArgumentException("basinWidth must be greater than zero when basinDepth is enabled");
        }
    }

    @Override
    public double compute(DensityFunction.FunctionContext context) {
        double ridge = ridgeNoise.getValue(context.blockX() * xzScale, 0.0, context.blockZ() * xzScale);

        if (ridge < 0.0) {
            if (ridge <= -steepWidth) {
                return 0.0;
            }
            return amplitude * Mth.smoothstep(1.0 + ridge / steepWidth);
        }
        if (ridge <= gentleWidth) {
            return amplitude * Mth.smoothstep(1.0 - ridge / gentleWidth);
        }
        if (basinDepth > 0.0 && ridge <= gentleWidth + basinWidth) {
            double progress = (ridge - gentleWidth) / basinWidth;
            double bump = 4.0 * progress * (1.0 - progress);
            return -basinDepth * bump * bump;
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
            visitor.visitNoise(ridgeNoise),
            xzScale,
            steepWidth,
            gentleWidth,
            amplitude,
            basinWidth,
            basinDepth
        );
    }

    @Override
    public double minValue() {
        return Math.min(0.0, Math.min(amplitude, -basinDepth));
    }

    @Override
    public double maxValue() {
        return Math.max(0.0, amplitude);
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }

    private static void requirePositiveFinite(double value, String name) {
        if (!Double.isFinite(value) || value <= 0.0) {
            throw new IllegalArgumentException(name + " must be finite and greater than zero");
        }
    }

    private static void requireNonNegativeFinite(double value, String name) {
        if (!Double.isFinite(value) || value < 0.0) {
            throw new IllegalArgumentException(name + " must be finite and non-negative");
        }
    }

    private static void requireFinite(double value, String name) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(name + " must be finite");
        }
    }
}

package mrkartoshki.rawlands.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Oversized procedural mushrooms for the Fungi Forest: taller and wider than vanilla's huge
 * mushrooms, with a slightly drifting stem and either a domed red cap or a broad flat brown cap
 * with a drooping rim.
 */
public class GiantMushroomFeature extends Feature<NoneFeatureConfiguration> {

    private static final BlockState STEM = Blocks.MUSHROOM_STEM.defaultBlockState();
    private static final BlockState RED_CAP = Blocks.RED_MUSHROOM_BLOCK.defaultBlockState();
    private static final BlockState BROWN_CAP = Blocks.BROWN_MUSHROOM_BLOCK.defaultBlockState();

    public GiantMushroomFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        if (!isValidGround(level.getBlockState(origin.below()))) {
            return false;
        }

        boolean red = random.nextBoolean();
        int height = red ? 6 + random.nextInt(5) : 5 + random.nextInt(5);

        // Require clear space along the stem line before committing.
        for (int y = 0; y < height; y++) {
            if (!TreeBranchHelper.canReplace(level, origin.above(y))) {
                return false;
            }
        }

        // Stem with a small lateral drift for tall specimens.
        BlockPos top = origin;
        double driftX = (random.nextDouble() - 0.5) * 0.35;
        double driftZ = (random.nextDouble() - 0.5) * 0.35;
        for (int y = 0; y < height; y++) {
            BlockPos pos = origin.offset((int) Math.round(driftX * y), y, (int) Math.round(driftZ * y));
            if (TreeBranchHelper.canReplace(level, pos)) {
                level.setBlock(pos, STEM, 3);
            }
            top = pos;
        }

        if (red) {
            placeDomeCap(level, random, top.above(), 3 + random.nextInt(2));
        } else {
            placeFlatCap(level, random, top, 4 + random.nextInt(3));
        }
        return true;
    }

    /**
     * Hollow hemisphere shell of red mushroom blocks over the stem top. Bounds are compared
     * as squared distance so the per-block loop (up to a few hundred iterations for a large
     * cap) never calls {@code Math.sqrt}. The outer/inner/rim radii are fixed per call, so their
     * squares are computed once, above the loops.
     */
    private static void placeDomeCap(WorldGenLevel level, RandomSource random, BlockPos capBase, int radius) {
        double outerSq = (radius + 0.4) * (radius + 0.4);
        double innerSq = (radius - 1.1) * (radius - 1.1);
        double rimSq = (radius - 0.1) * (radius - 0.1);
        for (int dy = 0; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    double distSq = dx * dx + dz * dz + (double) dy * dy;
                    // Shell only: skin of the hemisphere, slightly ragged at the rim.
                    if (distSq <= outerSq && distSq >= innerSq) {
                        if (dy == 0 && Mth.abs(dx) < radius - 1 && Mth.abs(dz) < radius - 1) {
                            continue; // keep the underside open around the stem
                        }
                        if (distSq >= rimSq && random.nextFloat() < 0.12) {
                            continue;
                        }
                        BlockPos pos = capBase.offset(dx, dy, dz);
                        if (TreeBranchHelper.canReplace(level, pos)) {
                            level.setBlock(pos, RED_CAP, 3);
                        }
                    }
                }
            }
        }
    }

    /** Broad one-block-thick brown disk at the stem top, rim drooping one block. */
    private static void placeFlatCap(WorldGenLevel level, RandomSource random, BlockPos top, int radius) {
        double outerSq = (radius + 0.3) * (radius + 0.3);
        double rimSq = (radius - 0.9) * (radius - 0.9);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double distSq = dx * dx + (double) dz * dz;
                if (distSq > outerSq) {
                    continue;
                }
                boolean rim = distSq >= rimSq;
                if (rim && random.nextFloat() < 0.15) {
                    continue;
                }
                BlockPos pos = top.above().offset(dx, rim ? -1 : 0, dz);
                if (TreeBranchHelper.canReplace(level, pos)) {
                    level.setBlock(pos, BROWN_CAP, 3);
                }
            }
        }
    }

    private static boolean isValidGround(BlockState state) {
        return state.is(BlockTags.DIRT)
            || state.is(Blocks.GRASS_BLOCK)
            || state.is(Blocks.PODZOL)
            || state.is(Blocks.MYCELIUM)
            || state.is(Blocks.MOSS_BLOCK)
            || state.is(Blocks.MUD);
    }
}

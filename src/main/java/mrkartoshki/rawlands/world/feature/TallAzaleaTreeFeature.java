package mrkartoshki.rawlands.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.tags.BlockTags;

import static mrkartoshki.rawlands.world.feature.TreeDecorHelper.estimateTip;
import static mrkartoshki.rawlands.world.feature.TreeDecorHelper.placeFoliageCluster;

public class TallAzaleaTreeFeature extends Feature<NoneFeatureConfiguration> {

    private static final BlockState OAK_LOG = Blocks.OAK_LOG.defaultBlockState();
    private static final BlockState AZALEA_LEAVES =
        Blocks.AZALEA_LEAVES.defaultBlockState().setValue(LeavesBlock.DISTANCE, 1);
    private static final BlockState FLOWERING_AZALEA_LEAVES =
        Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState().setValue(LeavesBlock.DISTANCE, 1);

    public TallAzaleaTreeFeature(Codec<NoneFeatureConfiguration> codec) {
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

        int height = 7 + random.nextInt(7);
        boolean splitTrunk = random.nextFloat() < 0.2;
        boolean isAncient = height >= 12;
        int mainHeight = splitTrunk ? 3 + random.nextInt(3) : height;

        for (int y = 0; y < mainHeight; y++) {
            BlockPos check = origin.above(y);
            if (!TreeBranchHelper.canReplace(level, check) && !level.getBlockState(check).is(BlockTags.LOGS)) {
                return false;
            }
        }

        TreeBranchHelper.generateBaseFlare(level, random, OAK_LOG, origin);

        TreeBranchHelper.generateTrunk(level, random, OAK_LOG, origin, mainHeight, 0.35, (pos, y, totalHeight) -> {
            if (splitTrunk) return;

            float progress = (float) y / totalHeight;

            if (y >= 4 && progress > 0.3 && random.nextFloat() < 0.5) {
                double bx = (random.nextDouble() - 0.5) * 2.0;
                double by = 0.2 + random.nextDouble() * 0.5;
                double bz = (random.nextDouble() - 0.5) * 2.0;
                int branchLen = 3 + random.nextInt(5);

                if (progress > 0.7) {
                    branchLen = Math.max(2, branchLen - 2);
                }

                TreeBranchHelper.generateBranch(level, random, OAK_LOG, pos, bx, by, bz, branchLen, 0);
            }

            if (y == totalHeight - 1) {
                int topBranches = 2 + random.nextInt(3);
                for (int b = 0; b < topBranches; b++) {
                    double angle = (b / (double) topBranches) * Math.PI * 2 + (random.nextDouble() - 0.5) * 0.8;
                    double bx = Math.cos(angle) * (0.5 + random.nextDouble() * 0.5);
                    double by = 0.5 + random.nextDouble() * 0.5;
                    double bz = Math.sin(angle) * (0.5 + random.nextDouble() * 0.5);
                    int branchLen = 2 + random.nextInt(3);
                    TreeBranchHelper.generateBranch(level, random, OAK_LOG, pos, bx, by, bz, branchLen, 1);
                    placeFoliageCluster(level, random, AZALEA_LEAVES, FLOWERING_AZALEA_LEAVES, 0.35f, estimateTip(pos, bx, by, bz, branchLen), 2);
                }
                placeFoliageCluster(level, random, AZALEA_LEAVES, FLOWERING_AZALEA_LEAVES, 0.35f, pos.above(), 2);
            }
        });

        if (splitTrunk) {
            BlockPos splitBase = origin.above(mainHeight);
            for (int fork = 0; fork < 2; fork++) {
                double angle = fork * Math.PI + (random.nextDouble() - 0.5) * 0.6;
                double dx = Math.cos(angle) * 0.6;
                double dz = Math.sin(angle) * 0.6;
                int forkHeight = (height - mainHeight) / 2 + random.nextInt(3);
                generateFork(level, random, splitBase, dx, dz, forkHeight);
            }
        }

        if (isAncient) {
            TreeBranchHelper.generateExposedRoots(level, random, OAK_LOG, origin);
        } else if (random.nextFloat() < 0.35) {
            TreeBranchHelper.generateExposedRoots(level, random, OAK_LOG, origin);
        }

        return true;
    }

    private static boolean isValidGround(BlockState state) {
        return state.is(BlockTags.DIRT)
            || state.is(Blocks.GRASS_BLOCK)
            || state.is(Blocks.PODZOL)
            || state.is(Blocks.COARSE_DIRT)
            || state.is(Blocks.MYCELIUM)
            || state.is(Blocks.MOSS_BLOCK)
            || state.is(Blocks.MUD);
    }

    /** Fork arm that adds foliage clusters at branch tips — azalea-specific. */
    private void generateFork(WorldGenLevel level, RandomSource random, BlockPos base, double dx, double dz, int height) {
        double px = base.getX() + 0.5;
        double py = base.getY() + 0.5;
        double pz = base.getZ() + 0.5;

        for (int i = 0; i < height; i++) {
            BlockPos pos = BlockPos.containing(px, py, pz);
            TreeBranchHelper.placeLog(level, pos, OAK_LOG, Direction.Axis.Y);

            if (i >= 2 && random.nextFloat() < 0.45f) {
                double bx = (random.nextDouble() - 0.5) * 2.0;
                double by = 0.3 + random.nextDouble() * 0.4;
                double bz = (random.nextDouble() - 0.5) * 2.0;
                int branchLen = 2 + random.nextInt(4);
                TreeBranchHelper.generateBranch(level, random, OAK_LOG, pos, bx, by, bz, branchLen, 1);
            }

            if (i == height - 1) {
                int topBranches = 1 + random.nextInt(3);
                for (int b = 0; b < topBranches; b++) {
                    double bx = (random.nextDouble() - 0.5) * 1.5;
                    double by = 0.5 + random.nextDouble() * 0.5;
                    double bz2 = (random.nextDouble() - 0.5) * 1.5;
                    int branchLen = 1 + random.nextInt(3);
                    TreeBranchHelper.generateBranch(level, random, OAK_LOG, pos, bx, by, bz2, branchLen, 2);
                    placeFoliageCluster(level, random, AZALEA_LEAVES, FLOWERING_AZALEA_LEAVES, 0.35f, estimateTip(pos, bx, by, bz2, branchLen), 1);
                }
                placeFoliageCluster(level, random, AZALEA_LEAVES, FLOWERING_AZALEA_LEAVES, 0.35f, pos.above(), 1);
            }

            px += dx;
            py += 1.0;
            pz += dz;

            dx += (random.nextDouble() - 0.5) * 0.15;
            dz += (random.nextDouble() - 0.5) * 0.15;
        }
    }

}

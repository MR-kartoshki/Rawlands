package mrkartoshki.rawlands.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.tags.BlockTags;

public class DeadDarkOakFeature extends Feature<NoneFeatureConfiguration> {

    private static final BlockState DARK_OAK_LOG = Blocks.DARK_OAK_LOG.defaultBlockState();

    public DeadDarkOakFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        if (!level.getBlockState(origin.below()).is(BlockTags.DIRT)) {
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

        generateBaseFlare(level, random, origin);

        TreeBranchHelper.generateTrunk(level, random, DARK_OAK_LOG, origin, mainHeight, 0.35, (pos, y, totalHeight) -> {
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

                TreeBranchHelper.generateBranch(level, random, DARK_OAK_LOG, pos, bx, by, bz, branchLen, 0);
            }

            if (y == totalHeight - 1) {
                int topBranches = 2 + random.nextInt(3);
                for (int b = 0; b < topBranches; b++) {
                    double angle = (b / (double) topBranches) * Math.PI * 2 + (random.nextDouble() - 0.5) * 0.8;
                    double bx = Math.cos(angle) * (0.5 + random.nextDouble() * 0.5);
                    double by = 0.5 + random.nextDouble() * 0.5;
                    double bz = Math.sin(angle) * (0.5 + random.nextDouble() * 0.5);
                    int branchLen = 2 + random.nextInt(3);
                    TreeBranchHelper.generateBranch(level, random, DARK_OAK_LOG, pos, bx, by, bz, branchLen, 1);
                }
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
            generateExposedRoots(level, random, origin);
        } else if (random.nextFloat() < 0.35) {
            generateExposedRoots(level, random, origin);
        }

        return true;
    }

    private void generateBaseFlare(WorldGenLevel level, RandomSource random, BlockPos origin) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (random.nextFloat() < 0.6) {
                BlockPos flarePos = origin.relative(dir);
                if (level.getBlockState(flarePos).is(BlockTags.DIRT) || TreeBranchHelper.canReplace(level, flarePos)) {
                    TreeBranchHelper.placeLog(level, flarePos, DARK_OAK_LOG, Direction.Axis.Y);
                }
            }
        }

        if (random.nextFloat() < 0.3) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                if (random.nextFloat() < 0.4) {
                    BlockPos flarePos = origin.above().relative(dir);
                    if (TreeBranchHelper.canReplace(level, flarePos)) {
                        TreeBranchHelper.placeLog(level, flarePos, DARK_OAK_LOG, Direction.Axis.Y);
                    }
                }
            }
        }
    }

    private void generateFork(WorldGenLevel level, RandomSource random, BlockPos base, double dx, double dz, int height) {
        double px = base.getX() + 0.5;
        double py = base.getY() + 0.5;
        double pz = base.getZ() + 0.5;

        for (int i = 0; i < height; i++) {
            BlockPos pos = BlockPos.containing(px, py, pz);
            TreeBranchHelper.placeLog(level, pos, DARK_OAK_LOG, Direction.Axis.Y);

            if (i >= 2 && random.nextFloat() < 0.45) {
                double bx = (random.nextDouble() - 0.5) * 2.0;
                double by = 0.3 + random.nextDouble() * 0.4;
                double bz = (random.nextDouble() - 0.5) * 2.0;
                int branchLen = 2 + random.nextInt(4);
                TreeBranchHelper.generateBranch(level, random, DARK_OAK_LOG, pos, bx, by, bz, branchLen, 1);
            }

            if (i == height - 1) {
                int topBranches = 1 + random.nextInt(3);
                for (int b = 0; b < topBranches; b++) {
                    double bx = (random.nextDouble() - 0.5) * 1.5;
                    double by = 0.5 + random.nextDouble() * 0.5;
                    double bz2 = (random.nextDouble() - 0.5) * 1.5;
                    int branchLen = 1 + random.nextInt(3);
                    TreeBranchHelper.generateBranch(level, random, DARK_OAK_LOG, pos, bx, by, bz2, branchLen, 2);
                }
            }

            px += dx;
            py += 1.0;
            pz += dz;

            dx += (random.nextDouble() - 0.5) * 0.15;
            dz += (random.nextDouble() - 0.5) * 0.15;
        }
    }

    private void generateExposedRoots(WorldGenLevel level, RandomSource random, BlockPos base) {
        int rootCount = 2 + random.nextInt(3);
        for (int r = 0; r < rootCount; r++) {
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos rootStart = base.below().relative(dir);

            for (int seg = 0; seg < 1 + random.nextInt(3); seg++) {
                if (level.getBlockState(rootStart).is(BlockTags.DIRT)) {
                    TreeBranchHelper.placeLog(level, rootStart, DARK_OAK_LOG, dir.getAxis());
                    rootStart = rootStart.relative(dir);
                    if (random.nextFloat() < 0.3) {
                        rootStart = rootStart.below();
                    }
                } else {
                    break;
                }
            }
        }
    }
}

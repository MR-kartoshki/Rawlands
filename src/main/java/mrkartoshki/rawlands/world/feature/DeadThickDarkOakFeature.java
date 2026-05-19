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

public class DeadThickDarkOakFeature extends Feature<NoneFeatureConfiguration> {

    private static final BlockState DARK_OAK_LOG = Blocks.DARK_OAK_LOG.defaultBlockState();

    public DeadThickDarkOakFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        for (int dx = 0; dx <= 1; dx++) {
            for (int dz = 0; dz <= 1; dz++) {
                if (!level.getBlockState(origin.offset(dx, -1, dz)).is(BlockTags.DIRT)) {
                    return false;
                }
            }
        }

        int height = 9 + random.nextInt(8);
        boolean splitTrunk = random.nextFloat() < 0.25;
        int mainHeight = splitTrunk ? 4 + random.nextInt(3) : height;

        for (int y = 0; y < mainHeight; y++) {
            for (int dx = 0; dx <= 1; dx++) {
                for (int dz = 0; dz <= 1; dz++) {
                    BlockPos check = origin.offset(dx, y, dz);
                    if (!TreeBranchHelper.canReplace(level, check) && !level.getBlockState(check).is(BlockTags.LOGS)) {
                        return false;
                    }
                }
            }
        }

        generateBaseFlare(level, random, origin);

        double offsetX = 0;
        double offsetZ = 0;

        for (int y = 0; y < mainHeight; y++) {
            int roundedX = (int) Math.round(offsetX);
            int roundedZ = (int) Math.round(offsetZ);

            for (int dx = 0; dx <= 1; dx++) {
                for (int dz = 0; dz <= 1; dz++) {
                    BlockPos pos = origin.offset(roundedX + dx, y, roundedZ + dz);
                    TreeBranchHelper.placeLog(level, pos, DARK_OAK_LOG, Direction.Axis.Y);
                }
            }

            BlockPos branchAnchor = origin.offset(roundedX, y, roundedZ);
            float progress = (float) y / mainHeight;

            if (!splitTrunk && y >= 5 && progress > 0.35 && random.nextFloat() < 0.55) {
                double bx = (random.nextDouble() - 0.5) * 2.0;
                double by = 0.2 + random.nextDouble() * 0.5;
                double bz = (random.nextDouble() - 0.5) * 2.0;
                int branchLen = 4 + random.nextInt(6);
                if (progress > 0.7) branchLen = Math.max(2, branchLen - 2);

                BlockPos branchStart = branchAnchor.offset(
                    bx > 0 ? 1 : 0, 0, bz > 0 ? 1 : 0
                );
                TreeBranchHelper.generateBranch(level, random, DARK_OAK_LOG, branchStart, bx, by, bz, branchLen, 0);
            }

            if (!splitTrunk && y == mainHeight - 1) {
                int topBranches = 3 + random.nextInt(4);
                for (int b = 0; b < topBranches; b++) {
                    double angle = (b / (double) topBranches) * Math.PI * 2 + (random.nextDouble() - 0.5) * 0.6;
                    double bx = Math.cos(angle) * (0.6 + random.nextDouble() * 0.5);
                    double by = 0.4 + random.nextDouble() * 0.6;
                    double bz = Math.sin(angle) * (0.6 + random.nextDouble() * 0.5);
                    int branchLen = 3 + random.nextInt(4);

                    BlockPos branchStart = branchAnchor.offset(
                        bx > 0 ? 1 : 0, 0, bz > 0 ? 1 : 0
                    );
                    TreeBranchHelper.generateBranch(level, random, DARK_OAK_LOG, branchStart, bx, by, bz, branchLen, 1);
                }
            }

            offsetX += (random.nextDouble() - 0.5) * 0.3;
            offsetZ += (random.nextDouble() - 0.5) * 0.3;
            offsetX = Math.max(-2.0, Math.min(2.0, offsetX));
            offsetZ = Math.max(-2.0, Math.min(2.0, offsetZ));
        }

        if (splitTrunk) {
            BlockPos splitBase = origin.offset(
                (int) Math.round(offsetX), mainHeight, (int) Math.round(offsetZ)
            );
            int forkCount = 2 + (random.nextFloat() < 0.3 ? 1 : 0);
            for (int fork = 0; fork < forkCount; fork++) {
                double angle = (fork / (double) forkCount) * Math.PI * 2 + (random.nextDouble() - 0.5) * 0.5;
                double dx = Math.cos(angle) * 0.7;
                double dz = Math.sin(angle) * 0.7;
                int forkHeight = (height - mainHeight) / 2 + random.nextInt(4);
                generateFork(level, random, splitBase, dx, dz, forkHeight);
            }
        }

        generateExposedRoots(level, random, origin);

        return true;
    }

    private void generateBaseFlare(WorldGenLevel level, RandomSource random, BlockPos origin) {
        int[][] flareOffsets = {
            {-1, 0}, {-1, 1}, {2, 0}, {2, 1},
            {0, -1}, {1, -1}, {0, 2}, {1, 2},
            {-1, -1}, {-1, 2}, {2, -1}, {2, 2}
        };
        for (int[] off : flareOffsets) {
            if (random.nextFloat() < 0.55) {
                BlockPos flarePos = origin.offset(off[0], 0, off[1]);
                if (level.getBlockState(flarePos).is(BlockTags.DIRT) || TreeBranchHelper.canReplace(level, flarePos)) {
                    TreeBranchHelper.placeLog(level, flarePos, DARK_OAK_LOG, Direction.Axis.Y);
                }
            }
        }

        if (random.nextFloat() < 0.4) {
            for (int[] off : flareOffsets) {
                if (random.nextFloat() < 0.3) {
                    BlockPos flarePos = origin.offset(off[0], 1, off[1]);
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

            if (i >= 2 && random.nextFloat() < 0.5) {
                double bx = (random.nextDouble() - 0.5) * 2.0;
                double by = 0.3 + random.nextDouble() * 0.4;
                double bz = (random.nextDouble() - 0.5) * 2.0;
                int branchLen = 3 + random.nextInt(5);
                TreeBranchHelper.generateBranch(level, random, DARK_OAK_LOG, pos, bx, by, bz, branchLen, 1);
            }

            if (i == height - 1) {
                int topBranches = 2 + random.nextInt(3);
                for (int b = 0; b < topBranches; b++) {
                    double bx = (random.nextDouble() - 0.5) * 1.5;
                    double by = 0.5 + random.nextDouble() * 0.5;
                    double bz = (random.nextDouble() - 0.5) * 1.5;
                    int branchLen = 2 + random.nextInt(3);
                    TreeBranchHelper.generateBranch(level, random, DARK_OAK_LOG, pos, bx, by, bz, branchLen, 2);
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
        int rootCount = 3 + random.nextInt(4);
        for (int r = 0; r < rootCount; r++) {
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int startOffset = random.nextInt(2);
            BlockPos rootStart = base.offset(
                dir.getAxis() == Direction.Axis.X ? (dir.getStepX() > 0 ? 2 : -1) : startOffset,
                -1,
                dir.getAxis() == Direction.Axis.Z ? (dir.getStepZ() > 0 ? 2 : -1) : startOffset
            );

            for (int seg = 0; seg < 2 + random.nextInt(3); seg++) {
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

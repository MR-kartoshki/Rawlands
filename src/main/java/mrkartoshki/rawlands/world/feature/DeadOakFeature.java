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

public class DeadOakFeature extends Feature<NoneFeatureConfiguration> {

    private static final BlockState OAK_LOG = Blocks.OAK_LOG.defaultBlockState();

    public DeadOakFeature(Codec<NoneFeatureConfiguration> codec) {
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

        int height = 5 + random.nextInt(5);
        boolean snapped = random.nextFloat() < 0.15;
        int effectiveHeight = snapped ? 2 + random.nextInt(3) : height;

        for (int y = 0; y < effectiveHeight; y++) {
            BlockPos check = origin.above(y);
            if (!TreeBranchHelper.canReplace(level, check) && !level.getBlockState(check).is(BlockTags.LOGS)) {
                return false;
            }
        }

        TreeBranchHelper.generateTrunk(level, random, OAK_LOG, origin, effectiveHeight, 0.4, (pos, y, totalHeight) -> {
            if (snapped) return;

            float progress = (float) y / totalHeight;

            if (y >= 3 && progress > 0.35 && random.nextFloat() < 0.45) {
                double bx = (random.nextDouble() - 0.5) * 2.0;
                double by = 0.3 + random.nextDouble() * 0.5;
                double bz = (random.nextDouble() - 0.5) * 2.0;
                int branchLen = 2 + random.nextInt(4);
                if (progress > 0.7) branchLen = Math.max(1, branchLen - 1);

                if (random.nextFloat() < 0.2) {
                    branchLen = Math.max(1, branchLen / 2);
                }

                TreeBranchHelper.generateBranch(level, random, OAK_LOG, pos, bx, by, bz, branchLen, 1);
            }

            if (y == totalHeight - 1) {
                int topBranches = 1 + random.nextInt(3);
                for (int b = 0; b < topBranches; b++) {
                    double bx = (random.nextDouble() - 0.5) * 1.5;
                    double by = 0.6 + random.nextDouble() * 0.4;
                    double bz = (random.nextDouble() - 0.5) * 1.5;
                    int branchLen = 1 + random.nextInt(3);
                    TreeBranchHelper.generateBranch(level, random, OAK_LOG, pos, bx, by, bz, branchLen, 2);
                }
            }
        });

        if (snapped && random.nextFloat() < 0.5) {
            placeStumpDebris(level, random, origin, effectiveHeight);
        }

        if (random.nextFloat() < 0.25) {
            placeExposedRoots(level, random, origin);
        }

        return true;
    }

    private void placeStumpDebris(WorldGenLevel level, RandomSource random, BlockPos base, int stumpHeight) {
        BlockPos top = base.above(stumpHeight);
        Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        int logLen = 2 + random.nextInt(3);
        Direction.Axis axis = dir.getAxis();

        for (int i = 0; i < logLen; i++) {
            BlockPos logPos = top.relative(dir, i + 1);
            BlockPos below = logPos.below();
            if (TreeBranchHelper.canReplace(level, logPos) && !level.getBlockState(below).isAir()) {
                TreeBranchHelper.placeLog(level, logPos, OAK_LOG, axis);
            } else {
                break;
            }
        }
    }

    private void placeExposedRoots(WorldGenLevel level, RandomSource random, BlockPos base) {
        int rootCount = 1 + random.nextInt(3);
        for (int r = 0; r < rootCount; r++) {
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos rootPos = base.below().relative(dir);
            if (level.getBlockState(rootPos).is(BlockTags.DIRT)) {
                TreeBranchHelper.placeLog(level, rootPos, OAK_LOG, dir.getAxis());
                if (random.nextFloat() < 0.4) {
                    BlockPos ext = rootPos.relative(dir);
                    if (level.getBlockState(ext).is(BlockTags.DIRT)) {
                        TreeBranchHelper.placeLog(level, ext, OAK_LOG, dir.getAxis());
                    }
                }
            }
        }
    }
}

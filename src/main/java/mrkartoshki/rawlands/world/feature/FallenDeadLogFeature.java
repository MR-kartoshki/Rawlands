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

public class FallenDeadLogFeature extends Feature<NoneFeatureConfiguration> {

    public FallenDeadLogFeature(Codec<NoneFeatureConfiguration> codec) {
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

        boolean useDarkOak = random.nextFloat() < 0.35;
        BlockState log = useDarkOak ? Blocks.DARK_OAK_LOG.defaultBlockState() : Blocks.OAK_LOG.defaultBlockState();

        Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        Direction.Axis axis = dir.getAxis();
        int length = 4 + random.nextInt(7);

        boolean hasStump = random.nextFloat() < 0.5;
        if (hasStump) {
            int stumpHeight = 1 + random.nextInt(2);
            for (int y = 0; y < stumpHeight; y++) {
                BlockPos stumpPos = origin.above(y);
                if (TreeBranchHelper.canReplace(level, stumpPos)) {
                    TreeBranchHelper.placeLog(level, stumpPos, log, Direction.Axis.Y);
                }
            }
        }

        BlockPos logStart = hasStump ? origin.relative(dir) : origin;

        int validLength = 0;
        for (int i = 0; i < length; i++) {
            BlockPos logPos = logStart.relative(dir, i);
            BlockPos below = logPos.below();
            if (!level.getBlockState(below).isAir() && TreeBranchHelper.canReplace(level, logPos)) {
                validLength++;
            } else if (level.getBlockState(below).isAir()) {
                BlockPos lower = logPos.below();
                if (!level.getBlockState(lower.below()).isAir() && TreeBranchHelper.canReplace(level, lower)) {
                    validLength++;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        if (validLength < 3) return false;

        for (int i = 0; i < length; i++) {
            BlockPos logPos = logStart.relative(dir, i);
            BlockPos below = logPos.below();

            if (!level.getBlockState(below).isAir() && TreeBranchHelper.canReplace(level, logPos)) {
                TreeBranchHelper.placeLog(level, logPos, log, axis);

                if (random.nextFloat() < 0.06) {
                    BlockPos mushroom = logPos.above();
                    if (level.getBlockState(mushroom).isAir()) {
                        level.setBlock(mushroom, Blocks.BROWN_MUSHROOM.defaultBlockState(), 2);
                    }
                }
            } else if (level.getBlockState(below).isAir()) {
                BlockPos lower = logPos.below();
                if (!level.getBlockState(lower.below()).isAir() && TreeBranchHelper.canReplace(level, lower)) {
                    TreeBranchHelper.placeLog(level, lower, log, axis);
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        return true;
    }
}

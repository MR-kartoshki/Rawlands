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

public class FallenDeadLogFeature extends Feature<NoneFeatureConfiguration> {

    public FallenDeadLogFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        if (!TreeBranchHelper.isDirtLike(level.getBlockState(origin.below()))) {
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
        int groundY = logStart.getY();

        // Resolve every segment's position first (ground level, or stepped down one block where
        // the terrain drops away). Placement below reuses these, so each block state is read once.
        BlockPos[] placements = new BlockPos[length];
        int validLength = 0;
        for (int i = 0; i < length; i++) {
            BlockPos logPos = logStart.relative(dir, i);
            BlockPos below = logPos.below();
            if (!level.getBlockState(below).isAir()) {
                if (!TreeBranchHelper.canReplace(level, logPos)) break;
                placements[i] = logPos;
            } else if (!level.getBlockState(below.below()).isAir() && TreeBranchHelper.canReplace(level, below)) {
                placements[i] = below;
            } else {
                break;
            }
            validLength++;
        }
        if (validLength < 3) return false;

        for (int i = 0; i < validLength; i++) {
            TreeBranchHelper.placeLog(level, placements[i], log, axis);

            if (placements[i].getY() == groundY && random.nextFloat() < 0.06) {
                BlockPos mushroom = placements[i].above();
                if (level.getBlockState(mushroom).isAir()) {
                    level.setBlock(mushroom, Blocks.BROWN_MUSHROOM.defaultBlockState(), 2);
                }
            }
        }

        return true;
    }
}

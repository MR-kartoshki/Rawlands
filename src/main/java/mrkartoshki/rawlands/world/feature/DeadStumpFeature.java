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

public class DeadStumpFeature extends Feature<NoneFeatureConfiguration> {

    public DeadStumpFeature(Codec<NoneFeatureConfiguration> codec) {
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

        int stumpHeight = 1 + random.nextInt(3);

        for (int y = 0; y < stumpHeight; y++) {
            BlockPos pos = origin.above(y);
            if (!TreeBranchHelper.canReplace(level, pos)) return false;
            TreeBranchHelper.placeLog(level, pos, log, Direction.Axis.Y);
        }

        if (useDarkOak && random.nextFloat() < 0.5) {
            Direction flareDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos flarePos = origin.relative(flareDir);
            if (TreeBranchHelper.isDirtLike(level.getBlockState(flarePos)) || TreeBranchHelper.canReplace(level, flarePos)) {
                TreeBranchHelper.placeLog(level, flarePos, log, Direction.Axis.Y);
            }
        }

        if (random.nextFloat() < 0.4) {
            Direction rootDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos rootPos = origin.below().relative(rootDir);
            if (TreeBranchHelper.isDirtLike(level.getBlockState(rootPos))) {
                TreeBranchHelper.placeLog(level, rootPos, log, rootDir.getAxis());
            }
        }

        return true;
    }
}

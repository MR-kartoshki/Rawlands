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

public class SmallAzaleaTreeFeature extends Feature<NoneFeatureConfiguration> {

    private static final BlockState OAK_LOG = Blocks.OAK_LOG.defaultBlockState();
    private static final BlockState AZALEA_LEAVES =
        Blocks.AZALEA_LEAVES.defaultBlockState().setValue(LeavesBlock.DISTANCE, 1);
    private static final BlockState FLOWERING_AZALEA_LEAVES =
        Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState().setValue(LeavesBlock.DISTANCE, 1);

    public SmallAzaleaTreeFeature(Codec<NoneFeatureConfiguration> codec) {
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

        int height = 4 + random.nextInt(3);

        for (int y = 0; y < height; y++) {
            BlockPos check = origin.above(y);
            if (!TreeBranchHelper.canReplaceOrIsLog(level, check)) {
                return false;
            }
        }

        TreeBranchHelper.generateBaseFlare(level, random, OAK_LOG, origin);

        // Generate curved trunk with significant drift for that twisty look
        TreeBranchHelper.generateTrunk(level, random, OAK_LOG, origin, height, 0.6, (pos, y, totalHeight) -> {
            float progress = (float) y / totalHeight;

            // Generate branches along the trunk
            if (y >= 1 && progress > 0.25 && random.nextFloat() < 0.55) {
                double bx = (random.nextDouble() - 0.5) * 2.0;
                double by = 0.3 + random.nextDouble() * 0.3;
                double bz = (random.nextDouble() - 0.5) * 2.0;
                int branchLen = 2 + random.nextInt(2);

                TreeBranchHelper.generateBranch(level, random, OAK_LOG, pos, bx, by, bz, branchLen, 0);

                // Place foliage at branch end
                placeFoliageCluster(level, random, AZALEA_LEAVES, FLOWERING_AZALEA_LEAVES, 0.3f,
                    estimateTip(pos, bx, by, bz, branchLen), 2);
            }

            // Top of trunk gets foliage cluster
            if (y == totalHeight - 1) {
                int topBranches = 1 + random.nextInt(2);
                for (int b = 0; b < topBranches; b++) {
                    double bx = (random.nextDouble() - 0.5) * 1.5;
                    double by = 0.4 + random.nextDouble() * 0.4;
                    double bz = (random.nextDouble() - 0.5) * 1.5;
                    int branchLen = 1 + random.nextInt(2);

                    TreeBranchHelper.generateBranch(level, random, OAK_LOG, pos, bx, by, bz, branchLen, 1);
                    placeFoliageCluster(level, random, AZALEA_LEAVES, FLOWERING_AZALEA_LEAVES, 0.3f,
                        estimateTip(pos, bx, by, bz, branchLen), 2);
                }
                placeFoliageCluster(level, random, AZALEA_LEAVES, FLOWERING_AZALEA_LEAVES, 0.3f, pos.above(), 2);
            }
        });

        if (random.nextFloat() < 0.3) {
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
}

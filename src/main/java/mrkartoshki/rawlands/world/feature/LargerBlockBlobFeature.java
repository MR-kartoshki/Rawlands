package mrkartoshki.rawlands.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockBlobConfiguration;

public class LargerBlockBlobFeature extends Feature<BlockBlobConfiguration> {

    public LargerBlockBlobFeature(Codec<BlockBlobConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockBlobConfiguration> context) {
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockBlobConfiguration config = context.config();

        while (origin.getY() > level.getMinY() + 3 && !config.canPlaceOn().test(level, origin.below())) {
            origin = origin.below();
        }

        if (origin.getY() <= level.getMinY() + 3) {
            return false;
        }

        int blobCount = 2 + random.nextInt(2);
        BlockPos center = origin;

        for (int c = 0; c < blobCount; c++) {
            double rx = 1.0 + random.nextDouble() * 1.8;
            double ry = 0.7 + random.nextDouble() * 1.3;
            double rz = 1.0 + random.nextDouble() * 1.8;

            int minX = (int) Math.floor(-rx) - 1;
            int maxX = (int) Math.ceil(rx) + 1;
            int minY = (int) Math.floor(-ry) - 1;
            int maxY = (int) Math.ceil(ry) + 1;
            int minZ = (int) Math.floor(-rz) - 1;
            int maxZ = (int) Math.ceil(rz) + 1;

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        double nx = x / rx;
                        double ny = y / ry;
                        double nz = z / rz;
                        double distSq = nx * nx + ny * ny + nz * nz;

                        double threshold = 1.0 + (random.nextDouble() - 0.5) * 0.15;

                        if (distSq <= threshold * threshold) {
                            BlockPos blockPos = center.offset(x, y, z);
                            BlockState existing = level.getBlockState(blockPos);
                            if (existing.isAir() || existing.is(BlockTags.REPLACEABLE_BY_TREES)
                                    || existing.is(BlockTags.DIRT) || existing.is(BlockTags.BASE_STONE_OVERWORLD)) {
                                level.setBlock(blockPos, config.state(), 3);
                            }
                        }
                    }
                }
            }

            center = center.offset(
                -1 + random.nextInt(3),
                -random.nextInt(2),
                -1 + random.nextInt(3)
            );
        }

        return true;
    }
}

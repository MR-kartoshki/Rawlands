package mrkartoshki.rawlands.world.feature;

import com.mojang.serialization.Codec;
import mrkartoshki.rawlands.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;


public class SequoiaTreeFeature extends Feature<NoneFeatureConfiguration> {

    private static final BlockState LOG = ModBlocks.SEQUOIA_LOG.defaultBlockState();
    private static final BlockState LEAVES = ModBlocks.SEQUOIA_LEAVES.defaultBlockState()
        .setValue(LeavesBlock.DISTANCE, 1);

    public SequoiaTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        int coreSize = random.nextFloat() < 0.35f ? 3 : 2;
        for (int dx = 0; dx < coreSize; dx++) {
            for (int dz = 0; dz < coreSize; dz++) {
                BlockPos ground = origin.offset(dx, -1, dz);
                if (!TreeBranchHelper.isDirtLike(level.getBlockState(ground))) {
                    return false;
                }
            }
        }

        int buttressHeight = 4 + random.nextInt(3);
        int taperHeight = 2 + random.nextInt(2);
        int mainHeight = 14 + random.nextInt(29);
        if (mainHeight < buttressHeight + taperHeight + 4) {
            mainHeight = buttressHeight + taperHeight + 4;
        }
        int coreHeight = mainHeight - buttressHeight - taperHeight;
        int heightBonus = Math.max(0, mainHeight - 22);
        int crownHeight = 8 + random.nextInt(9) + heightBonus / 4;
        int totalHeight = mainHeight + crownHeight;
        for (int y = 0; y < totalHeight; y++) {
            int footprint = y < buttressHeight + coreHeight ? coreSize : 1;
            for (int dx = 0; dx < footprint; dx++) {
                for (int dz = 0; dz < footprint; dz++) {
                    BlockPos check = origin.offset(dx, y, dz);
                    if (!TreeBranchHelper.canReplaceOrIsLog(level, check)) {
                        return false;
                    }
                }
            }
        }

        for (int y = 0; y < buttressHeight; y++) {
            float keepChance = lerp(0.95f, 0.55f, y / (float) buttressHeight);
            placeRingLayer(level, random, LOG, origin, y, -1, coreSize + 2, coreSize, keepChance);
        }

        for (int y = buttressHeight; y < buttressHeight + coreHeight; y++) {
            for (int dx = 0; dx < coreSize; dx++) {
                for (int dz = 0; dz < coreSize; dz++) {
                    TreeBranchHelper.placeLog(level, origin.offset(dx, y, dz), LOG, Direction.Axis.Y);
                }
            }
        }

        int centerX = coreSize / 2;
        int centerZ = coreSize / 2;

        for (int i = 0; i < taperHeight; i++) {
            int y = buttressHeight + coreHeight + i;
            float progress = taperHeight <= 1 ? 1f : i / (float) (taperHeight - 1);
            float keepChance = lerp(0.9f, 0f, progress);
            for (int dx = 0; dx < coreSize; dx++) {
                for (int dz = 0; dz < coreSize; dz++) {
                    boolean isCenter = dx == centerX && dz == centerZ;
                    if (!isCenter && random.nextFloat() >= keepChance) continue;
                    TreeBranchHelper.placeLog(level, origin.offset(dx, y, dz), LOG, Direction.Axis.Y);
                }
            }
        }

        if (coreSize == 2) {
            TreeBranchHelper.generateThickBaseFlare(level, random, LOG, origin);
            TreeBranchHelper.generateThickExposedRoots(level, random, LOG, origin);
        } else {
            TreeBranchHelper.generateThickBaseFlare(level, random, LOG, origin);
            TreeBranchHelper.generateThickBaseFlare(level, random, LOG, origin.offset(1, 0, 0));
            TreeBranchHelper.generateThickExposedRoots(level, random, LOG, origin);
            TreeBranchHelper.generateThickExposedRoots(level, random, LOG, origin.offset(1, 0, 1));
        }

        int foliageStartY = Math.min(7 + random.nextInt(4), mainHeight - 4);

        generateSpruceCrown(level, random, origin, centerX, centerZ, coreSize, foliageStartY, mainHeight, crownHeight);

        return true;
    }

    private static void generateSpruceCrown(
        WorldGenLevel level, RandomSource random, BlockPos origin, int centerX, int centerZ,
        int coreSize, int foliageStartY, int mainHeight, int crownHeight
    ) {
        int maxRadius = coreSize + 2 + Math.min(2, Math.max(0, mainHeight - 22) / 8);
        int topY = mainHeight + crownHeight;
        int span = Math.max(1, topY - foliageStartY);

        for (int y = mainHeight; y < topY; y++) {
            TreeBranchHelper.placeLog(level, origin.offset(centerX, y, centerZ), LOG, Direction.Axis.Y);
        }

        int ringIndex = 0;
        for (int y = foliageStartY; y < topY; y++, ringIndex++) {
            BlockPos pos = origin.offset(centerX, y, centerZ);

            float progress = (y - foliageStartY) / (float) span;
            int radius = Math.round(maxRadius * (1f - progress));
            if (ringIndex % 4 == 3) {
                radius = Math.max(0, radius - 1);
            }
            if (y >= topY - 2) {
                radius = Math.min(radius, 1);
            }

            if (radius > 0) {
                placeFoliageRing(level, random, LEAVES, pos, radius);
            }
        }

        BlockPos tip = origin.offset(centerX, topY, centerZ);
        if (TreeBranchHelper.canReplace(level, tip)) {
            level.setBlock(tip, LEAVES, 3);
        }
    }

    private static void placeFoliageRing(
        WorldGenLevel level, RandomSource random, BlockState leaf, BlockPos center, int radius
    ) {
        int radiusSq = radius * radius + 1;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int distSq = dx * dx + dz * dz;
                if (distSq > radiusSq) continue;
                if (distSq == 0) continue;
                if (random.nextFloat() < 0.02f) continue;
                BlockPos leafPos = center.offset(dx, 0, dz);
                if (!TreeBranchHelper.canReplace(level, leafPos)) continue;
                level.setBlock(leafPos, leaf, 3);
            }
        }
    }

    private static void placeRingLayer(
        WorldGenLevel level, RandomSource random, BlockState logState,
        BlockPos origin, int y, int originOffset, int footprintSize, int coreSize, float keepChance
    ) {
        for (int dx = originOffset; dx < originOffset + footprintSize; dx++) {
            for (int dz = originOffset; dz < originOffset + footprintSize; dz++) {
                boolean isCore = dx >= 0 && dx < coreSize && dz >= 0 && dz < coreSize;
                if (!isCore && random.nextFloat() >= keepChance) continue;
                TreeBranchHelper.placeLog(level, origin.offset(dx, y, dz), logState, Direction.Axis.Y);
            }
        }
    }

    private static float lerp(float from, float to, float t) {
        t = Math.max(0f, Math.min(1f, t));
        return from + (to - from) * t;
    }
}

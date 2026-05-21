package mrkartoshki.rawlands.world.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public class TreeDecorHelper {

    /**
     * Estimates the endpoint of a branch given its origin, unnormalised direction,
     * and step count. Used to position foliage clusters at branch tips.
     */
    public static BlockPos estimateTip(BlockPos origin, double dx, double dy, double dz, int length) {
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len == 0) return origin;
        return BlockPos.containing(
            origin.getX() + 0.5 + (dx / len) * length,
            origin.getY() + 0.5 + (dy / len) * length,
            origin.getZ() + 0.5 + (dz / len) * length
        );
    }

    /**
     * Places a roughly spherical foliage cluster centred on {@code center}.
     * About 15% of positions are skipped as natural gaps.
     *
     * @param primaryLeaf    the dominant leaf block
     * @param secondaryLeaf  optional accent leaf block (pass null to use only primaryLeaf)
     * @param secondaryChance probability [0,1] to use secondaryLeaf instead of primaryLeaf
     * @param radius         sphere radius in blocks
     */
    public static void placeFoliageCluster(
        WorldGenLevel level, RandomSource random,
        BlockState primaryLeaf, BlockState secondaryLeaf, float secondaryChance,
        BlockPos center, int radius
    ) {
        int radiusSq = radius * radius + 1;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dy * dy + dz * dz > radiusSq) continue;
                    if (random.nextFloat() < 0.15f) continue;
                    BlockPos leafPos = center.offset(dx, dy, dz);
                    if (!TreeBranchHelper.canReplace(level, leafPos)) continue;
                    BlockState leaf = (secondaryLeaf != null && random.nextFloat() < secondaryChance)
                        ? secondaryLeaf : primaryLeaf;
                    level.setBlock(leafPos, leaf, 3);
                }
            }
        }
    }
}

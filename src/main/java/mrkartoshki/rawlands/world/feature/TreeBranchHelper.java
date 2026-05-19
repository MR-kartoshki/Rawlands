package mrkartoshki.rawlands.world.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;

public class TreeBranchHelper {

    private static final int MAX_BRANCH_DEPTH = 4;

    public static boolean canReplace(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir()
            || state.is(BlockTags.LEAVES)
            || state.is(BlockTags.REPLACEABLE_BY_TREES)
            || state.is(Blocks.WATER)
            || state.is(Blocks.VINE);
    }

    public static void placeLog(WorldGenLevel level, BlockPos pos, BlockState logState, Direction.Axis axis) {
        if (!canReplace(level, pos)) return;
        BlockState oriented = logState;
        if (oriented.hasProperty(RotatedPillarBlock.AXIS)) {
            oriented = oriented.setValue(RotatedPillarBlock.AXIS, axis);
        }
        level.setBlock(pos, oriented, 3);
    }

    public static Direction.Axis dominantAxis(double dx, double dy, double dz) {
        double ax = Math.abs(dx);
        double ay = Math.abs(dy);
        double az = Math.abs(dz);
        if (ay >= ax && ay >= az) return Direction.Axis.Y;
        if (ax >= az) return Direction.Axis.X;
        return Direction.Axis.Z;
    }

    public static void generateBranch(
        WorldGenLevel level, RandomSource random, BlockState logState,
        BlockPos start, double dirX, double dirY, double dirZ,
        int length, int depth
    ) {
        if (depth > MAX_BRANCH_DEPTH || length <= 0) return;

        double px = start.getX() + 0.5;
        double py = start.getY() + 0.5;
        double pz = start.getZ() + 0.5;

        double mag = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        if (mag < 0.001) return;
        double nx = dirX / mag;
        double ny = dirY / mag;
        double nz = dirZ / mag;

        BlockPos prev = null;
        for (int i = 0; i < length; i++) {
            BlockPos current = BlockPos.containing(px, py, pz);

            if (prev == null || !current.equals(prev)) {
                if (!canReplace(level, current) && !level.getBlockState(current).is(BlockTags.LOGS)) {
                    break;
                }
                placeLog(level, current, logState, dominantAxis(nx, ny, nz));
                prev = current;
            }

            px += nx;
            py += ny;
            pz += nz;

            nx += (random.nextDouble() - 0.5) * 0.3;
            ny += (random.nextDouble() - 0.5) * 0.15;
            nz += (random.nextDouble() - 0.5) * 0.3;

            ny = Math.max(ny, -0.4);

            mag = Math.sqrt(nx * nx + ny * ny + nz * nz);
            if (mag > 0.001) {
                nx /= mag;
                ny /= mag;
                nz /= mag;
            }

            if (i > 1 && random.nextFloat() < 0.3 && depth < MAX_BRANCH_DEPTH) {
                int subLength = Math.max(1, length - i - random.nextInt(2));
                double forkX = nx + (random.nextDouble() - 0.5) * 0.8;
                double forkY = ny + random.nextDouble() * 0.3;
                double forkZ = nz + (random.nextDouble() - 0.5) * 0.8;
                generateBranch(level, random, logState, current, forkX, forkY, forkZ, subLength, depth + 1);
            }
        }

        if (depth < 2 && random.nextFloat() < 0.3) {
            BlockPos tip = BlockPos.containing(px, py, pz);
            if (canReplace(level, tip)) {
                placeLog(level, tip, logState, Direction.Axis.Y);
            }
        }
    }

    public static void generateTrunk(
        WorldGenLevel level, RandomSource random, BlockState logState,
        BlockPos base, int height, double maxDrift, TrunkCallback callback
    ) {
        double offsetX = 0;
        double offsetZ = 0;

        for (int y = 0; y < height; y++) {
            BlockPos pos = base.offset((int) Math.round(offsetX), y, (int) Math.round(offsetZ));
            placeLog(level, pos, logState, Direction.Axis.Y);

            if (callback != null) {
                callback.onTrunkBlock(pos, y, height);
            }

            offsetX += (random.nextDouble() - 0.5) * maxDrift;
            offsetZ += (random.nextDouble() - 0.5) * maxDrift;
            offsetX = clamp(offsetX, -2.0, 2.0);
            offsetZ = clamp(offsetZ, -2.0, 2.0);
        }
    }

    private static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    @FunctionalInterface
    public interface TrunkCallback {
        void onTrunkBlock(BlockPos pos, int y, int totalHeight);
    }
}

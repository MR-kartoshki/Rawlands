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

    // -----------------------------------------------------------------------
    // Shared structural helpers used by dead-tree and tall-azalea features
    // -----------------------------------------------------------------------

    /** Cardinal base flare for a 1×1 trunk. */
    public static void generateBaseFlare(WorldGenLevel level, RandomSource random, BlockState logState, BlockPos origin) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (random.nextFloat() < 0.6f) {
                BlockPos flarePos = origin.relative(dir);
                if (level.getBlockState(flarePos).is(BlockTags.DIRT) || canReplace(level, flarePos)) {
                    placeLog(level, flarePos, logState, Direction.Axis.Y);
                }
            }
        }
        if (random.nextFloat() < 0.3f) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                if (random.nextFloat() < 0.4f) {
                    BlockPos flarePos = origin.above().relative(dir);
                    if (canReplace(level, flarePos)) {
                        placeLog(level, flarePos, logState, Direction.Axis.Y);
                    }
                }
            }
        }
    }

    /** 12-position base flare for a 2×2 trunk. */
    public static void generateThickBaseFlare(WorldGenLevel level, RandomSource random, BlockState logState, BlockPos origin) {
        int[][] flareOffsets = {
            {-1, 0}, {-1, 1}, {2, 0}, {2, 1},
            {0, -1}, {1, -1}, {0, 2}, {1, 2},
            {-1, -1}, {-1, 2}, {2, -1}, {2, 2}
        };
        for (int[] off : flareOffsets) {
            if (random.nextFloat() < 0.55f) {
                BlockPos flarePos = origin.offset(off[0], 0, off[1]);
                if (level.getBlockState(flarePos).is(BlockTags.DIRT) || canReplace(level, flarePos)) {
                    placeLog(level, flarePos, logState, Direction.Axis.Y);
                }
            }
        }
        if (random.nextFloat() < 0.4f) {
            for (int[] off : flareOffsets) {
                if (random.nextFloat() < 0.3f) {
                    BlockPos flarePos = origin.offset(off[0], 1, off[1]);
                    if (canReplace(level, flarePos)) {
                        placeLog(level, flarePos, logState, Direction.Axis.Y);
                    }
                }
            }
        }
    }

    /** Diverging single-trunk fork arm. Used by 1×1 dead-tree features. */
    public static void generateFork(
        WorldGenLevel level, RandomSource random, BlockState logState,
        BlockPos base, double dx, double dz, int height
    ) {
        double px = base.getX() + 0.5;
        double py = base.getY() + 0.5;
        double pz = base.getZ() + 0.5;

        for (int i = 0; i < height; i++) {
            BlockPos pos = BlockPos.containing(px, py, pz);
            placeLog(level, pos, logState, Direction.Axis.Y);

            if (i >= 2 && random.nextFloat() < 0.45f) {
                double bx = (random.nextDouble() - 0.5) * 2.0;
                double by = 0.3 + random.nextDouble() * 0.4;
                double bz = (random.nextDouble() - 0.5) * 2.0;
                int branchLen = 2 + random.nextInt(4);
                generateBranch(level, random, logState, pos, bx, by, bz, branchLen, 1);
            }

            if (i == height - 1) {
                int topBranches = 1 + random.nextInt(3);
                for (int b = 0; b < topBranches; b++) {
                    double bx = (random.nextDouble() - 0.5) * 1.5;
                    double by = 0.5 + random.nextDouble() * 0.5;
                    double bz2 = (random.nextDouble() - 0.5) * 1.5;
                    int branchLen = 1 + random.nextInt(3);
                    generateBranch(level, random, logState, pos, bx, by, bz2, branchLen, 2);
                }
            }

            px += dx;
            py += 1.0;
            pz += dz;

            dx += (random.nextDouble() - 0.5) * 0.15;
            dz += (random.nextDouble() - 0.5) * 0.15;
        }
    }

    /** Diverging fork arm sized for a 2×2 trunk (denser branching). */
    public static void generateThickFork(
        WorldGenLevel level, RandomSource random, BlockState logState,
        BlockPos base, double dx, double dz, int height
    ) {
        double px = base.getX() + 0.5;
        double py = base.getY() + 0.5;
        double pz = base.getZ() + 0.5;

        for (int i = 0; i < height; i++) {
            BlockPos pos = BlockPos.containing(px, py, pz);
            placeLog(level, pos, logState, Direction.Axis.Y);

            if (i >= 2 && random.nextFloat() < 0.5f) {
                double bx = (random.nextDouble() - 0.5) * 2.0;
                double by = 0.3 + random.nextDouble() * 0.4;
                double bz = (random.nextDouble() - 0.5) * 2.0;
                int branchLen = 3 + random.nextInt(5);
                generateBranch(level, random, logState, pos, bx, by, bz, branchLen, 1);
            }

            if (i == height - 1) {
                int topBranches = 2 + random.nextInt(3);
                for (int b = 0; b < topBranches; b++) {
                    double bx = (random.nextDouble() - 0.5) * 1.5;
                    double by = 0.5 + random.nextDouble() * 0.5;
                    double bz2 = (random.nextDouble() - 0.5) * 1.5;
                    int branchLen = 2 + random.nextInt(3);
                    generateBranch(level, random, logState, pos, bx, by, bz2, branchLen, 2);
                }
            }

            px += dx;
            py += 1.0;
            pz += dz;

            dx += (random.nextDouble() - 0.5) * 0.15;
            dz += (random.nextDouble() - 0.5) * 0.15;
        }
    }

    /** Surface-exposed roots for a 1×1 trunk (2–4 roots, 1–3 segments each). */
    public static void generateExposedRoots(WorldGenLevel level, RandomSource random, BlockState logState, BlockPos base) {
        int rootCount = 2 + random.nextInt(3);
        for (int r = 0; r < rootCount; r++) {
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos rootStart = base.below().relative(dir);

            for (int seg = 0; seg < 1 + random.nextInt(3); seg++) {
                if (level.getBlockState(rootStart).is(BlockTags.DIRT)) {
                    placeLog(level, rootStart, logState, dir.getAxis());
                    rootStart = rootStart.relative(dir);
                    if (random.nextFloat() < 0.3f) {
                        rootStart = rootStart.below();
                    }
                } else {
                    break;
                }
            }
        }
    }

    /** Surface-exposed roots for a 2×2 trunk (3–6 roots, offset from trunk edge). */
    public static void generateThickExposedRoots(WorldGenLevel level, RandomSource random, BlockState logState, BlockPos base) {
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
                    placeLog(level, rootStart, logState, dir.getAxis());
                    rootStart = rootStart.relative(dir);
                    if (random.nextFloat() < 0.3f) {
                        rootStart = rootStart.below();
                    }
                } else {
                    break;
                }
            }
        }
    }

    @FunctionalInterface
    public interface TrunkCallback {
        void onTrunkBlock(BlockPos pos, int y, int totalHeight);
    }
}

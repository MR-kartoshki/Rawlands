package mrkartoshki.rawlands.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.tags.BlockTags;

import java.util.ArrayList;
import java.util.List;

public class ProceduralTreeFeature extends Feature<NoneFeatureConfiguration> {

    private final BlockState logState;
    private final BlockState leafState;
    /** Optional second leaf type (e.g. flowering azalea). Null = single leaf type. */
    private final BlockState secondLeafState;
    /** Probability [0,1] that a leaf position uses secondLeafState instead of leafState. */
    private final double secondLeafChance;
    private final int minHeight;
    private final int extraHeight;
    private final double drift;
    private final int foliageRadius;
    private final int foliageHeight;
    private final double gapChance;
    private final double vineChance;

    /** Single-leaf-type constructor. */
    public ProceduralTreeFeature(
        Codec<NoneFeatureConfiguration> codec,
        BlockState logState, BlockState leafState,
        int minHeight, int extraHeight, double drift,
        int foliageRadius, int foliageHeight, double gapChance, double vineChance
    ) {
        this(codec, logState, leafState, null, 0.0,
            minHeight, extraHeight, drift, foliageRadius, foliageHeight, gapChance, vineChance);
    }

    /** Two-leaf-type constructor (e.g. azalea + flowering azalea). */
    public ProceduralTreeFeature(
        Codec<NoneFeatureConfiguration> codec,
        BlockState logState, BlockState leafState, BlockState secondLeafState, double secondLeafChance,
        int minHeight, int extraHeight, double drift,
        int foliageRadius, int foliageHeight, double gapChance, double vineChance
    ) {
        super(codec);
        this.logState = logState;
        // Worldgen leaves keep distance 7 by default, which decays them on the next
        // random tick. Pin to 1 so they survive; neighbour updates recompute the real
        // distance later (e.g. when the trunk is chopped).
        this.leafState = leafState.hasProperty(LeavesBlock.DISTANCE)
            ? leafState.setValue(LeavesBlock.DISTANCE, 1)
            : leafState;
        this.secondLeafState = (secondLeafState != null && secondLeafState.hasProperty(LeavesBlock.DISTANCE))
            ? secondLeafState.setValue(LeavesBlock.DISTANCE, 1)
            : secondLeafState;
        this.secondLeafChance = secondLeafChance;
        this.minHeight = minHeight;
        this.extraHeight = extraHeight;
        this.drift = drift;
        this.foliageRadius = foliageRadius;
        this.foliageHeight = foliageHeight;
        this.gapChance = gapChance;
        this.vineChance = vineChance;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        if (!isValidGround(level.getBlockState(origin.below()))) {
            return false;
        }

        int height = minHeight + random.nextInt(extraHeight + 1);

        BlockPos.MutableBlockPos check = new BlockPos.MutableBlockPos();
        for (int y = 0; y < height; y++) {
            check.setWithOffset(origin, 0, y, 0);
            if (!TreeBranchHelper.canReplaceOrIsLog(level, check)) {
                return false;
            }
        }

        BlockPos[] top = new BlockPos[]{ origin.above(height - 1) };
        TreeBranchHelper.generateTrunk(level, random, logState, origin, height, drift, (pos, y, totalHeight) -> {
            if (y == totalHeight - 1) {
                top[0] = pos;
            }
        });

        placeFoliage(level, random, top[0]);
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

    private void placeFoliage(WorldGenLevel level, RandomSource random, BlockPos center) {
        // Vine sites are collected during the leaf pass and placed afterward.
        // Placing vines inline causes floating segments: canReplace() returns true
        // for vines, so a vine placed at leafPos.relative(dir) gets overwritten when
        // the loop later reaches that same position and places a leaf there, leaving
        // any segments below the first one unsupported.
        record VineSite(BlockPos leaf, Direction dir) {}
        List<VineSite> vineSites = vineChance > 0.0 ? new ArrayList<>() : null;

        BlockPos.MutableBlockPos leafPos = new BlockPos.MutableBlockPos();

        for (int dy = -(foliageHeight - 1); dy <= 1; dy++) {
            int layerRadius = foliageRadius;
            if (dy >= 1 || dy <= -(foliageHeight - 1)) {
                layerRadius = Math.max(0, foliageRadius - 1);
            }
            int maxDistSq = layerRadius * layerRadius + 1;

            for (int dx = -layerRadius; dx <= layerRadius; dx++) {
                for (int dz = -layerRadius; dz <= layerRadius; dz++) {
                    if (dx * dx + dz * dz > maxDistSq) continue;
                    if (gapChance > 0.0 && random.nextDouble() < gapChance) continue;

                    leafPos.setWithOffset(center, dx, dy, dz);
                    if (!TreeBranchHelper.canReplace(level, leafPos)) continue;

                    BlockState chosen = (secondLeafState != null && random.nextDouble() < secondLeafChance)
                        ? secondLeafState : leafState;
                    level.setBlock(leafPos, chosen, TreeBranchHelper.TREE_UPDATE_FLAGS);

                    if (vineSites != null && random.nextDouble() < vineChance) {
                        Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                        // immutable(): the site outlives this iteration, and leafPos is reused.
                        vineSites.add(new VineSite(leafPos.immutable(), dir));
                    }
                }
            }
        }

        if (vineSites != null) {
            for (VineSite site : vineSites) {
                // Re-check the leaf still exists: another tree's foliage may have overwritten
                // it with a non-leaf block between collection and placement.
                if (!level.getBlockState(site.leaf()).is(BlockTags.LEAVES)) continue;
                placeHangingVine(level, random, site.leaf().relative(site.dir()), site.dir().getOpposite());
            }
        }
    }

    private void placeHangingVine(WorldGenLevel level, RandomSource random, BlockPos pos, Direction attachFace) {
        BooleanProperty property = VineBlock.PROPERTY_BY_DIRECTION.get(attachFace);
        if (property == null) return;
        BlockState vine = Blocks.VINE.defaultBlockState().setValue(property, true);

        int length = 1 + random.nextInt(3);
        BlockPos.MutableBlockPos vinePos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < length; i++) {
            vinePos.setWithOffset(pos, 0, -i, 0);
            if (!level.getBlockState(vinePos).isAir()) break;
            level.setBlock(vinePos, vine, TreeBranchHelper.TREE_UPDATE_FLAGS);
        }
    }
}

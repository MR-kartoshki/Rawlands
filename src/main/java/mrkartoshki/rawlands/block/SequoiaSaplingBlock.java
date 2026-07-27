package mrkartoshki.rawlands.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Optional;

/**
 * Sapling for the massive Sequoia tree. Mirrors vanilla Dark Oak: a lone sapling never
 * grows. Growth requires planting a matching 2x2 (preferred: 3x3) grid of Sequoia
 * saplings. This class does its own grid detection and configured-feature placement
 * rather than relying on {@link TreeGrower}'s private mega-tree handling, since that
 * internal logic is keyed off a single hardcoded 2x2 check and isn't reusable for a 3x3
 * grid. The {@link #SEQUOIA_TREE_GROWER} below is intentionally empty in every slot:
 * there is no "small form" a lone sapling could fall back to.
 */
public class SequoiaSaplingBlock extends SaplingBlock {

    private static final int MIN_GRID = 2;
    private static final int MAX_GRID = 3;

    private static final ResourceKey<ConfiguredFeature<?, ?>> SEQUOIA_TREE_FEATURE =
        ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath("rawlands", "sequoia_tree"));

    public static final TreeGrower SEQUOIA_TREE_GROWER = new TreeGrower(
        "sequoia",
        0.0F,
        Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.empty()
    );

    public SequoiaSaplingBlock(Properties properties) {
        super(SEQUOIA_TREE_GROWER, properties);
    }

    @Override
    public void advanceTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (state.getValue(STAGE) == 0) {
            level.setBlock(pos, state.cycle(STAGE), 260);
            return;
        }
        tryGrowGrid(level, pos, state, random);
    }

    private void tryGrowGrid(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        Block block = state.getBlock();
        for (int size = MAX_GRID; size >= MIN_GRID; size--) {
            BlockPos anchor = findMatchingGrid(level, pos, block, size);
            if (anchor != null && growFrom(level, anchor, size, state, random)) {
                return;
            }
        }
        // No matching grid found (or placement failed): the sapling(s) are left as-is,
        // exactly like a lone vanilla Dark Oak sapling that never grows.
    }

    private static BlockPos findMatchingGrid(ServerLevel level, BlockPos pos, Block block, int size) {
        for (int ox = -(size - 1); ox <= 0; ox++) {
            for (int oz = -(size - 1); oz <= 0; oz++) {
                BlockPos anchor = pos.offset(ox, 0, oz);
                if (allMatch(level, anchor, block, size)) {
                    return anchor;
                }
            }
        }
        return null;
    }

    private static boolean allMatch(ServerLevel level, BlockPos anchor, Block block, int size) {
        for (int dx = 0; dx < size; dx++) {
            for (int dz = 0; dz < size; dz++) {
                if (!level.getBlockState(anchor.offset(dx, 0, dz)).is(block)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean growFrom(ServerLevel level, BlockPos anchor, int size, BlockState saplingState, RandomSource random) {
        Holder<ConfiguredFeature<?, ?>> featureHolder =
            level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).get(SEQUOIA_TREE_FEATURE).orElse(null);
        if (featureHolder == null) {
            return false;
        }

        BlockState air = Blocks.AIR.defaultBlockState();
        for (int dx = 0; dx < size; dx++) {
            for (int dz = 0; dz < size; dz++) {
                level.setBlock(anchor.offset(dx, 0, dz), air, 260);
            }
        }

        ConfiguredFeature<?, ?> feature = featureHolder.value();
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        if (feature.place(level, generator, random, anchor)) {
            return true;
        }

        for (int dx = 0; dx < size; dx++) {
            for (int dz = 0; dz < size; dz++) {
                level.setBlock(anchor.offset(dx, 0, dz), saplingState, 260);
            }
        }
        return false;
    }
}

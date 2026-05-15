package mrkartoshki.rawlands.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.tags.BlockTags;

import java.util.List;

public class DeadCoralTreeFeature extends Feature<NoneFeatureConfiguration> {

    private static final List<Block> DEAD_CORAL_BLOCKS = List.of(
        Blocks.DEAD_TUBE_CORAL_BLOCK,
        Blocks.DEAD_BRAIN_CORAL_BLOCK,
        Blocks.DEAD_BUBBLE_CORAL_BLOCK,
        Blocks.DEAD_FIRE_CORAL_BLOCK,
        Blocks.DEAD_HORN_CORAL_BLOCK
    );

    private static final List<Block> DEAD_CORALS = List.of(
        Blocks.DEAD_TUBE_CORAL,
        Blocks.DEAD_BRAIN_CORAL,
        Blocks.DEAD_BUBBLE_CORAL,
        Blocks.DEAD_FIRE_CORAL,
        Blocks.DEAD_HORN_CORAL
    );

    private static final List<Block> DEAD_WALL_CORALS = List.of(
        Blocks.DEAD_TUBE_CORAL_WALL_FAN,
        Blocks.DEAD_BRAIN_CORAL_WALL_FAN,
        Blocks.DEAD_BUBBLE_CORAL_WALL_FAN,
        Blocks.DEAD_FIRE_CORAL_WALL_FAN,
        Blocks.DEAD_HORN_CORAL_WALL_FAN
    );

    public DeadCoralTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        RandomSource random = context.random();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        Block coralBlock = DEAD_CORAL_BLOCKS.get(random.nextInt(DEAD_CORAL_BLOCKS.size()));
        BlockState state = coralBlock.defaultBlockState();

        MutableBlockPos mutPos = origin.mutable();
        int trunkHeight = random.nextInt(3) + 1;

        for (int i = 0; i < trunkHeight; i++) {
            if (!placeDeadCoralBlock(level, random, mutPos, state)) return true;
            mutPos.move(Direction.UP);
        }

        BlockPos trunkTop = mutPos.immutable();
        int nBranches = random.nextInt(3) + 2;
        List<Direction> directions = Plane.HORIZONTAL.shuffledCopy(random);

        for (Direction branchDir : directions.subList(0, nBranches)) {
            mutPos.set(trunkTop);
            mutPos.move(branchDir);
            int branchHeight = random.nextInt(5) + 2;
            int segmentLength = 0;

            for (int j = 0; j < branchHeight && placeDeadCoralBlock(level, random, mutPos, state); j++) {
                segmentLength++;
                mutPos.move(Direction.UP);
                if (j == 0 || segmentLength >= 2 && random.nextFloat() < 0.25F) {
                    mutPos.move(branchDir);
                    segmentLength = 0;
                }
            }
        }

        return true;
    }

    private boolean placeDeadCoralBlock(WorldGenLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos above = pos.above();
        BlockState current = level.getBlockState(pos);
        if ((current.is(Blocks.WATER) || current.is(BlockTags.CORALS)) && level.getBlockState(above).is(Blocks.WATER)) {
            level.setBlock(pos, state, 3);

            if (random.nextFloat() < 0.25F) {
                Block deadCoral = DEAD_CORALS.get(random.nextInt(DEAD_CORALS.size()));
                BlockState plantState = deadCoral.defaultBlockState();
                if (plantState.hasProperty(BlockStateProperties.WATERLOGGED)) {
                    plantState = plantState.setValue(BlockStateProperties.WATERLOGGED, true);
                }
                level.setBlock(above, plantState, 2);
            }

            for (Direction direction : Plane.HORIZONTAL) {
                if (random.nextFloat() < 0.2F) {
                    BlockPos side = pos.relative(direction);
                    if (level.getBlockState(side).is(Blocks.WATER)) {
                        Block wallFanBlock = DEAD_WALL_CORALS.get(random.nextInt(DEAD_WALL_CORALS.size()));
                        BlockState fanState = wallFanBlock.defaultBlockState();
                        if (fanState.hasProperty(BaseCoralWallFanBlock.FACING)) {
                            fanState = fanState.setValue(BaseCoralWallFanBlock.FACING, direction);
                        }
                        if (fanState.hasProperty(BlockStateProperties.WATERLOGGED)) {
                            fanState = fanState.setValue(BlockStateProperties.WATERLOGGED, true);
                        }
                        level.setBlock(side, fanState, 2);
                    }
                }
            }

            return true;
        }
        return false;
    }
}

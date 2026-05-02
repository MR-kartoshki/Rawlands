package mrkartoshki.rawlands.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

public class TallCattailBlock extends DoublePlantBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public TallCattailBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(WATERLOGGED, true));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        FluidState fluidHere = context.getLevel().getFluidState(pos);
        if (!fluidHere.is(FluidTags.WATER)) return null;
        BlockState above = context.getLevel().getBlockState(pos.above());
        if (!above.isAir() && !context.getLevel().getFluidState(pos.above()).is(FluidTags.WATER)) return null;
        return this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER).setValue(WATERLOGGED, true);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState below = level.getBlockState(pos.below());
            return below.getBlock() == this && below.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
        BlockPos groundPos = pos.below();
        BlockState groundState = level.getBlockState(groundPos);
        if (!groundState.isFaceSturdy(level, groundPos, Direction.UP)) return false;
        return level.getFluidState(pos).is(FluidTags.WATER);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        super.onPlace(state, level, pos, oldState, moved);
        // When the lower half is placed (e.g. from worldgen), ensure the upper half is created
        if (!level.isClientSide() && state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            BlockPos upperPos = pos.above();
            BlockState upperState = level.getBlockState(upperPos);
            if (!upperState.is(this)) {
                boolean waterAbove = level.getFluidState(upperPos).is(FluidTags.WATER);
                BlockState newUpper = this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER).setValue(WATERLOGGED, waterAbove);
                level.setBlock(upperPos, newUpper, 3);
            }
        }
    }

    // Harvesting with shears drops the cattail and removes both halves.
    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(Items.SHEARS)) {
            if (!level.isClientSide()) {
                // Resolve both half positions regardless of which was clicked.
                DoubleBlockHalf half = state.getValue(HALF);
                BlockPos lowerPos = half == DoubleBlockHalf.LOWER ? pos : pos.below();
                BlockPos upperPos = lowerPos.above();

                level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
                // Drop one item from the lower-half position.
                Block.popResource(level, lowerPos, new ItemStack(this));
                mrkartoshki.rawlands.Rawlands.LOGGER.info("Tall cattail sheared at {}: dropped item", lowerPos);
                // Remove upper half first so removing lower doesn't trigger a cascade update
                // that could attempt another drop via updateShape.
                BlockState upperState = level.getBlockState(upperPos);
                if (upperState.getBlock() == this) {
                    level.removeBlock(upperPos, false);
                }
                level.removeBlock(lowerPos, false);
                stack.hurtAndBreak(1, player,
                        hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess,
                                     BlockPos pos, Direction direction, BlockPos neighborPos,
                                     BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        if (!state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder); // adds HALF
        builder.add(WATERLOGGED);
    }
}

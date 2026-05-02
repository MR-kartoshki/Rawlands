package mrkartoshki.rawlands.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DeltaLilyBlock extends Block {
    // Use a very flat collision like the vanilla lily pad (1 pixel high = 1.0D)

    // changed to be consistent with the vanilla lily pad (1 pixel margin on each side)
    private static final VoxelShape SHAPE = Block.box(1D, 0.0D, 1D, 15D, 1.0D, 15D);

    public DeltaLilyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, net.minecraft.world.level.BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        // worldgen places at the water block's y; player placement goes one above.
        // Check either the position itself or the block below for water.
        return world.getFluidState(pos).is(FluidTags.WATER)
            || world.getFluidState(pos.below()).is(FluidTags.WATER);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(Items.SHEARS)) {
            if (!level.isClientSide()) {
                level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
                Block.popResource(level, pos, new ItemStack(this));
                mrkartoshki.rawlands.Rawlands.LOGGER.info("Delta lily sheared at {}: dropped item", pos);
                level.removeBlock(pos, false);
                stack.hurtAndBreak(1, player,
                        hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    // rely on loot tables for normal drops; shears handling above still spawns an item
}

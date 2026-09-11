package mrkartoshki.rawlands.mixin;

import mrkartoshki.rawlands.block.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {
    @Inject(method = "isValid", at = @At("RETURN"), cancellable = true)
    private void rawlands$acceptCustomBlocks(BlockState state, CallbackInfoReturnable<Boolean> callbackInfo) {
        if (callbackInfo.getReturnValue()) {
            return;
        }

        BlockEntityType<?> type = (BlockEntityType<?>)(Object)this;
        Block block = state.getBlock();
        boolean valid = type == BlockEntityTypes.SIGN && rawlands$isSequoiaSign(block)
            || type == BlockEntityTypes.HANGING_SIGN && rawlands$isSequoiaHangingSign(block)
            || type == BlockEntityTypes.SHELF && block == ModBlocks.SEQUOIA_SHELF;

        if (valid) {
            callbackInfo.setReturnValue(true);
        }
    }

    private static boolean rawlands$isSequoiaSign(Block block) {
        return block == ModBlocks.SEQUOIA_SIGN || block == ModBlocks.SEQUOIA_WALL_SIGN;
    }

    private static boolean rawlands$isSequoiaHangingSign(Block block) {
        return block == ModBlocks.SEQUOIA_HANGING_SIGN || block == ModBlocks.SEQUOIA_WALL_HANGING_SIGN;
    }
}

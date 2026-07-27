package mrkartoshki.rawlands.mixin;

import com.sun.jna.Structure;
import mrkartoshki.rawlands.block.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {
    @Final
    @Shadow
    private Set<Block> validBlocks;

    @Inject(method = "isValid", at = @At("TAIL"), cancellable = true)
    public void isValid(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        boolean valid = ((this.validBlocks.contains(state.getBlock())) || ModBlocks.BLOCK_ENTITIES.contains(state.getBlock()));
        cir.setReturnValue(valid);
    }
}

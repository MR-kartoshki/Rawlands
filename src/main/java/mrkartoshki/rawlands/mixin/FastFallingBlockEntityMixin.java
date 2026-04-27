package mrkartoshki.rawlands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mrkartoshki.rawlands.block.FastColoredFallingBlock;
import net.minecraft.world.entity.item.FallingBlockEntity;

@Mixin(FallingBlockEntity.class)
abstract class FastFallingBlockEntityMixin {
	@Inject(method = "tick", at = @At("TAIL"))
	private void rawlands$applyExtraGravity(CallbackInfo ci) {
		FallingBlockEntity self = (FallingBlockEntity) (Object) this;
		if (self.getBlockState().getBlock() instanceof FastColoredFallingBlock) {
			self.setDeltaMovement(self.getDeltaMovement().add(0, -0.04, 0));
		}
	}
}

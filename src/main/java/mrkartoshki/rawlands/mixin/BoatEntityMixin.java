package mrkartoshki.rawlands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mrkartoshki.rawlands.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

// AbstractBoat moved to the boat subpackage in MC 26.1.x
@Mixin(targets = "net.minecraft.world.entity.vehicle.boat.AbstractBoat")
abstract class BoatEntityMixin {
	@Inject(method = "tick", at = @At("TAIL"))
	private void rawlands$breakDeltaLilies(CallbackInfo ci) {
		Entity self = (Entity) (Object) this;
		Level level = self.level();
		if (level.isClientSide()) {
			return;
		}

		AABB box = self.getBoundingBox().inflate(0.02D);
		BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
		BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);
		for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
			BlockState state = level.getBlockState(pos);
			if (state.is(ModBlocks.DELTA_LILY)) {
				mrkartoshki.rawlands.Rawlands.LOGGER.debug("Boat hit delta lily at {}: destroying block", pos);
				level.destroyBlock(pos.immutable(), true);
			}
		}
	}
}

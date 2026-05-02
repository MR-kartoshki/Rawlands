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

@Mixin(targets = "net.minecraft.world.entity.vehicle.Boat")
abstract class BoatEntityMixin {
	@Inject(method = "tick", at = @At("TAIL"))
	private void rawlands$breakDeltaLilies(CallbackInfo ci) {
		Entity self = (Entity) (Object) this;
		Level level = self.level();
		if (level.isClientSide()) {
			return;
		}

		AABB box = self.getBoundingBox().inflate(0.02D);
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int x = BlockPos.containing(box.minX, box.minY, box.minZ).getX(); x <= BlockPos.containing(box.maxX, box.maxY, box.maxZ).getX(); x++) {
			for (int y = BlockPos.containing(box.minX, box.minY, box.minZ).getY(); y <= BlockPos.containing(box.maxX, box.maxY, box.maxZ).getY(); y++) {
				for (int z = BlockPos.containing(box.minX, box.minY, box.minZ).getZ(); z <= BlockPos.containing(box.maxX, box.maxY, box.maxZ).getZ(); z++) {
					pos.set(x, y, z);
					BlockState state = level.getBlockState(pos);
					if (state.is(ModBlocks.DELTA_LILY)) {
						mrkartoshki.rawlands.Rawlands.LOGGER.info("Boat hit delta lily at {}: destroying block", pos);
						level.destroyBlock(pos, true);
					}
				}
			}
		}
	}
}

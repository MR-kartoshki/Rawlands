package mrkartoshki.rawlands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import mrkartoshki.rawlands.block.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.feature.OreFeature;

@Mixin(OreFeature.class)
abstract class OreFeatureAxisMixin {
	private static final Direction.Axis[] AXES = Direction.Axis.values();

	@Redirect(
		method = "doPlace",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;")
	)
	private BlockState rawlands$randomizeSaltBlockAxis(LevelChunkSection section, int x, int y, int z, BlockState state, boolean lock) {
		if (state.is(ModBlocks.SALT_BLOCK)) {
			int hash = Math.abs(x * 7 + y * 3 + z * 13);
			state = state.setValue(RotatedPillarBlock.AXIS, AXES[hash % AXES.length]);
		}
		return section.setBlockState(x, y, z, state, lock);
	}
}

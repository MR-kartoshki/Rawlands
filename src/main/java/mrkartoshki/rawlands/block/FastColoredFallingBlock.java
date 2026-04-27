package mrkartoshki.rawlands.block;

import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class FastColoredFallingBlock extends ColoredFallingBlock {
	public FastColoredFallingBlock(ColorRGBA dustColor, BlockBehaviour.Properties properties) {
		super(dustColor, properties);
	}

	@Override
	protected int getDelayAfterPlace() {
		return 1;
	}
}

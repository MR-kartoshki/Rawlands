package mrkartoshki.rawlands.client;

import java.util.List;
import mrkartoshki.rawlands.block.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.color.block.BlockTintSources;

public class RawlandsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockColorRegistry.register(
			List.of(BlockTintSources.foliage()),
			ModBlocks.OLIVE_LEAVES
		);
	}
}

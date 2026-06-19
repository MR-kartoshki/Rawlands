package mrkartoshki.rawlands.client;

import java.util.List;
import mrkartoshki.rawlands.block.ModBlocks;
import mrkartoshki.rawlands.client.particle.BioluminescentAlgaeParticle;
import mrkartoshki.rawlands.particle.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.color.block.BlockTintSources;

public class RawlandsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockColorRegistry.register(
			List.of(BlockTintSources.foliage()),
			ModBlocks.OLIVE_LEAVES
		);
		ParticleProviderRegistry.getInstance().register(
			ModParticles.BIOLUMINESCENT_ALGAE,
			BioluminescentAlgaeParticle.Factory::new
		);
	}
}

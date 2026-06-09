package mrkartoshki.rawlands.client;

import java.util.List;
import mrkartoshki.rawlands.block.ModBlocks;
import mrkartoshki.rawlands.client.particle.BioluminescentAlgaeParticle;
import mrkartoshki.rawlands.particle.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class RawlandsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockColors.createDefault().register((blockState, blockAndTintGetter, blockPos, i) -> blockAndTintGetter != null && blockPos != null ? BiomeColors.getAverageGrassColor(blockAndTintGetter, blockState.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? blockPos.below() : blockPos) : GrassColor.getDefaultColor(), ModBlocks.OLIVE_LEAVES);
		ParticleFactoryRegistry.getInstance().register(
			ModParticles.BIOLUMINESCENT_ALGAE,
			BioluminescentAlgaeParticle.Factory::new
		);
	}
}

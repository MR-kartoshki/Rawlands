package mrkartoshki.rawlands;

import mrkartoshki.rawlands.block.ModBlocks;
import mrkartoshki.rawlands.item.ModItems;
import mrkartoshki.rawlands.sound.ModSounds;
import mrkartoshki.rawlands.particle.ModParticles;
import mrkartoshki.rawlands.world.densityfunction.BiomeGateDensityFunction;
import mrkartoshki.rawlands.world.densityfunction.RawlandsDensityFunctionTypes;
import mrkartoshki.rawlands.world.feature.ModFeatures;
import mrkartoshki.rawlands.world.surface.BiomeKeyRuleSource;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunctions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rawlands implements ModInitializer {
	public static final String MOD_ID = "rawlands";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.initialize();
		ModItems.initialize();
		ModSounds.init();
		ModParticles.register();
		ModFeatures.register();
		BiomeKeyRuleSource.register();
		RawlandsDensityFunctionTypes.register();

		// Capture the overworld's biome source + a constant-depth climate sampler so
		// rawlands:biome_gate density functions can resolve the surface biome per column.
		// Depth must be a constant because the real sampler's depth includes the offset graph,
		// which contains the biome gate itself (see BiomeGateDensityFunction). The value is
		// pinned INSIDE the strict-win depth span used by gated biomes (see strictWinDepth in
		// RawlandsRegion) so every gate query is a strict — deterministic — win over vanilla's
		// depth-0 parameter points. If that span or this constant ever change, they must move
		// together: the constant has to stay inside the span.
		ServerLevelEvents.LOAD.register((server, world) -> {
			if (world.dimension() == Level.OVERWORLD) {
				Climate.Sampler real = world.getChunkSource().randomState().sampler();
				BiomeGateDensityFunction.setContext(
					world.getChunkSource().getGenerator().getBiomeSource(),
					new Climate.Sampler(
						real.temperature(), real.humidity(), real.continentalness(),
						real.erosion(), DensityFunctions.constant(0.03), real.weirdness(),
						real.spawnTarget()
					)
				);
			}
		});
		ServerLevelEvents.UNLOAD.register((server, world) -> {
			if (world.dimension() == Level.OVERWORLD) {
				BiomeGateDensityFunction.clearContext();
			}
		});

		LOGGER.info("Rawlands initializing.");
	}
}
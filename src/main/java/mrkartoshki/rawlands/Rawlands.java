package mrkartoshki.rawlands;

import mrkartoshki.rawlands.block.ModBlocks;
import mrkartoshki.rawlands.item.ModItems;
import mrkartoshki.rawlands.particle.ModParticles;
import mrkartoshki.rawlands.sound.ModMusic;
import mrkartoshki.rawlands.world.feature.ModFeatures;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rawlands implements ModInitializer {
	public static final String MOD_ID = "rawlands";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.initialize();
		ModItems.initialize();
		ModMusic.init();
		ModParticles.register();
		ModFeatures.register();
		LOGGER.info("Rawlands initializing.");
	}
}
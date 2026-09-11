package mrkartoshki.rawlands.client;

import mrkartoshki.rawlands.client.datagen.RawlandsAssetsProvider;
import mrkartoshki.rawlands.client.datagen.RawlandsBiomeTagsProvider;
import mrkartoshki.rawlands.client.datagen.RawlandsBlockLootProvider;
import mrkartoshki.rawlands.client.datagen.RawlandsWorldgenProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class RawlandsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(RawlandsAssetsProvider::new);
		pack.addProvider(RawlandsBiomeTagsProvider::new);
		pack.addProvider(RawlandsBlockLootProvider::new);
		pack.addProvider(RawlandsWorldgenProvider::new);
	}
}

package mrkartoshki.rawlands.client.datagen;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

public final class RawlandsBiomeTagsProvider implements DataProvider {
	private final Path isOverworldPath;
	private final Path isMountainPath;

	public RawlandsBiomeTagsProvider(FabricPackOutput output) {
		PackOutput.PathProvider provider = output.createRegistryTagsPathProvider(net.minecraft.core.registries.Registries.BIOME);
		this.isOverworldPath = provider.json(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "is_overworld"));
		this.isMountainPath = provider.json(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "is_mountain"));
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		return CompletableFuture.allOf(
				DataProvider.saveStable(output, tagFile("rawlands:salt_flat", "rawlands:shrubland", "rawlands:subalpine_meadow", "rawlands:mediterranean_scrubland", "rawlands:flooded_delta", "rawlands:dead_forest", "rawlands:coral_forest"), isOverworldPath),
				DataProvider.saveStable(output, tagFile("rawlands:subalpine_meadow"), isMountainPath)
		);
	}

	@Override
	public String getName() {
		return "Rawlands Biome Tags";
	}

	private static JsonObject tagFile(String... values) {
		JsonArray entries = new JsonArray();

		for (String value : values) {
			entries.add(value);
		}

		JsonObject root = new JsonObject();
		root.addProperty("replace", false);
		root.add("values", entries);
		return root;
	}
}

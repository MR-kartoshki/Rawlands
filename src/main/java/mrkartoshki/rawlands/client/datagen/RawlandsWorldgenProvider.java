package mrkartoshki.rawlands.client.datagen;

import mrkartoshki.rawlands.Rawlands;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;

import java.util.concurrent.CompletableFuture;

public final class RawlandsWorldgenProvider extends FabricDynamicRegistryProvider {
	public RawlandsWorldgenProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void configure(HolderLookup.Provider registries, Entries entries) {
		registries.lookupOrThrow(Registries.CONFIGURED_FEATURE).listElements()
				.filter(holder -> holder.key().identifier().getNamespace().equals(Rawlands.MOD_ID))
				.forEach(entries::add);
		registries.lookupOrThrow(Registries.PLACED_FEATURE).listElements()
				.filter(holder -> holder.key().identifier().getNamespace().equals(Rawlands.MOD_ID))
				.forEach(entries::add);
	}

	@Override
	public String getName() {
		return "Rawlands Worldgen";
	}
}

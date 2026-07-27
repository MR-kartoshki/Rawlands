package mrkartoshki.rawlands.client;

import mrkartoshki.rawlands.Rawlands;
import mrkartoshki.rawlands.block.ModBlocks;
import mrkartoshki.rawlands.client.particle.BioluminescentAlgaeParticle;
import mrkartoshki.rawlands.entity.ModEntityTypes;
import mrkartoshki.rawlands.particle.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.resources.Identifier;

import java.util.List;

public class RawlandsClient implements ClientModInitializer {

	public static final ModelLayerLocation SEQUOIA_BOAT_LAYER =
		new ModelLayerLocation(Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "boat/sequoia"), "main");

	public static final ModelLayerLocation SEQUOIA_CHEST_BOAT_LAYER =
		new ModelLayerLocation(Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "chest_boat/sequoia"), "main");

	@Override
	public void onInitializeClient() {
		BlockColorRegistry.register(
			List.of(BlockTintSources.foliage()),
			ModBlocks.OLIVE_LEAVES,
			ModBlocks.SEQUOIA_LEAVES
		);
		ParticleProviderRegistry.getInstance().register(
			ModParticles.BIOLUMINESCENT_ALGAE,
			BioluminescentAlgaeParticle.Factory::new
		);

		ModelLayerRegistry.registerModelLayer(SEQUOIA_BOAT_LAYER, BoatModel::createBoatModel);
		ModelLayerRegistry.registerModelLayer(SEQUOIA_CHEST_BOAT_LAYER, BoatModel::createChestBoatModel);
		EntityRendererRegistry.register(ModEntityTypes.SEQUOIA_BOAT, context -> new BoatRenderer(context, SEQUOIA_BOAT_LAYER));
		EntityRendererRegistry.register(ModEntityTypes.SEQUOIA_CHEST_BOAT, context -> new BoatRenderer(context, SEQUOIA_CHEST_BOAT_LAYER));
	}
}

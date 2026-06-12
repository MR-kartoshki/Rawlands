package mrkartoshki.rawlands.client;

import mrkartoshki.rawlands.block.ModBlocks;
import mrkartoshki.rawlands.client.particle.BioluminescentAlgaeParticle;
import mrkartoshki.rawlands.particle.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayerGroup;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.List;

public class RawlandsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModBlocks.translucentBlocks().forEach(e -> {
			BlockRenderLayerMap.putBlock(e, ChunkSectionLayer.CUTOUT);});
	}
}

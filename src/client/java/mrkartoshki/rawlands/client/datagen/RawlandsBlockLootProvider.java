package mrkartoshki.rawlands.client.datagen;

import java.util.concurrent.CompletableFuture;

import mrkartoshki.rawlands.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public final class RawlandsBlockLootProvider extends FabricBlockLootSubProvider {
	public RawlandsBlockLootProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	public void generate() {
		dropSelf(ModBlocks.SALT_BLOCK);
		dropSelf(ModBlocks.COARSE_SALT);
		dropSelf(ModBlocks.FINE_SALT);
		add(ModBlocks.DRY_SCRUB, createShearsDispatchTable(ModBlocks.DRY_SCRUB,
				applyExplosionDecay(ModBlocks.DRY_SCRUB,
						LootItem.lootTableItem(Items.STICK)
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))))));
		add(ModBlocks.OLIVE_LEAVES, createShearsOnlyDrop(ModBlocks.OLIVE_LEAVES));
		add(ModBlocks.BROADLEAF_LUPINE, block -> createSinglePropConditionTable(block, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER));
		add(ModBlocks.SHORT_CATTAIL, createShearsOnlyDrop(ModBlocks.SHORT_CATTAIL));
		add(ModBlocks.TALL_CATTAIL,  block -> createSinglePropConditionTable(block, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER));
		add(ModBlocks.DELTA_LILY, createShearsOnlyDrop(ModBlocks.DELTA_LILY));
	}
}

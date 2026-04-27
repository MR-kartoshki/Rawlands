package mrkartoshki.rawlands.block;

import java.util.ArrayList;
import java.util.List;

import mrkartoshki.rawlands.Rawlands;
import mrkartoshki.rawlands.item.DeltaLilyItem;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.TintedParticleLeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {
	private static final List<Block> REGISTERED_BLOCKS = new ArrayList<>();

	public static final Block SALT_BLOCK = register(
			"salt_block",
			new RotatedPillarBlock(properties("salt_block").mapColor(MapColor.QUARTZ).strength(0.5F).sound(SoundType.CALCITE))
	);

	public static final Block COARSE_SALT = register(
			"coarse_salt",
			new FastColoredFallingBlock(
					new ColorRGBA(0xFFF2F2EF),
					properties("coarse_salt").mapColor(MapColor.QUARTZ).strength(0.5F).sound(SoundType.CALCITE)
			)
	);

	public static final Block FINE_SALT = register(
			"fine_salt",
			new ColoredFallingBlock(
					new ColorRGBA(0xFFFFFFFF),
					properties("fine_salt").mapColor(MapColor.SNOW).strength(0.5F).sound(SoundType.CALCITE)
			)
	);

	public static final Block DRY_SCRUB = register(
			"dry_scrub",
			new DryScrubBlock(
					properties("dry_scrub")
							.mapColor(MapColor.WOOD)
							.noCollision()
							.instabreak()
							.sound(SoundType.GRASS)
							.offsetType(OffsetType.XYZ)
			)
	);

	public static final Block OLIVE_LEAVES = register(
			"olive_leaves",
			new TintedParticleLeavesBlock(
					0.01F,
					properties("olive_leaves")
							.mapColor(MapColor.PLANT)
							.strength(0.2F)
							.randomTicks()
							.sound(SoundType.GRASS)
							.noOcclusion()
							.ignitedByLava()
			)
	);

	public static final Block BROADLEAF_LUPINE = register(
			"broadleaf_lupine",
			new TallFlowerBlock(
					properties("broadleaf_lupine")
							.mapColor(MapColor.COLOR_PURPLE)
							.noCollision()
							.instabreak()
							.sound(SoundType.GRASS)
							.offsetType(OffsetType.XZ)
			)
	);

	public static final Block SHORT_CATTAIL = register(
			"short_cattail",
			new ShortCattailBlock(
					properties("short_cattail")
							.mapColor(MapColor.PLANT)
							.noCollision()
							.instabreak()
							.sound(SoundType.WET_GRASS)
							.offsetType(OffsetType.XZ)
			)
	);

	public static final Block TALL_CATTAIL = register(
			"tall_cattail",
			new TallCattailBlock(
					properties("tall_cattail")
							.mapColor(MapColor.PLANT)
							.noCollision()
							.instabreak()
							.sound(SoundType.WET_GRASS)
							.offsetType(OffsetType.XZ)
			)
	);

	public static final Block NIGHTSHADE = register(
			"nightshade",
			new NightshadeBlock(
					properties("nightshade")
							.mapColor(MapColor.COLOR_PURPLE)
							.noCollision()
							.instabreak()
							.sound(SoundType.GRASS)
							.offsetType(OffsetType.XZ)
			)
	);

	public static final Block DELTA_LILY = registerBlockOnly(
			"delta_lily",
			new DeltaLilyBlock(
					properties("delta_lily")
							.mapColor(MapColor.PLANT)
							.noCollision()
							.instabreak()
							.sound(SoundType.WET_GRASS)
			)
	);

	private ModBlocks() {
	}

	public static void initialize() {
		Identifier deltaLilyId = id("delta_lily");
		Registry.register(BuiltInRegistries.ITEM, deltaLilyId,
				new DeltaLilyItem(DELTA_LILY, new Item.Properties()
						.setId(ResourceKey.create(Registries.ITEM, deltaLilyId))
						.useBlockDescriptionPrefix())
		);

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(output -> {
			output.accept(SALT_BLOCK);
			output.accept(COARSE_SALT);
			output.accept(FINE_SALT);
			output.accept(DRY_SCRUB);
			output.accept(OLIVE_LEAVES);
			output.accept(BROADLEAF_LUPINE);
			output.accept(SHORT_CATTAIL);
			output.accept(TALL_CATTAIL);
			output.accept(NIGHTSHADE);
			output.accept(DELTA_LILY);
		});
	}

	public static List<Block> allBlocks() {
		return List.copyOf(REGISTERED_BLOCKS);
	}

	private static Block register(String name, Block block) {
		Identifier id = id(name);
		Block registeredBlock = Registry.register(BuiltInRegistries.BLOCK, id, block);
		Registry.register(
				BuiltInRegistries.ITEM,
				id,
				new BlockItem(registeredBlock, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)).useBlockDescriptionPrefix())
		);
		REGISTERED_BLOCKS.add(registeredBlock);
		return registeredBlock;
	}

	private static Block registerBlockOnly(String name, Block block) {
		Identifier id = id(name);
		Block registeredBlock = Registry.register(BuiltInRegistries.BLOCK, id, block);
		REGISTERED_BLOCKS.add(registeredBlock);
		return registeredBlock;
	}

	private static BlockBehaviour.Properties properties(String name) {
		return BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, id(name)));
	}

	private static Identifier id(String name) {
		return Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, name);
	}
}

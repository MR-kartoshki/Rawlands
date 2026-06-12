package mrkartoshki.rawlands.block;

import java.util.ArrayList;
import java.util.List;

import mrkartoshki.rawlands.Rawlands;
import mrkartoshki.rawlands.item.DeltaLilyItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {
	private static final List<Block> REGISTERED_BLOCKS = new ArrayList<>();
	private static final List<Block> TRANSLUCENT_BLOCKS = new ArrayList<>();
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
							.dynamicShape()
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
							.noOcclusion()
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

	public static final Block OLIVE_SAPLING = register("olive_sapling",new OliveSaplingBlock(OliveSaplingBlock.OLIVE_TREE,
			properties("olive_sapling").mapColor(MapColor.PLANT).instabreak().noCollision().sound(SoundType.GRASS)));

	private ModBlocks() {
	}

	public static void initialize() {
		Identifier deltaLilyId = id("delta_lily");
		Registry.register(BuiltInRegistries.ITEM, deltaLilyId,
				new DeltaLilyItem(DELTA_LILY, new Item.Properties()
						.setId(ResourceKey.create(Registries.ITEM, deltaLilyId))
						.useBlockDescriptionPrefix())
		);
		registerCompostables();
		TRANSLUCENT_BLOCKS.addAll(List.of(
				DRY_SCRUB,
				OLIVE_LEAVES,
				OLIVE_SAPLING,
				BROADLEAF_LUPINE,
				SHORT_CATTAIL,
				TALL_CATTAIL,
				NIGHTSHADE,
				DELTA_LILY
			)
		);
	}

	public static List<Block> allBlocks() {
		return List.copyOf(REGISTERED_BLOCKS);
	}
	public static List<Block> translucentBlocks() {
		return List.copyOf(TRANSLUCENT_BLOCKS);
	}

	// Custom flowers compost like vanilla flowers (65% chance per item to raise the composter level).
	private static void registerCompostables() {
		ComposterBlock.COMPOSTABLES.put(BROADLEAF_LUPINE,0.65f);
		ComposterBlock.COMPOSTABLES.put(SHORT_CATTAIL,0.65f);
		ComposterBlock.COMPOSTABLES.put(TALL_CATTAIL,0.65f);
		ComposterBlock.COMPOSTABLES.put(NIGHTSHADE,0.65f);
		ComposterBlock.COMPOSTABLES.put(DELTA_LILY,0.65f);
		ComposterBlock.COMPOSTABLES.put(OLIVE_SAPLING,0.3f);
		ComposterBlock.COMPOSTABLES.put(OLIVE_LEAVES,0.3f);
		ComposterBlock.COMPOSTABLES.put(DRY_SCRUB,0.65f);

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

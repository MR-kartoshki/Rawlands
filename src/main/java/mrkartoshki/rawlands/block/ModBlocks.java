package mrkartoshki.rawlands.block;

import java.util.ArrayList;
import java.util.List;

import mrkartoshki.rawlands.Rawlands;
import mrkartoshki.rawlands.item.DeltaLilyItem;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.registry.CompostableRegistry;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.TintedParticleLeavesBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

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

	public static final Block OLIVE_SAPLING = register("olive_sapling",new OliveSaplingBlock(OliveSaplingBlock.OLIVE_TREE,
			properties("olive_sapling").mapColor(MapColor.PLANT).instabreak().noCollision().sound(SoundType.GRASS)));

	// --- Sequoia wood set ---

	public static final Block SEQUOIA_LOG = register(
			"sequoia_log",
			new RotatedPillarBlock(
					properties("sequoia_log").mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS)
							.strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava()
			)
	);

	public static final Block STRIPPED_SEQUOIA_LOG = register(
			"stripped_sequoia_log",
			new RotatedPillarBlock(
					properties("stripped_sequoia_log").mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS)
							.strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava()
			)
	);

	public static final Block SEQUOIA_WOOD = register(
			"sequoia_wood",
			new RotatedPillarBlock(
					properties("sequoia_wood").mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS)
							.strength(2.0F).sound(SoundType.WOOD).ignitedByLava()
			)
	);

	public static final Block STRIPPED_SEQUOIA_WOOD = register(
			"stripped_sequoia_wood",
			new RotatedPillarBlock(
					properties("stripped_sequoia_wood").mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS)
							.strength(2.0F).sound(SoundType.WOOD).ignitedByLava()
			)
	);

	public static final Block SEQUOIA_PLANKS = register(
			"sequoia_planks",
			new Block(
					properties("sequoia_planks").mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS)
							.strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava()
			)
	);

	public static final Block SEQUOIA_STAIRS = register(
			"sequoia_stairs",
			new StairBlock(
					SEQUOIA_PLANKS.defaultBlockState(),
					properties("sequoia_stairs").mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS)
							.strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava()
			)
	);

	public static final Block SEQUOIA_SLAB = register(
			"sequoia_slab",
			new SlabBlock(
					properties("sequoia_slab").mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS)
							.strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava()
			)
	);

	public static final Block SEQUOIA_FENCE = register(
			"sequoia_fence",
			new FenceBlock(
					properties("sequoia_fence").mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS)
							.strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava()
			)
	);

	public static final Block SEQUOIA_FENCE_GATE = register(
			"sequoia_fence_gate",
			new FenceGateBlock(
					ModWoodTypes.SEQUOIA_WOOD_TYPE,
					properties("sequoia_fence_gate").mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS)
							.strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava()
			)
	);

	public static final Block SEQUOIA_TRAPDOOR = register(
			"sequoia_trapdoor",
			new TrapDoorBlock(
					ModWoodTypes.SEQUOIA_SET_TYPE,
					properties("sequoia_trapdoor").mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS)
							.strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn(Blocks::never)
							.ignitedByLava()
			)
	);

	public static final Block SEQUOIA_PRESSURE_PLATE = register(
			"sequoia_pressure_plate",
			new PressurePlateBlock(
					ModWoodTypes.SEQUOIA_SET_TYPE,
					properties("sequoia_pressure_plate").mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS)
							.noCollision().strength(0.5F).sound(SoundType.WOOD).ignitedByLava()
			)
	);

	public static final Block SEQUOIA_BUTTON = register(
			"sequoia_button",
			new ButtonBlock(
					ModWoodTypes.SEQUOIA_SET_TYPE, 30,
					properties("sequoia_button").noCollision().instabreak().sound(SoundType.WOOD)
			)
	);

	public static final Block SEQUOIA_LEAVES = register(
			"sequoia_leaves",
			new TintedParticleLeavesBlock(
					0.01F,
					properties("sequoia_leaves").mapColor(MapColor.PLANT).strength(0.2F).randomTicks()
							.sound(SoundType.GRASS).noOcclusion().ignitedByLava()
			)
	);

	public static final Block SEQUOIA_SAPLING = register(
			"sequoia_sapling",
			new SequoiaSaplingBlock(
					properties("sequoia_sapling").mapColor(MapColor.PLANT).instabreak().noCollision().sound(SoundType.GRASS)
			)
	);

	public static final Block SEQUOIA_DOOR = registerBlockOnly(
			"sequoia_door",
			new DoorBlock(
					ModWoodTypes.SEQUOIA_SET_TYPE,
					properties("sequoia_door").mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS)
							.strength(3.0F).noOcclusion().ignitedByLava()
			)
	);

	public static final Block SEQUOIA_SIGN = registerBlockOnly(
			"sequoia_sign",
			new StandingSignBlock(
					ModWoodTypes.SEQUOIA_WOOD_TYPE,
					properties("sequoia_sign").mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS)
							.noCollision().strength(1.0F).ignitedByLava()
			)
	);

	public static final Block SEQUOIA_WALL_SIGN = registerBlockOnly(
			"sequoia_wall_sign",
			new WallSignBlock(
					ModWoodTypes.SEQUOIA_WOOD_TYPE,
					properties("sequoia_wall_sign").mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS)
							.noCollision().strength(1.0F).ignitedByLava()
							.overrideLootTable(SEQUOIA_SIGN.getLootTable())
							.overrideDescription(SEQUOIA_SIGN.getDescriptionId())
			)
	);

	public static final Block SEQUOIA_HANGING_SIGN = registerBlockOnly(
			"sequoia_hanging_sign",
			new CeilingHangingSignBlock(
					ModWoodTypes.SEQUOIA_WOOD_TYPE,
					properties("sequoia_hanging_sign").mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS)
							.noCollision().strength(1.0F).ignitedByLava()
			)
	);

	public static final Block SEQUOIA_WALL_HANGING_SIGN = registerBlockOnly(
			"sequoia_wall_hanging_sign",
			new WallHangingSignBlock(
					ModWoodTypes.SEQUOIA_WOOD_TYPE,
					properties("sequoia_wall_hanging_sign").mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS)
							.noCollision().strength(1.0F).ignitedByLava()
							.overrideLootTable(SEQUOIA_HANGING_SIGN.getLootTable())
							.overrideDescription(SEQUOIA_HANGING_SIGN.getDescriptionId())
			)
	);

	public static final Block SEQUOIA_SHELF = registerBlockOnly(
			"sequoia_shelf",
			new ShelfBlock(
					properties("sequoia_shelf").mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS)
							.strength(2.0F, 3.0F).sound(SoundType.SHELF).ignitedByLava()
			)
	);

	private ModBlocks() {
	}

	public static void initialize() {
		ModWoodTypes.init();

		Identifier deltaLilyId = id("delta_lily");
		Registry.register(BuiltInRegistries.ITEM, deltaLilyId,
				new DeltaLilyItem(DELTA_LILY, new Item.Properties()
						.setId(ResourceKey.create(Registries.ITEM, deltaLilyId))
						.useBlockDescriptionPrefix())
		);

		Identifier doorId = id("sequoia_door");
		Registry.register(BuiltInRegistries.ITEM, doorId,
				new DoubleHighBlockItem(SEQUOIA_DOOR, new Item.Properties()
						.setId(ResourceKey.create(Registries.ITEM, doorId))
						.useBlockDescriptionPrefix())
		);

		Identifier signId = id("sequoia_sign");
		Registry.register(BuiltInRegistries.ITEM, signId,
				new SignItem(SEQUOIA_SIGN, SEQUOIA_WALL_SIGN, new Item.Properties()
						.setId(ResourceKey.create(Registries.ITEM, signId))
						.useBlockDescriptionPrefix()
						.stacksTo(16))
		);

		Identifier hangingSignId = id("sequoia_hanging_sign");
		Registry.register(BuiltInRegistries.ITEM, hangingSignId,
				new HangingSignItem(SEQUOIA_HANGING_SIGN, SEQUOIA_WALL_HANGING_SIGN, new Item.Properties()
						.setId(ResourceKey.create(Registries.ITEM, hangingSignId))
						.useBlockDescriptionPrefix()
						.stacksTo(16))
		);

		Identifier shelfId = id("sequoia_shelf");
		Registry.register(BuiltInRegistries.ITEM, shelfId,
				new BlockItem(SEQUOIA_SHELF, new Item.Properties()
						.setId(ResourceKey.create(Registries.ITEM, shelfId))
						.useBlockDescriptionPrefix()
						.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY))
		);

		registerCompostables();
		registerStrippables();
		registerFlammables();

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(output -> {
			output.accept(OLIVE_SAPLING);
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
			output.accept(SEQUOIA_LEAVES);
			output.accept(SEQUOIA_SAPLING);
		});

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(output -> {
			output.accept(SEQUOIA_LOG);
			output.accept(STRIPPED_SEQUOIA_LOG);
			output.accept(SEQUOIA_WOOD);
			output.accept(STRIPPED_SEQUOIA_WOOD);
			output.accept(SEQUOIA_PLANKS);
			output.accept(SEQUOIA_STAIRS);
			output.accept(SEQUOIA_SLAB);
			output.accept(SEQUOIA_FENCE);
			output.accept(SEQUOIA_FENCE_GATE);
			output.accept(SEQUOIA_DOOR);
			output.accept(SEQUOIA_TRAPDOOR);
			output.accept(SEQUOIA_PRESSURE_PLATE);
			output.accept(SEQUOIA_BUTTON);
			output.accept(SEQUOIA_SIGN);
			output.accept(SEQUOIA_HANGING_SIGN);
			output.accept(SEQUOIA_SHELF);
		});
	}

	public static List<Block> allBlocks() {
		return List.copyOf(REGISTERED_BLOCKS);
	}

	// Custom flowers compost like vanilla flowers (65% chance per item to raise the composter level).
	private static void registerCompostables() {
		CompostableRegistry.INSTANCE.add(BROADLEAF_LUPINE, 0.65F);
		CompostableRegistry.INSTANCE.add(NIGHTSHADE, 0.65F);
		CompostableRegistry.INSTANCE.add(DELTA_LILY, 0.65F);
		CompostableRegistry.INSTANCE.add(SHORT_CATTAIL, 0.65F);
		CompostableRegistry.INSTANCE.add(TALL_CATTAIL, 0.65F);
		CompostableRegistry.INSTANCE.add(DRY_SCRUB, 0.65F);
		CompostableRegistry.INSTANCE.add(OLIVE_SAPLING, 0.30F);
		CompostableRegistry.INSTANCE.add(OLIVE_LEAVES, 0.30F);
		CompostableRegistry.INSTANCE.add(SEQUOIA_SAPLING, 0.30F);
		CompostableRegistry.INSTANCE.add(SEQUOIA_LEAVES, 0.30F);
	}

	// Axe-stripping: log <-> stripped log, wood <-> stripped wood.
	private static void registerStrippables() {
		StrippableBlockRegistry.register(SEQUOIA_LOG, STRIPPED_SEQUOIA_LOG);
		StrippableBlockRegistry.register(SEQUOIA_WOOD, STRIPPED_SEQUOIA_WOOD);
	}

	// Vanilla-oak-equivalent catch/spread chances for every combustible Sequoia block.
	private static void registerFlammables() {
		FlammableBlockRegistry registry = FlammableBlockRegistry.getDefaultInstance();
		registry.add(SEQUOIA_LOG, 5, 5);
		registry.add(STRIPPED_SEQUOIA_LOG, 5, 5);
		registry.add(SEQUOIA_WOOD, 5, 5);
		registry.add(STRIPPED_SEQUOIA_WOOD, 5, 5);
		registry.add(SEQUOIA_PLANKS, 5, 20);
		registry.add(SEQUOIA_STAIRS, 5, 20);
		registry.add(SEQUOIA_SLAB, 5, 20);
		registry.add(SEQUOIA_FENCE, 5, 20);
		registry.add(SEQUOIA_FENCE_GATE, 5, 20);
		registry.add(SEQUOIA_DOOR, 5, 20);
		registry.add(SEQUOIA_TRAPDOOR, 5, 20);
		registry.add(SEQUOIA_SIGN, 5, 20);
		registry.add(SEQUOIA_WALL_SIGN, 5, 20);
		registry.add(SEQUOIA_HANGING_SIGN, 5, 20);
		registry.add(SEQUOIA_WALL_HANGING_SIGN, 5, 20);
		registry.add(SEQUOIA_SHELF, 5, 20);
		registry.add(SEQUOIA_LEAVES, 30, 60);
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

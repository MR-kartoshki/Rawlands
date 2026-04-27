package mrkartoshki.rawlands.client.datagen;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

public final class RawlandsAssetsProvider implements DataProvider {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private final PackOutput.PathProvider blockStateOutput;
	private final PackOutput.PathProvider modelOutput;
	private final PackOutput.PathProvider itemDefinitionOutput;
	private final Path langPath;

	public RawlandsAssetsProvider(FabricPackOutput output) {
		this.blockStateOutput = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
		this.modelOutput = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
		this.itemDefinitionOutput = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
		this.langPath = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve("rawlands/lang/en_us.json");
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		List<CompletableFuture<?>> futures = new ArrayList<>();

		futures.add(saveJson(output, blockStateOutput.json(id("salt_block")), axisBlockState("rawlands:block/salt_block")));
		futures.add(saveJson(output, blockStateOutput.json(id("coarse_salt")), topRotatingBlockState("rawlands:block/coarse_salt")));
		futures.add(saveJson(output, blockStateOutput.json(id("fine_salt")), topRotatingBlockState("rawlands:block/fine_salt")));
		futures.add(saveJson(output, blockStateOutput.json(id("dry_scrub")), crossBlockState("rawlands:block/dry_scrub")));
		futures.add(saveJson(output, blockStateOutput.json(id("olive_leaves")), cubeAllBlockState("rawlands:block/olive_leaves")));
		futures.add(saveJson(output, blockStateOutput.json(id("broadleaf_lupine")), tallFlowerBlockState("rawlands:block/broadleaf_lupine_bottom", "rawlands:block/broadleaf_lupine_top")));

		futures.add(saveJson(output, modelOutput.json(id("block/salt_block")), cubeAllModel("rawlands:block/salt_block")));
		for (int rot : new int[]{0, 90, 180, 270}) {
			futures.add(saveJson(output, modelOutput.json(id("block/coarse_salt" + rotSuffix(rot))), cubeAllTopRotatedModel("rawlands:block/coarse_salt", rot)));
			futures.add(saveJson(output, modelOutput.json(id("block/fine_salt"   + rotSuffix(rot))), cubeAllTopRotatedModel("rawlands:block/fine_salt",   rot)));
		}
		futures.add(saveJson(output, modelOutput.json(id("block/dry_scrub")), crossModel("rawlands:block/dry_scrub")));
		futures.add(saveJson(output, modelOutput.json(id("block/olive_leaves")), leavesModel("rawlands:block/olive_leaves")));
		futures.add(saveJson(output, modelOutput.json(id("block/broadleaf_lupine_bottom")), crossModel("rawlands:block/broadleaf_lupine_bottom")));
		futures.add(saveJson(output, modelOutput.json(id("block/broadleaf_lupine_top")), crossModel("rawlands:block/broadleaf_lupine_top")));

		futures.add(saveJson(output, modelOutput.json(id("item/salt_block")), generatedItemModel("rawlands:block/salt_block")));
		futures.add(saveJson(output, modelOutput.json(id("item/coarse_salt")), generatedItemModel("rawlands:block/coarse_salt")));
		futures.add(saveJson(output, modelOutput.json(id("item/fine_salt")), generatedItemModel("rawlands:block/fine_salt")));
		futures.add(saveJson(output, modelOutput.json(id("item/dry_scrub")), generatedItemModel("rawlands:block/dry_scrub")));
		futures.add(saveJson(output, modelOutput.json(id("item/olive_leaves")), generatedItemModel("rawlands:block/olive_leaves")));
		futures.add(saveJson(output, modelOutput.json(id("item/broadleaf_lupine")), generatedItemModel("rawlands:item/broadleaf_lupine")));

		futures.add(saveJson(output, itemDefinitionOutput.json(id("salt_block")), itemDefinitionModel("rawlands:block/salt_block")));
		futures.add(saveJson(output, itemDefinitionOutput.json(id("coarse_salt")), itemDefinitionModel("rawlands:block/coarse_salt")));
		futures.add(saveJson(output, itemDefinitionOutput.json(id("fine_salt")), itemDefinitionModel("rawlands:block/fine_salt")));
		futures.add(saveJson(output, itemDefinitionOutput.json(id("dry_scrub")), itemDefinitionModel("rawlands:block/dry_scrub")));
		futures.add(saveJson(output, itemDefinitionOutput.json(id("olive_leaves")), itemDefinitionModel("rawlands:block/olive_leaves")));
		futures.add(saveJson(output, itemDefinitionOutput.json(id("broadleaf_lupine")), itemDefinitionModel("rawlands:item/broadleaf_lupine")));

		// Short cattail
		futures.add(saveJson(output, blockStateOutput.json(id("short_cattail")), crossBlockState("rawlands:block/short_cattail")));
		futures.add(saveJson(output, modelOutput.json(id("block/short_cattail")), crossModel("rawlands:block/short_cattail")));
		futures.add(saveJson(output, modelOutput.json(id("item/short_cattail")), generatedItemModel("rawlands:block/short_cattail")));
		futures.add(saveJson(output, itemDefinitionOutput.json(id("short_cattail")), itemDefinitionModel("rawlands:block/short_cattail")));

		// Tall cattail
		futures.add(saveJson(output, blockStateOutput.json(id("tall_cattail")), tallFlowerBlockState("rawlands:block/tall_cattail_bottom", "rawlands:block/tall_cattail_top")));
		futures.add(saveJson(output, modelOutput.json(id("block/tall_cattail_bottom")), crossModel("rawlands:block/tall_cattail_bottom")));
		futures.add(saveJson(output, modelOutput.json(id("block/tall_cattail_top")), crossModel("rawlands:block/tall_cattail_top")));
		futures.add(saveJson(output, modelOutput.json(id("item/tall_cattail")), generatedItemModel("rawlands:item/tall_cattail")));
		futures.add(saveJson(output, itemDefinitionOutput.json(id("tall_cattail")), itemDefinitionModel("rawlands:item/tall_cattail")));

		// Delta lily (block model is a custom Blockbench export — not regenerated by datagen)
		futures.add(saveJson(output, blockStateOutput.json(id("delta_lily")), crossBlockState("rawlands:block/delta_lily")));
		futures.add(saveJson(output, modelOutput.json(id("item/delta_lily")), generatedItemModel("rawlands:block/delta_lily")));
		futures.add(saveJson(output, itemDefinitionOutput.json(id("delta_lily")), itemDefinitionModel("rawlands:block/delta_lily")));

		futures.add(saveJson(output, langPath, langFile()));

		return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
	}

	@Override
	public String getName() {
		return "Rawlands Assets";
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath("rawlands", path);
	}

	private static CompletableFuture<?> saveJson(CachedOutput output, Path path, JsonObject json) {
		try {
			byte[] bytes = GSON.toJson(json).getBytes(StandardCharsets.UTF_8);
			output.writeIfNeeded(path, bytes, Hashing.sha1().hashBytes(bytes));
			return CompletableFuture.completedFuture(null);
		} catch (IOException e) {
			return CompletableFuture.failedFuture(e);
		}
	}

	private static CompletableFuture<?> writePng(CachedOutput output, Path path, BufferedImage image) {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ImageIO.write(image, "png", stream);
			byte[] bytes = stream.toByteArray();
			output.writeIfNeeded(path, bytes, Hashing.sha1().hashBytes(bytes));
			return CompletableFuture.completedFuture(null);
		} catch (IOException exception) {
			return CompletableFuture.failedFuture(exception);
		}
	}

	private static JsonObject cubeAllBlockState(String model) {
		JsonObject variant = new JsonObject();
		variant.addProperty("model", model);
		JsonObject variants = new JsonObject();
		variants.add("", variant);
		JsonObject root = new JsonObject();
		root.add("variants", variants);
		return root;
	}

	private static JsonObject crossBlockState(String model) {
		return cubeAllBlockState(model);
	}

	private static String rotSuffix(int rotation) {
		return rotation == 0 ? "" : "_rot" + rotation;
	}

	/** Blockstate that picks randomly among 4 models with 0/90/180/270 top-face UV rotation. */
	private static JsonObject topRotatingBlockState(String baseModel) {
		JsonObject root = new JsonObject();
		JsonObject variants = new JsonObject();
		JsonArray weightedVariants = new JsonArray();
		for (int rot : new int[]{0, 90, 180, 270}) {
			JsonObject v = new JsonObject();
			v.addProperty("model", baseModel + rotSuffix(rot));
			weightedVariants.add(v);
		}
		variants.add("", weightedVariants);
		root.add("variants", variants);
		return root;
	}

	/** cube_all model where the top face UV is explicitly rotated by {@code topRotation} degrees. */
	private static JsonObject cubeAllTopRotatedModel(String texture, int topRotation) {
		JsonObject textures = new JsonObject();
		textures.addProperty("all", texture);
		textures.addProperty("particle", "#all");

		JsonObject faces = new JsonObject();
		for (String dir : new String[]{"north", "south", "east", "west", "down"}) {
			faces.add(dir, plainFace(dir));
		}
		faces.add("up", rotatedFace("up", topRotation));

		JsonObject element = new JsonObject();
		element.add("from", intArr(0, 0, 0));
		element.add("to",   intArr(16, 16, 16));
		element.add("faces", faces);

		JsonArray elements = new JsonArray();
		elements.add(element);

		JsonObject root = new JsonObject();
		root.addProperty("parent", "minecraft:block/block");
		root.add("textures", textures);
		root.add("elements", elements);
		return root;
	}

	private static JsonObject plainFace(String cullface) {
		JsonObject face = new JsonObject();
		face.add("uv", intArr(0, 0, 16, 16));
		face.addProperty("texture", "#all");
		face.addProperty("cullface", cullface);
		return face;
	}

	private static JsonObject rotatedFace(String cullface, int uvRotation) {
		JsonObject face = plainFace(cullface);
		if (uvRotation != 0) face.addProperty("rotation", uvRotation);
		return face;
	}

	private static JsonArray intArr(int... values) {
		JsonArray arr = new JsonArray();
		for (int v : values) arr.add(v);
		return arr;
	}

	private static JsonObject rotatedCubeAllBlockState(String model) {
		JsonObject root = new JsonObject();
		JsonObject variants = new JsonObject();
		JsonArray weightedVariants = new JsonArray();

		for (int rotation : new int[]{0, 90, 180, 270}) {
			JsonObject variant = new JsonObject();
			variant.addProperty("model", model);
			if (rotation != 0) {
				variant.addProperty("y", rotation);
			}
			weightedVariants.add(variant);
		}

		variants.add("", weightedVariants);
		root.add("variants", variants);
		return root;
	}

	private static JsonObject axisBlockState(String model) {
		JsonObject root = new JsonObject();
		JsonObject variants = new JsonObject();

		JsonObject axisY = new JsonObject();
		axisY.addProperty("model", model);
		variants.add("axis=y", axisY);

		JsonObject axisZ = new JsonObject();
		axisZ.addProperty("model", model);
		axisZ.addProperty("x", 90);
		variants.add("axis=z", axisZ);

		JsonObject axisX = new JsonObject();
		axisX.addProperty("model", model);
		axisX.addProperty("x", 90);
		axisX.addProperty("y", 90);
		variants.add("axis=x", axisX);

		root.add("variants", variants);
		return root;
	}

	private static JsonObject tallFlowerBlockState(String lowerModel, String upperModel) {
		JsonObject root = new JsonObject();
		JsonObject variants = new JsonObject();

		JsonObject lower = new JsonObject();
		lower.addProperty("model", lowerModel);
		variants.add("half=lower", lower);

		JsonObject upper = new JsonObject();
		upper.addProperty("model", upperModel);
		variants.add("half=upper", upper);

		root.add("variants", variants);
		return root;
	}

	private static JsonObject cubeAllModel(String texture) {
		JsonObject textures = new JsonObject();
		textures.addProperty("all", texture);

		JsonObject root = new JsonObject();
		root.addProperty("parent", "minecraft:block/cube_all");
		root.add("textures", textures);
		return root;
	}

	private static JsonObject leavesModel(String texture) {
		JsonObject textures = new JsonObject();
		textures.addProperty("all", texture);

		JsonObject root = new JsonObject();
		root.addProperty("parent", "minecraft:block/leaves");
		root.add("textures", textures);
		return root;
	}

	private static JsonObject crossModel(String texture) {
		JsonObject textures = new JsonObject();
		textures.addProperty("cross", texture);

		JsonObject root = new JsonObject();
		root.addProperty("parent", "minecraft:block/cross");
		root.add("textures", textures);
		return root;
	}

	private static JsonObject flatLilyModel(String texture) {
		JsonObject textures = new JsonObject();
		textures.addProperty("lily", texture);
		textures.addProperty("particle", texture);

		JsonObject upFace = new JsonObject();
		upFace.add("uv", intArr(0, 0, 16, 16));
		upFace.addProperty("texture", "#lily");

		JsonObject downFace = new JsonObject();
		downFace.add("uv", intArr(0, 0, 16, 16));
		downFace.addProperty("texture", "#lily");
		downFace.addProperty("cullface", "down");

		JsonObject faces = new JsonObject();
		faces.add("up", upFace);
		faces.add("down", downFace);

		JsonObject element = new JsonObject();
		element.add("from", intArr(0, 0, 0));
		element.add("to", intArr(16, 1, 16));
		element.addProperty("shade", false);
		element.add("faces", faces);

		JsonArray elements = new JsonArray();
		elements.add(element);

		JsonObject root = new JsonObject();
		root.addProperty("ambientocclusion", false);
		root.add("textures", textures);
		root.add("elements", elements);
		return root;
	}

	private static JsonObject generatedItemModel(String texture) {
		JsonObject textures = new JsonObject();
		textures.addProperty("layer0", texture);

		JsonObject root = new JsonObject();
		root.addProperty("parent", "minecraft:item/generated");
		root.add("textures", textures);
		return root;
	}

	private static JsonObject itemDefinitionModel(String model) {
		JsonObject modelObject = new JsonObject();
		modelObject.addProperty("type", "minecraft:model");
		modelObject.addProperty("model", model);

		JsonObject root = new JsonObject();
		root.add("model", modelObject);
		return root;
	}

	private static JsonObject langFile() {
		JsonObject root = new JsonObject();
		root.addProperty("block.rawlands.salt_block", "Salt Block");
		root.addProperty("block.rawlands.coarse_salt", "Coarse Salt");
		root.addProperty("block.rawlands.fine_salt", "Fine Salt");
		root.addProperty("block.rawlands.dry_scrub", "Dry Scrub");
		root.addProperty("block.rawlands.olive_leaves", "Olive Leaves");
		root.addProperty("block.rawlands.broadleaf_lupine", "Broadleaf Lupine");
		root.addProperty("item.rawlands.salt_block", "Salt Block");
		root.addProperty("item.rawlands.coarse_salt", "Coarse Salt");
		root.addProperty("item.rawlands.fine_salt", "Fine Salt");
		root.addProperty("item.rawlands.dry_scrub", "Dry Scrub");
		root.addProperty("item.rawlands.olive_leaves", "Olive Leaves");
		root.addProperty("item.rawlands.broadleaf_lupine", "Broadleaf Lupine");
		root.addProperty("biome.rawlands.salt_flat", "Salt Flat");
		root.addProperty("biome.rawlands.shrubland", "Shrubland");
		root.addProperty("biome.rawlands.subalpine_meadow", "Subalpine Meadow");
		root.addProperty("biome.rawlands.mediterranean_scrubland", "Mediterranean Scrubland");
		root.addProperty("block.rawlands.short_cattail", "Short Cattail");
		root.addProperty("block.rawlands.tall_cattail",  "Tall Cattail");
		root.addProperty("item.rawlands.short_cattail",  "Short Cattail");
		root.addProperty("item.rawlands.tall_cattail",   "Tall Cattail");
		root.addProperty("biome.rawlands.flooded_delta", "Flooded Delta");
		root.addProperty("biome.rawlands.dead_forest", "Dead Forest");
		root.addProperty("block.rawlands.delta_lily", "Delta Lily");
		root.addProperty("item.rawlands.delta_lily", "Delta Lily");
		return root;
	}

	private static BufferedImage solid(int width, int height, Color color) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				image.setRGB(x, y, color.getRGB());
			}
		}
		return image;
	}

	private static BufferedImage noise(int width, int height, Color primary, Color secondary) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				boolean pickPrimary = ((x * 19) + (y * 31) + (x * y * 7)) % 5 != 0;
				image.setRGB(x, y, (pickPrimary ? primary : secondary).getRGB());
			}
		}
		return image;
	}

	private static BufferedImage scrubTexture() {
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Color transparent = new Color(0, 0, 0, 0);
		Color branch = new Color(0x72, 0x58, 0x36);
		Color highlight = new Color(0x8C, 0x6D, 0x45);

		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				image.setRGB(x, y, transparent.getRGB());
			}
		}

		for (int i = 3; i < 13; i++) {
			image.setRGB(8, i, branch.getRGB());
		}

		int[][] twigs = {
				{8, 6}, {7, 5}, {6, 4}, {5, 3},
				{8, 7}, {9, 6}, {10, 5}, {11, 4},
				{8, 9}, {7, 8}, {6, 7},
				{8, 10}, {9, 9}, {10, 8}
		};

		for (int[] twig : twigs) {
			image.setRGB(twig[0], twig[1], branch.getRGB());
		}

		image.setRGB(7, 6, highlight.getRGB());
		image.setRGB(9, 7, highlight.getRGB());
		image.setRGB(7, 9, highlight.getRGB());
		image.setRGB(10, 8, highlight.getRGB());
		return image;
	}

}

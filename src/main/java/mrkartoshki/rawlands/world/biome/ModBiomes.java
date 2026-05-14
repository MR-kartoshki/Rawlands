package mrkartoshki.rawlands.world.biome;

import mrkartoshki.rawlands.Rawlands;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public final class ModBiomes {

    public static final ResourceKey<Biome> SALT_FLAT               = key("salt_flat");
    public static final ResourceKey<Biome> SHRUBLAND               = key("shrubland");
    public static final ResourceKey<Biome> SUBALPINE_MEADOW        = key("subalpine_meadow");
    public static final ResourceKey<Biome> MEDITERRANEAN_SCRUBLAND = key("mediterranean_scrubland");
    public static final ResourceKey<Biome> FLOODED_DELTA           = key("flooded_delta");
    public static final ResourceKey<Biome> DEAD_FOREST             = key("dead_forest");
    public static final ResourceKey<Biome> TEMPERATE_RAINFOREST    = key("temperate_rainforest");
    public static final ResourceKey<Biome> ALPINE_FOREST           = key("alpine_forest");
    public static final ResourceKey<Biome> GRAVEL_FLATS            = key("gravel_flats");
    public static final ResourceKey<Biome> ROCKY_FIELDS            = key("rocky_fields");
    public static final ResourceKey<Biome> CORAL_FOREST            = key("coral_forest");

    private ModBiomes() {}

    private static ResourceKey<Biome> key(String name) {
        return ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, name));
    }
}

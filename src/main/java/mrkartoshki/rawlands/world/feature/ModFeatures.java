package mrkartoshki.rawlands.world.feature;

import mrkartoshki.rawlands.Rawlands;
import mrkartoshki.rawlands.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockBlobConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class ModFeatures {

    public static final Feature<NoneFeatureConfiguration> DEAD_CORAL_TREE =
        new DeadCoralTreeFeature(NoneFeatureConfiguration.CODEC);

    public static final Feature<BlockBlobConfiguration> LARGER_BLOCK_BLOB =
        new LargerBlockBlobFeature(BlockBlobConfiguration.CODEC);

    public static final Feature<NoneFeatureConfiguration> DEAD_OAK_TREE =
        new DeadOakFeature(NoneFeatureConfiguration.CODEC);

    public static final Feature<NoneFeatureConfiguration> DEAD_DARK_OAK_TREE =
        new DeadDarkOakFeature(NoneFeatureConfiguration.CODEC);

    public static final Feature<NoneFeatureConfiguration> DEAD_STUMP =
        new DeadStumpFeature(NoneFeatureConfiguration.CODEC);

    public static final Feature<NoneFeatureConfiguration> FALLEN_DEAD_LOG =
        new FallenDeadLogFeature(NoneFeatureConfiguration.CODEC);

    public static final Feature<NoneFeatureConfiguration> DEAD_THICK_DARK_OAK_TREE =
        new DeadThickDarkOakFeature(NoneFeatureConfiguration.CODEC);

    public static final Feature<NoneFeatureConfiguration> ALPINE_FOREST_OAK_TREE =
        new ProceduralTreeFeature(
            NoneFeatureConfiguration.CODEC,
            Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LEAVES.defaultBlockState(),
            5, 3, 0.32, 2, 3, 0.0, 0.0
        );

    public static final Feature<NoneFeatureConfiguration> MOSSWOOD_FOREST_OAK_TREE =
        new ProceduralTreeFeature(
            NoneFeatureConfiguration.CODEC,
            Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LEAVES.defaultBlockState(),
            5, 4, 0.32, 3, 3, 0.0, 0.05
        );

    public static final Feature<NoneFeatureConfiguration> OLIVE_TREE =
        new ProceduralTreeFeature(
            NoneFeatureConfiguration.CODEC,
            Blocks.OAK_LOG.defaultBlockState(), ModBlocks.OLIVE_LEAVES.defaultBlockState(),
            3, 2, 0.5, 3, 2, 0.35, 0.0
        );

    public static final Feature<NoneFeatureConfiguration> OLIVE_TREE_ALT =
        new ProceduralTreeFeature(
            NoneFeatureConfiguration.CODEC,
            Blocks.OAK_LOG.defaultBlockState(), ModBlocks.OLIVE_LEAVES.defaultBlockState(),
            4, 3, 0.5, 4, 3, 0.4, 0.0
        );

    public static final Feature<NoneFeatureConfiguration> AZALEA_TREE =
        new ProceduralTreeFeature(
            NoneFeatureConfiguration.CODEC,
            Blocks.OAK_LOG.defaultBlockState(),
            Blocks.AZALEA_LEAVES.defaultBlockState(),
            Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState(), 0.3,
            4, 2, 0.3, 3, 3, 0.1, 0.0
        );

    public static void register() {
        Registry.register(
            BuiltInRegistries.FEATURE,
            Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "dead_coral_tree"),
            DEAD_CORAL_TREE
        );
        Registry.register(
            BuiltInRegistries.FEATURE,
            Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "larger_block_blob"),
            LARGER_BLOCK_BLOB
        );
        Registry.register(
            BuiltInRegistries.FEATURE,
            Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "dead_oak_tree"),
            DEAD_OAK_TREE
        );
        Registry.register(
            BuiltInRegistries.FEATURE,
            Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "dead_dark_oak_tree"),
            DEAD_DARK_OAK_TREE
        );
        Registry.register(
            BuiltInRegistries.FEATURE,
            Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "dead_stump"),
            DEAD_STUMP
        );
        Registry.register(
            BuiltInRegistries.FEATURE,
            Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "fallen_dead_log"),
            FALLEN_DEAD_LOG
        );
        Registry.register(
            BuiltInRegistries.FEATURE,
            Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "dead_thick_dark_oak_tree"),
            DEAD_THICK_DARK_OAK_TREE
        );
        Registry.register(
            BuiltInRegistries.FEATURE,
            Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "alpine_forest_oak_tree"),
            ALPINE_FOREST_OAK_TREE
        );
        Registry.register(
            BuiltInRegistries.FEATURE,
            Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "mosswood_forest_oak_tree"),
            MOSSWOOD_FOREST_OAK_TREE
        );
        Registry.register(
            BuiltInRegistries.FEATURE,
            Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "olive_tree"),
            OLIVE_TREE
        );
        Registry.register(
            BuiltInRegistries.FEATURE,
            Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "olive_tree_alt"),
            OLIVE_TREE_ALT
        );
        Registry.register(
            BuiltInRegistries.FEATURE,
            Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "azalea_tree"),
            AZALEA_TREE
        );
    }
}

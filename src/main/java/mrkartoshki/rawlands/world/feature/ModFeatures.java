package mrkartoshki.rawlands.world.feature;

import mrkartoshki.rawlands.Rawlands;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
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
    }
}

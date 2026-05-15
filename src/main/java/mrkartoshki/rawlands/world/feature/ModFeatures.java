package mrkartoshki.rawlands.world.feature;

import mrkartoshki.rawlands.Rawlands;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class ModFeatures {

    public static final Feature<NoneFeatureConfiguration> DEAD_CORAL_TREE =
        new DeadCoralTreeFeature(NoneFeatureConfiguration.CODEC);

    public static void register() {
        Registry.register(
            BuiltInRegistries.FEATURE,
            Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "dead_coral_tree"),
            DEAD_CORAL_TREE
        );
    }
}

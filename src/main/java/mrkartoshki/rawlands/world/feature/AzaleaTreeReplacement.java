package mrkartoshki.rawlands.world.feature;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class AzaleaTreeReplacement {
    private static final ResourceKey<ConfiguredFeature<?, ?>> VANILLA_AZALEA_TREE = ResourceKey.create(
        Registries.CONFIGURED_FEATURE,
        Identifier.withDefaultNamespace("azalea_tree")
    );

    private AzaleaTreeReplacement() {
    }

    public static boolean appliesTo(PlacedFeature feature) {
        return feature.feature().is(VANILLA_AZALEA_TREE);
    }
}

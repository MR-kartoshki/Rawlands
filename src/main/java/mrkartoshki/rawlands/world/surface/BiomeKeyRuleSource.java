package mrkartoshki.rawlands.world.surface;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mrkartoshki.rawlands.Rawlands;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * A surface {@link SurfaceRules.RuleSource} that gates an inner rule on a biome,
 * matching <em>by {@link ResourceKey}</em> rather than by biome {@link net.minecraft.core.Holder}
 * identity.
 *
 * <p>Vanilla's {@link SurfaceRules#isBiome} was changed in 26.2 to hold a
 * {@code HolderSet<Biome>} and compare biomes by holder identity. That cannot work for
 * datapack-registered (JSON) biomes such as Rawlands' own biomes: their holders are created
 * fresh when the worldgen registries load, so they never match the holders any
 * registration-time {@code HolderGetter} could produce. Matching by {@code ResourceKey}
 * (interned, so {@code ==} is reliable) is robust for both vanilla and modded biomes — this
 * mirrors how TerraBlender's own namespaced rules dispatch on the biome key.
 */
public record BiomeKeyRuleSource(ResourceKey<Biome> biome, SurfaceRules.RuleSource then)
    implements SurfaceRules.RuleSource {

    public static final MapCodec<BiomeKeyRuleSource> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            ResourceKey.codec(Registries.BIOME).fieldOf("biome").forGetter(BiomeKeyRuleSource::biome),
            SurfaceRules.RuleSource.CODEC.fieldOf("then").forGetter(BiomeKeyRuleSource::then)
        ).apply(instance, BiomeKeyRuleSource::new)
    );

    /** Registers the rule codec so the rule type is recognised by the surface-rule codec dispatch. */
    public static void register() {
        Registry.register(
            BuiltInRegistries.MATERIAL_RULE,
            Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "biome_key"),
            CODEC
        );
    }

    @Override
    public MapCodec<? extends SurfaceRules.RuleSource> codec() {
        return CODEC;
    }

    @Override
    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
        SurfaceRules.SurfaceRule thenRule = this.then.apply(context);
        return (x, y, z) -> context.getBiome().is(this.biome) ? thenRule.tryApply(x, y, z) : null;
    }
}

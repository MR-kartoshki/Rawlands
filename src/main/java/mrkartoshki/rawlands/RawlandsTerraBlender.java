package mrkartoshki.rawlands;

import mrkartoshki.rawlands.block.ModBlocks;
import mrkartoshki.rawlands.world.biome.ModBiomes;
import mrkartoshki.rawlands.world.biome.RawlandsRegion;
import mrkartoshki.rawlands.world.surface.BiomeKeyRuleSource;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.TerraBlenderApi;

public class RawlandsTerraBlender implements TerraBlenderApi {

    @Override
    public void onTerraBlenderInitialized() {
        Regions.register(
                new RawlandsRegion(
                        Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "overworld")
                )
        );

        // 26.2.0.0.2: addSurfaceRules now takes a SurfaceRuleManager.RuleBuilder
        // (Function<HolderGetter<Biome>, RuleSource>) so it can construct rules that need a
        // biome registry lookup. Our rules dispatch on ResourceKey via BiomeKeyRuleSource and
        // never touch the HolderGetter, so the lambda just ignores its argument.
        SurfaceRuleManager.addSurfaceRules(
                SurfaceRuleManager.RuleCategory.OVERWORLD,
                Rawlands.MOD_ID,
                biomes -> SurfaceRules.sequence(
                        // SALT_FLAT — coarse salt on top, thin salt layer below, then vanilla
                        // stone. DEEP_UNDER_FLOOR is intentionally omitted: cave carvers dig
                        // through the pre-painted surface layer, so any salt painted deep would
                        // be exposed on cave walls. Two blocks of salt is enough for the visual.
                        new BiomeKeyRuleSource(ModBiomes.SALT_FLAT,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                SurfaceRules.state(ModBlocks.SALT_BLOCK.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
                                                SurfaceRules.state(ModBlocks.SALT_BLOCK.defaultBlockState()))
                                )
                        ),
                        // FLOODED_DELTA — mud only where flooded (at/below water level); above water gets vanilla grass
                        new BiomeKeyRuleSource(ModBiomes.FLOODED_DELTA,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
                                                        SurfaceRules.state(Blocks.MUD.defaultBlockState()))),
                                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.MUD.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.DIRT.defaultBlockState()))
                                )
                        ),
                        // DEAD_FOREST — coarse dirt surface with dirt beneath
                        new BiomeKeyRuleSource(ModBiomes.DEAD_FOREST,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                SurfaceRules.state(Blocks.COARSE_DIRT.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.DIRT.defaultBlockState()))
                                )
                        ),
                        // GRAVEL_FLATS — gravel surface, coarse dirt subsurface, stone deep under
                        new BiomeKeyRuleSource(ModBiomes.GRAVEL_FLATS,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                SurfaceRules.state(Blocks.GRAVEL.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.COARSE_DIRT.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.STONE.defaultBlockState()))
                                )
                        ),
                        // TEMPERATE_RAINFOREST — podzol surface with dirt beneath (natural forest floor)
                        new BiomeKeyRuleSource(ModBiomes.TEMPERATE_RAINFOREST,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                SurfaceRules.state(Blocks.PODZOL.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.DIRT.defaultBlockState()))
                                )
                        ),
                        // CORAL_FOREST — sand seafloor over sandstone (ocean biome replacing Warm Ocean)
                        new BiomeKeyRuleSource(ModBiomes.CORAL_FOREST,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                SurfaceRules.state(Blocks.SAND.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.SAND.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.SANDSTONE.defaultBlockState()))
                                )
                        ),
                        // ABYSSAL_TRENCHES — sand cave floor (seagrass-compatible), deepslate beneath
                        new BiomeKeyRuleSource(ModBiomes.ABYSSAL_TRENCHES,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                SurfaceRules.state(Blocks.SAND.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.DEEPSLATE.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.DEEPSLATE.defaultBlockState()))
                                )
                        ),
                        // MIST_COAST — gravel shore over stone
                        new BiomeKeyRuleSource(ModBiomes.MIST_COAST,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                SurfaceRules.state(Blocks.GRAVEL.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.STONE.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.STONE.defaultBlockState()))
                                )
                        ),
                        // GLACIAL_FLATS — snow surface with packed ice beneath, stone deep under
                        new BiomeKeyRuleSource(ModBiomes.GLACIAL_FLATS,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                SurfaceRules.state(Blocks.SNOW_BLOCK.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.PACKED_ICE.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.STONE.defaultBlockState()))
                                )
                        ),
                        // FJORDS — bare stone walls (steep check), gravel channel floors under
                        // water; land tops fall through to vanilla grass
                        new BiomeKeyRuleSource(ModBiomes.FJORDS,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.steep(),
                                                SurfaceRules.state(Blocks.STONE.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
                                                        SurfaceRules.state(Blocks.GRAVEL.defaultBlockState())))
                                )
                        ),
                        // DUNE_SEA — deep sand over sandstone
                        new BiomeKeyRuleSource(ModBiomes.DUNE_SEA,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                SurfaceRules.state(Blocks.SAND.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.SAND.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.SANDSTONE.defaultBlockState()))
                                )
                        ),
                        // ALPS — bare stone on the sheer cliff faces (steep check), permanent
                        // snow/packed-ice cap above the snowline, moss-covered gentle slopes below
                        new BiomeKeyRuleSource(ModBiomes.ALPS,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.steep(),
                                                SurfaceRules.state(Blocks.STONE.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(190), 1),
                                                SurfaceRules.sequence(
                                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                                SurfaceRules.state(Blocks.SNOW_BLOCK.defaultBlockState())),
                                                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
                                                                SurfaceRules.state(Blocks.PACKED_ICE.defaultBlockState()))
                                                )
                                        ),
                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                SurfaceRules.state(Blocks.MOSS_BLOCK.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.DIRT.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.STONE.defaultBlockState()))
                                )
                        ),
                        // MONSOON_FOREST — mud only where flooded (at/below water level); above water gets vanilla grass
                        new BiomeKeyRuleSource(ModBiomes.MONSOON_FOREST,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
                                                        SurfaceRules.state(Blocks.MUD.defaultBlockState()))),
                                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.MUD.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.DIRT.defaultBlockState()))
                                )
                        )
                )
        );
    }
}

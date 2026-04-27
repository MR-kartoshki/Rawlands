package mrkartoshki.rawlands;

import mrkartoshki.rawlands.block.ModBlocks;
import mrkartoshki.rawlands.world.biome.ModBiomes;
import mrkartoshki.rawlands.world.biome.RawlandsRegion;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
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

        SurfaceRuleManager.addSurfaceRules(
                SurfaceRuleManager.RuleCategory.OVERWORLD,
                Rawlands.MOD_ID,
                SurfaceRules.sequence(
                        // SALT_FLAT — coarse salt surface
                        SurfaceRules.ifTrue(
                                SurfaceRules.isBiome(ModBiomes.SALT_FLAT),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                SurfaceRules.state(ModBlocks.SALT_BLOCK.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
                                                SurfaceRules.state(ModBlocks.SALT_BLOCK.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR,
                                                SurfaceRules.state(ModBlocks.SALT_BLOCK.defaultBlockState()))
                                )
                        ),
                        // FLOODED_DELTA — mud only where flooded (at/below water level); above water gets vanilla grass
                        SurfaceRules.ifTrue(
                                SurfaceRules.isBiome(ModBiomes.FLOODED_DELTA),
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
                        SurfaceRules.ifTrue(
                                SurfaceRules.isBiome(ModBiomes.DEAD_FOREST),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                SurfaceRules.state(Blocks.COARSE_DIRT.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.DIRT.defaultBlockState()))
                                )
                        ),
                        // TEMPERATE_RAINFOREST — podzol surface with dirt beneath (natural forest floor)
                        SurfaceRules.ifTrue(
                                SurfaceRules.isBiome(ModBiomes.TEMPERATE_RAINFOREST),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                                SurfaceRules.state(Blocks.PODZOL.defaultBlockState())),
                                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
                                                SurfaceRules.state(Blocks.DIRT.defaultBlockState()))
                                )
                        )
                )
        );
    }
}

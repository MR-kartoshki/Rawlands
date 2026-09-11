package mrkartoshki.rawlands.mixin;

import mrkartoshki.rawlands.world.feature.AzaleaTreeReplacement;
import mrkartoshki.rawlands.world.feature.ModFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.RootSystemFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RootSystemFeature.class)
abstract class RootSystemAzaleaTreeMixin {
    @Redirect(
        method = "placeDirtAndTree",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/placement/PlacedFeature;place(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Z"
        )
    )
    private static boolean rawlands$replaceTallAzalea(
        PlacedFeature feature,
        WorldGenLevel level,
        ChunkGenerator chunkGenerator,
        RandomSource random,
        BlockPos pos
    ) {
        if (AzaleaTreeReplacement.appliesTo(feature) && random.nextInt(10) == 0) {
            return ModFeatures.TALL_AZALEA_TREE.place(
                NoneFeatureConfiguration.INSTANCE,
                level,
                chunkGenerator,
                random,
                pos
            );
        }
        return feature.place(level, chunkGenerator, random, pos);
    }
}

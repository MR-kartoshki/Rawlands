package mrkartoshki.rawlands.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import mrkartoshki.rawlands.world.feature.ModFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.RootSystemFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Replaces 1 in 10 vanilla rooted-azalea-tree placements with the mod's tall
 * azalea tree. The injection point is the single ConfiguredFeature.place()
 * call inside RootSystemFeature.placeDirtAndTree().
 */
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
        PlacedFeature instance,
        WorldGenLevel level,
        ChunkGenerator chunkGenerator,
        RandomSource random,
        BlockPos pos
    ) {
        if (random.nextInt(10) == 0) {
            return ModFeatures.TALL_AZALEA_TREE.place(
                NoneFeatureConfiguration.INSTANCE, level, chunkGenerator, random, pos
            );
        }
        return instance.place(level, chunkGenerator, random, pos);
    }
}

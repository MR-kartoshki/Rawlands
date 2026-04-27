package mrkartoshki.rawlands.mixin;

import mrkartoshki.rawlands.world.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.RootSystemFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RootSystemConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RootSystemFeature.class)
public class RootedAzaleaTreeMixin {

    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void rawlands$blockInDeadForest(
            FeaturePlaceContext<RootSystemConfiguration> ctx,
            CallbackInfoReturnable<Boolean> cir) {

        WorldGenLevel level = ctx.level();
        BlockPos origin = ctx.origin();
        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, origin.getX(), origin.getZ());
        Holder<Biome> biome = level.getBiome(new BlockPos(origin.getX(), surfaceY, origin.getZ()));

        if (biome.is(ModBiomes.DEAD_FOREST)) {
            cir.setReturnValue(false);
        }
    }
}

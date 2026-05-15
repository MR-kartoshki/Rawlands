package mrkartoshki.rawlands.particle;

import mrkartoshki.rawlands.Rawlands;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ModParticles {

    public static final SimpleParticleType BIOLUMINESCENT_ALGAE =
            FabricParticleTypes.simple();

    public static void register() {
        Registry.register(
                BuiltInRegistries.PARTICLE_TYPE,
                Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "bioluminescent_algae"),
                BIOLUMINESCENT_ALGAE
        );
    }
}

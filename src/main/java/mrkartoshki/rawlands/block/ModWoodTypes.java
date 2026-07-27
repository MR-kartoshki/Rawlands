package mrkartoshki.rawlands.block;

import mrkartoshki.rawlands.Rawlands;
import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public final class ModWoodTypes {

    public static final BlockSetType SEQUOIA_SET_TYPE =
        new BlockSetTypeBuilder().register(Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "sequoia"));

    public static final WoodType SEQUOIA_WOOD_TYPE =
        new WoodTypeBuilder().register(Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, "sequoia"), SEQUOIA_SET_TYPE);

    private ModWoodTypes() {}

    public static void init() {
        // No-op: referencing this class triggers the static field initializers above,
        // which register the BlockSetType/WoodType before any block that needs them.
    }
}

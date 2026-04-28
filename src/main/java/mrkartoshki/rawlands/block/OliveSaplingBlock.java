package mrkartoshki.rawlands.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class OliveSaplingBlock extends SaplingBlock {
    public OliveSaplingBlock(TreeGrower treeGrower, Properties properties) {
        super(treeGrower, properties);
    }
    public static final TreeGrower OLIVE_TREE = new TreeGrower(
            "olive",
            0.4F,
            Optional.empty(),
            Optional.empty(),
            Optional.of(ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath("rawlands", "olive_tree"))),
            Optional.of(ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath("rawlands","olive_tree_alt"))),
            Optional.empty(),
            Optional.empty()
    );
}

package mrkartoshki.rawlands;

import mrkartoshki.rawlands.block.ModBlocks;
import mrkartoshki.rawlands.item.ModItems;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups {
    public static final ResourceKey<CreativeModeTab> RAWLANDS_KEY = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath("rawlands", "rawlands")
    );

    public static final CreativeModeTab RAWLANDS = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> new ItemStack(ModBlocks.SHORT_CATTAIL))
            .title(Component.translatable("itemGroup.rawlands"))
            .displayItems((params, output) -> {
                output.accept(ModBlocks.OLIVE_SAPLING);
                output.accept(ModBlocks.SALT_BLOCK);
                output.accept(ModBlocks.COARSE_SALT);
                output.accept(ModBlocks.FINE_SALT);
                output.accept(ModBlocks.DRY_SCRUB);
                output.accept(ModBlocks.OLIVE_LEAVES);
                output.accept(ModBlocks.BROADLEAF_LUPINE);
                output.accept(ModBlocks.SHORT_CATTAIL);
                output.accept(ModBlocks.TALL_CATTAIL);
                output.accept(ModBlocks.NIGHTSHADE);
                output.accept(ModBlocks.DELTA_LILY);
                output.accept(ModItems.OLIVE);
            })
            .build();

    public static void initialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, RAWLANDS_KEY, RAWLANDS);
    }

}

package mrkartoshki.rawlands.item;

import java.util.ArrayList;
import java.util.List;

import mrkartoshki.rawlands.Rawlands;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.Consumable;

public final class ModItems {
	private static final List<Item> REGISTERED_ITEMS = new ArrayList<>();

	public static final Item OLIVE = register(
			"olive",
			new Item(new Item.Properties()
					.setId(ResourceKey.create(Registries.ITEM, id("olive")))
					.food(new FoodProperties.Builder()
							.nutrition(2)
							.saturationModifier(0.3f)
							.build())
					.component(net.minecraft.core.component.DataComponents.CONSUMABLE,
							net.minecraft.world.item.component.Consumable.builder()
									.consumeSeconds(0.4f) // fast eating
									.build()
					)
			)
	);

	private ModItems() {
	}

	public static void initialize() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(output -> {
			output.accept(OLIVE);
		});
	}

	public static List<Item> allItems() {
		return List.copyOf(REGISTERED_ITEMS);
	}

	private static Item register(String name, Item item) {
		Identifier id = id(name);
		Item registeredItem = Registry.register(BuiltInRegistries.ITEM, id, item);
		REGISTERED_ITEMS.add(registeredItem);
		return registeredItem;
	}

	private static Identifier id(String name) {
		return Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, name);
	}
}

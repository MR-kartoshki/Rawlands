package mrkartoshki.rawlands.entity;

import mrkartoshki.rawlands.Rawlands;
import mrkartoshki.rawlands.item.ModItems;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;

public final class ModEntityTypes {

    public static final EntityType<Boat> SEQUOIA_BOAT = register(
        "sequoia_boat",
        EntityType.Builder.of(boatFactory(), MobCategory.MISC)
            .noLootTable()
            .sized(1.375F, 0.5625F)
            .eyeHeight(0.5625F)
            .clientTrackingRange(10)
    );

    public static final EntityType<ChestBoat> SEQUOIA_CHEST_BOAT = register(
        "sequoia_chest_boat",
        EntityType.Builder.of(chestBoatFactory(), MobCategory.MISC)
            .noLootTable()
            .sized(1.375F, 0.5625F)
            .eyeHeight(0.5625F)
            .clientTrackingRange(10)
    );

    private ModEntityTypes() {}

    private static EntityType.EntityFactory<Boat> boatFactory() {
        return (type, level) -> new Boat(type, level, () -> ModItems.SEQUOIA_BOAT);
    }

    private static EntityType.EntityFactory<ChestBoat> chestBoatFactory() {
        return (type, level) -> new ChestBoat(type, level, () -> ModItems.SEQUOIA_CHEST_BOAT);
    }

    public static void register() {
        // No-op: referencing this class triggers the static field initializers above.
    }

    private static <T extends net.minecraft.world.entity.Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        Identifier id = Identifier.fromNamespaceAndPath(Rawlands.MOD_ID, name);
        ResourceKey<EntityType<?>> key = ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, id);
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, builder.build(key));
    }
}

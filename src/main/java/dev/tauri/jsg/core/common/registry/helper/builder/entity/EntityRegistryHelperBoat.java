package dev.tauri.jsg.core.common.registry.helper.builder.entity;

import dev.tauri.jsg.core.client.entity.vehicle.boat.JSGBoatRendererProvider;
import dev.tauri.jsg.core.common.entity.vehicle.JSGBoat;
import dev.tauri.jsg.core.common.entity.vehicle.JSGBoatTypeWrapper;
import dev.tauri.jsg.core.common.entity.vehicle.JSGChestBoat;
import dev.tauri.jsg.core.common.item.BoatItem;
import dev.tauri.jsg.core.common.registry.helper.builder.RegistryHelper;
import dev.tauri.jsg.core.common.registry.helper.builder.RegistryObjectBuilder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EntityRegistryHelperBoat extends RegistryHelper<EntityType<?>, EntityRegistryHelperBoat.BoatEntityBuilder> {
    public final Supplier<DeferredRegister<Item>> itemRegistry;

    public EntityRegistryHelperBoat(Supplier<DeferredRegister<Item>> itemRegistry, Supplier<DeferredRegister<EntityType<?>>> registry) {
        super(registry, BoatEntityBuilder::new);
        this.itemRegistry = itemRegistry;
    }

    public record BoatRegistryObject(RegistryObject<EntityType<JSGBoat<?>>> entity,
                                     JSGBoatRendererProvider rendererProvider,
                                     Map<JSGBoatTypeWrapper.Type, RegistryObject<BoatItem<?>>> item) {
    }

    public record ChestBoatRegistryObject(RegistryObject<EntityType<JSGChestBoat<?>>> entity,
                                          JSGBoatRendererProvider rendererProvider,
                                          Map<JSGBoatTypeWrapper.Type, RegistryObject<BoatItem<?>>> item) {
    }

    public static class BoatEntityBuilder extends RegistryObjectBuilder<RegistryHelper<EntityType<?>, ? extends BoatEntityBuilder>> {
        public BoatEntityBuilder(RegistryHelper<EntityType<?>, ? extends BoatEntityBuilder> registryHelper, String name) {
            super(registryHelper, name);
        }

        public BoatEntityBuilder(String name, BoatEntityBuilder other) {
            super(name, other);
        }

        protected Item.Properties properties = new Item.Properties();

        public BoatEntityBuilder setProperties(Item.Properties properties) {
            this.properties = properties;
            return this;
        }

        public <T extends Enum<T> & JSGBoatTypeWrapper.Type> BoatRegistryObject buildBoats(final @NotNull JSGBoatTypeWrapper<T> boatTypeWrapper) {
            var entityType = registryHelper.registry.get().register(name, () -> EntityType.Builder.<JSGBoat<?>>of((type, level) -> new JSGBoat<>(boatTypeWrapper, type, level), MobCategory.MISC)
                    .sized(1.375f, 0.5625f).build(name));

            var itemMap = new HashMap<JSGBoatTypeWrapper.Type, RegistryObject<BoatItem<?>>>();
            for (var type : boatTypeWrapper.values)
                itemMap.put(type, ((EntityRegistryHelperBoat) registryHelper).itemRegistry.get().register(type.getSerializedName().toLowerCase() + "_" + name, () -> BoatItem.createNormal(entityType, boatTypeWrapper, type, properties, tabs)));

            return new BoatRegistryObject(entityType, new JSGBoatRendererProvider.Normal(boatTypeWrapper), itemMap);
        }

        public <T extends Enum<T> & JSGBoatTypeWrapper.Type> ChestBoatRegistryObject buildChestBoats(final @NotNull JSGBoatTypeWrapper<T> boatTypeWrapper) {
            var entityType = registryHelper.registry.get().register(name, () -> EntityType.Builder.<JSGChestBoat<?>>of((type, level) -> new JSGChestBoat<>(boatTypeWrapper, type, level), MobCategory.MISC)
                    .sized(1.375f, 0.5625f).build(name));

            var itemMap = new HashMap<JSGBoatTypeWrapper.Type, RegistryObject<BoatItem<?>>>();
            for (var type : boatTypeWrapper.values)
                itemMap.put(type, ((EntityRegistryHelperBoat) registryHelper).itemRegistry.get().register(type.getSerializedName().toLowerCase() + "_" + name, () -> BoatItem.createChest(entityType, boatTypeWrapper, type, properties, tabs)));

            return new ChestBoatRegistryObject(entityType, new JSGBoatRendererProvider.Chest(boatTypeWrapper), itemMap);
        }
    }
}

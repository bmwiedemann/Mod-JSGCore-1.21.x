package dev.tauri.jsg.core.common.capability;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.blockentity.JSGFluidHandlerBE;
import dev.tauri.jsg.core.common.item.EnergyItem;
import dev.tauri.jsg.core.common.item.JSGBucketItem;
import dev.tauri.jsg.core.common.registry.CoreBlockEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Central capability registration replacing the 1.20.1 getCapability/initCapabilities
 * overrides. Energy items and JSG buckets are picked up automatically from the item
 * registry (covers addon items too); fluid-tank block entity types must be enqueued
 * via {@link #registerFluidHandlerBE} during registration setup.
 */
@EventBusSubscriber(modid = JSGCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CoreCapabilities {
    private static final List<Supplier<BlockEntityType<?>>> FLUID_HANDLER_BES = new ArrayList<>();

    public static void registerFluidHandlerBE(Supplier<BlockEntityType<?>> beType) {
        FLUID_HANDLER_BES.add(beType);
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof EnergyItem energyItem) {
                event.registerItem(Capabilities.EnergyStorage.ITEM,
                        (stack, ignored) -> energyItem.createEnergyStorage(stack), item);
            }
            if (item instanceof JSGBucketItem) {
                event.registerItem(Capabilities.FluidHandler.ITEM,
                        (stack, ignored) -> new FluidBucketWrapper(stack), item);
            }
        }
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, CoreBlockEntities.FLUID_CAULDRON.get(),
                (be, side) -> be.getFluidHandler(side));

        for (var beType : FLUID_HANDLER_BES) {
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, beType.get(),
                    (be, side) -> ((JSGFluidHandlerBE) be).getFluidHandler(side));
        }
    }
}

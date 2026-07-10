package dev.tauri.jsg.core.common.integration.oc2;

import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.integration.oc2.methods.IOCDevice;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import javax.annotation.Nullable;

public interface OCIntegrationWrapper {
    boolean isLoaded();

    @Nullable
    IOCDevice createDevice(ComputerDeviceProvider tile, String deviceType);

    default void registerPeripheralBE(RegisterCapabilitiesEvent event, BlockEntityType<?> beType) {
    }
}

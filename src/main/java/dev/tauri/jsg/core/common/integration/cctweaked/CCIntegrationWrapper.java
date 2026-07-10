package dev.tauri.jsg.core.common.integration.cctweaked;

import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.integration.cctweaked.methods.ICCDevice;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import javax.annotation.Nullable;

public interface CCIntegrationWrapper {
    boolean isLoaded();

    @Nullable
    ICCDevice createDevice(ComputerDeviceProvider tile, String deviceType);

    /**
     * Expose block entities of the given type as CC peripherals
     * (NeoForge: peripherals are looked up through {@code PeripheralCapability}).
     */
    default void registerPeripheralBE(RegisterCapabilitiesEvent event, BlockEntityType<?> beType) {
    }
}

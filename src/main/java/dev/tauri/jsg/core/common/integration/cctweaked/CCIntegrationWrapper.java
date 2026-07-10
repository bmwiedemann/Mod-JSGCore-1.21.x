package dev.tauri.jsg.core.common.integration.cctweaked;

import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.integration.cctweaked.methods.ICCDevice;
import net.neoforged.neoforge.capabilities.Capability;
import net.neoforged.neoforge.common.util.LazyOptional;

import java.util.Optional;

public interface CCIntegrationWrapper {
    boolean isLoaded();

    boolean checkCaps(Capability<?> caps);

    Optional<Capability<?>> getCaps();

    <T> LazyOptional<ICCDevice> createDevice(Capability<T> cap, ComputerDeviceProvider tile, String deviceType);
}

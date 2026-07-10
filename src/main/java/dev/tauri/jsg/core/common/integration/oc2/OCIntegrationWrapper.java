package dev.tauri.jsg.core.common.integration.oc2;

import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.integration.oc2.methods.IOCDevice;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Optional;

public interface OCIntegrationWrapper {
    boolean isLoaded();
    boolean checkCaps(Capability<?> caps);

    Optional<Capability<?>> getCaps();

    <T> LazyOptional<IOCDevice> createDevice(Capability<T> cap, ComputerDeviceProvider provider, String deviceType);
}

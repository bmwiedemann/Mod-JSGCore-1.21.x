package dev.tauri.jsg.core.common.integration.oc2;

import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.integration.oc2.methods.IOCDevice;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Optional;

@SuppressWarnings("unused")
public class OCIntegrationNotLoaded implements OCIntegrationWrapper {
    @Override
    public boolean isLoaded() {
        return false;
    }

    @Override
    public boolean checkCaps(Capability<?> caps) {
        return false;
    }

    @Override
    public Optional<Capability<?>> getCaps() {
        return Optional.empty();
    }

    @Override
    public <T> LazyOptional<IOCDevice> createDevice(Capability<T> cap, ComputerDeviceProvider provider, String deviceType) {
        return LazyOptional.empty();
    }
}

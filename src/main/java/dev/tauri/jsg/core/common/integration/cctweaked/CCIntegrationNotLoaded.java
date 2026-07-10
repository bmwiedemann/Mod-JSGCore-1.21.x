package dev.tauri.jsg.core.common.integration.cctweaked;

import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.integration.cctweaked.methods.ICCDevice;
import net.neoforged.neoforge.capabilities.Capability;
import net.neoforged.neoforge.common.util.LazyOptional;

import java.util.Optional;

@SuppressWarnings("unused")
public class CCIntegrationNotLoaded implements CCIntegrationWrapper {
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
    public <T> LazyOptional<ICCDevice> createDevice(Capability<T> cap, ComputerDeviceProvider tile, String deviceType) {
        return LazyOptional.empty();
    }
}

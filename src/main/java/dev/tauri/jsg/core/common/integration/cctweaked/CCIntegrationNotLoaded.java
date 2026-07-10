package dev.tauri.jsg.core.common.integration.cctweaked;

import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.integration.cctweaked.methods.ICCDevice;

public class CCIntegrationNotLoaded implements CCIntegrationWrapper {
    @Override
    public boolean isLoaded() {
        return false;
    }

    @Override
    public ICCDevice createDevice(ComputerDeviceProvider tile, String deviceType) {
        return null;
    }
}

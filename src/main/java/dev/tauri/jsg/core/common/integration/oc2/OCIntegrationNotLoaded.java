package dev.tauri.jsg.core.common.integration.oc2;

import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.integration.oc2.methods.IOCDevice;

public class OCIntegrationNotLoaded implements OCIntegrationWrapper {
    @Override
    public boolean isLoaded() {
        return false;
    }

    @Override
    public IOCDevice createDevice(ComputerDeviceProvider tile, String deviceType) {
        return null;
    }
}

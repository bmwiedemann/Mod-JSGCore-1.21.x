package dev.tauri.jsg.core.common.integration.oc2;

import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.integration.oc2.methods.IOCDevice;
import li.cil.oc2.api.bus.device.Device;
import li.cil.oc2.api.bus.device.object.ObjectDevice;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Optional;

@SuppressWarnings("unused")
public class OCIntegrationLoaded implements dev.tauri.jsg.core.common.integration.oc2.OCIntegrationWrapper {
    private static final Capability<Device> CAP = CapabilityManager.get(new CapabilityToken<>() {
    });

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public boolean checkCaps(Capability<?> caps) {
        return caps == CAP;
    }

    @Override
    public Optional<Capability<?>> getCaps() {
        return Optional.of(CAP);
    }

    @Override
    public <T> LazyOptional<IOCDevice> createDevice(Capability<T> cap, ComputerDeviceProvider provider, String deviceType) {
        return LazyOptional.of(() -> {
            var device = OCDevice.valueOf(deviceType.toUpperCase());
            return new ObjectDevice(device.constructor.construct(provider), device.deviceName);
        }).cast();
    }
}

package dev.tauri.jsg.core.common.integration;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.integration.cctweaked.methods.ICCDevice;
import dev.tauri.jsg.core.common.integration.oc2.methods.IOCDevice;

import javax.annotation.Nullable;

public class ComputerDeviceHolder implements dev.tauri.jsg.core.common.integration.IComputerDeviceHolder {
    private ICCDevice ccDevice;
    private IOCDevice ocDevice;

    public final ComputerDeviceProvider provider; // usually stargate block entity

    public ComputerDeviceHolder(ComputerDeviceProvider provider) {
        this.provider = provider;
    }

    @Nullable
    public ICCDevice getOrCreateCCDevice() {
        if (!JSGCore.ccWrapper.isLoaded()) return null;
        if (ccDevice == null) {
            ccDevice = JSGCore.ccWrapper.createDevice(provider, provider.getDeviceType());
        }
        return ccDevice;
    }

    @Nullable
    public IOCDevice getOrCreateOCDevice() {
        if (!JSGCore.ocWrapper.isLoaded()) return null;
        if (ocDevice == null) {
            ocDevice = JSGCore.ocWrapper.createDevice(provider, provider.getDeviceType());
        }
        return ocDevice;
    }


    public void sendSignal(String eventName, Object... objects) {
        sendSignalCC(eventName, objects);
        sendSignalOC(eventName, objects);
    }

    public void connectToWirelessNetwork() {
        connectToWirelessNetworkCC();
    }

    public void disconnectFromWirelessNetwork() {
        disconnectFromWirelessNetworkCC();
    }


    // WIRED SIGNALS
    public void sendSignalCC(String eventName, Object... objects) {
        var device = getOrCreateCCDevice();
        if (device != null) device.sendSignal(eventName, objects);
    }

    public void sendSignalOC(String eventName, Object... objects) {
        var device = getOrCreateOCDevice();
        if (device != null) device.sendSignal(eventName, objects);
    }

    // WIRELESS NETWORK CONNECTION
    public void connectToWirelessNetworkCC() {
        var device = getOrCreateCCDevice();
        if (device != null) device.connectToWirelessNetwork();
    }

    public void disconnectFromWirelessNetworkCC() {
        var device = getOrCreateCCDevice();
        if (device != null) device.disconnectFromWirelessNetwork();
    }
}

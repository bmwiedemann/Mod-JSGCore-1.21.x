package dev.tauri.jsg.core.common.integration;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.integration.cctweaked.methods.ICCDevice;
import dev.tauri.jsg.core.common.integration.oc2.methods.IOCDevice;
import net.neoforged.neoforge.capabilities.Capability;
import net.neoforged.neoforge.common.util.LazyOptional;

public class ComputerDeviceHolder implements dev.tauri.jsg.core.common.integration.IComputerDeviceHolder {
    private LazyOptional<ICCDevice> ccDevice = LazyOptional.empty();
    private LazyOptional<IOCDevice> ocDevice = LazyOptional.empty();

    public final ComputerDeviceProvider provider; // usually stargate block entity

    public ComputerDeviceHolder(ComputerDeviceProvider provider) {
        this.provider = provider;
    }

    public <T> LazyOptional<T> getOrCreateDeviceBasedOnCap(Capability<T> cap) {
        var cc = getOrCreateCCDevice(cap);
        if (cc.isPresent()) return cc;
        var oc = getOrCreateOCDevice(cap);
        if (oc.isPresent()) return oc;
        return LazyOptional.empty();
    }

    public <T> LazyOptional<T> getOrCreateCCDevice(Capability<T> cap) {
        if (!JSGCore.ccWrapper.isLoaded()) return LazyOptional.empty();
        if (JSGCore.ccWrapper.checkCaps(cap)) {
            if (ccDevice.isPresent()) return ccDevice.cast();
            ccDevice = JSGCore.ccWrapper.createDevice(cap, provider, provider.getDeviceType());
            return ccDevice.cast();
        }
        return LazyOptional.empty();
    }

    public <T> LazyOptional<T> getOrCreateOCDevice(Capability<T> cap) {
        if (!JSGCore.ocWrapper.isLoaded()) return LazyOptional.empty();
        if (JSGCore.ocWrapper.checkCaps(cap)) {
            if (ocDevice.isPresent()) return ocDevice.cast();
            ocDevice = JSGCore.ocWrapper.createDevice(cap, provider, provider.getDeviceType());
            return ocDevice.cast();
        }
        return LazyOptional.empty();
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
        var caps = JSGCore.ccWrapper.getCaps();
        if (caps.isEmpty()) return;
        var opt = getOrCreateCCDevice(caps.get());
        if (!opt.isPresent()) return;
        var peripheral = opt.resolve().orElseThrow();
        if (!(peripheral instanceof ICCDevice device)) return;
        device.sendSignal(eventName, objects);
    }

    public void sendSignalOC(String eventName, Object... objects) {
        var caps = JSGCore.ocWrapper.getCaps();
        if (caps.isEmpty()) return;
        var opt = getOrCreateCCDevice(caps.get());
        if (!opt.isPresent()) return;
        var peripheral = opt.resolve().orElseThrow();
        if (!(peripheral instanceof IOCDevice device)) return;
        device.sendSignal(eventName, objects);
    }

    // WIRELESS NETWORK CONNECTION
    public void connectToWirelessNetworkCC() {
        var caps = JSGCore.ccWrapper.getCaps();
        if (caps.isEmpty()) return;
        var opt = getOrCreateCCDevice(caps.get());
        if (!opt.isPresent()) return;
        var peripheral = opt.resolve().orElseThrow();
        if (!(peripheral instanceof ICCDevice device)) return;
        device.connectToWirelessNetwork();
    }

    public void disconnectFromWirelessNetworkCC() {
        var caps = JSGCore.ccWrapper.getCaps();
        if (caps.isEmpty()) return;
        var opt = getOrCreateCCDevice(caps.get());
        if (!opt.isPresent()) return;
        var peripheral = opt.resolve().orElseThrow();
        if (!(peripheral instanceof ICCDevice device)) return;
        device.disconnectFromWirelessNetwork();
    }
}

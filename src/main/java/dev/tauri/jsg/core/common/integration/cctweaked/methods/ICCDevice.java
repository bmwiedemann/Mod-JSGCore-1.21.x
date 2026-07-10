package dev.tauri.jsg.core.common.integration.cctweaked.methods;

public interface ICCDevice {
    void sendSignal(String eventName, Object... objects);

    void connectToWirelessNetwork();

    void disconnectFromWirelessNetwork();
}

package dev.tauri.jsg.core.common.integration;

public interface IComputerDeviceHolder {
    void sendSignal(String eventName, Object... objects);
}

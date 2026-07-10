package dev.tauri.jsg.core.common.integration.oc2.methods;

public interface IOCDevice {
    void sendSignal(String eventName, Object... objects);
}

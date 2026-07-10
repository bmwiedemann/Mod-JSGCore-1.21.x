package dev.tauri.jsg.core.common.integration;

public interface ComputerDeviceProvider {
    String getDeviceType();

    IComputerDeviceHolder getDeviceHolder();

    default void sendSignal(SignalHolder signalHolder) {
        sendSignal(signalHolder.eventName(), signalHolder.args());
    }

    default void sendSignal(String eventName, Object... objects) {
        getDeviceHolder().sendSignal(eventName, objects);
    }
}

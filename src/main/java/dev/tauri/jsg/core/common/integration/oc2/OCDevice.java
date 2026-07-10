package dev.tauri.jsg.core.common.integration.oc2;

import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;

import java.util.HashMap;

public class OCDevice {
    private static final HashMap<String, OCDevice> REGISTRY = new HashMap<>();

    public final String name;
    public final IConstructable constructor;
    public final String deviceName;

    public OCDevice(String name, String deviceName, IConstructable c) {
        this.name = name;
        this.deviceName = deviceName;
        this.constructor = c;
        REGISTRY.put(name, this);
    }

    public static OCDevice valueOf(String name) {
        return REGISTRY.get(name);
    }

    public interface IConstructable {
        Object construct(ComputerDeviceProvider tile);
    }
}

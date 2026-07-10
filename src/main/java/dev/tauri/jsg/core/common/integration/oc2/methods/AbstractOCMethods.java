package dev.tauri.jsg.core.common.integration.oc2.methods;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.tauri.jsg.core.common.integration.ComputerDeviceProvider;
import dev.tauri.jsg.core.common.integration.oc2.OCDevice;
import li.cil.oc2.api.bus.device.rpc.IEventSink;
import li.cil.oc2.api.bus.device.rpc.RPCEventSource;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractOCMethods<TILE extends ComputerDeviceProvider> implements RPCEventSource, IOCDevice {
    protected final TILE deviceTile;
    protected final OCDevice device;
    protected final UUID deviceUUID;
    protected final Map<IEventSink, UUID> computers = new HashMap<>();

    public AbstractOCMethods(TILE deviceTile, OCDevice device) {
        this.deviceTile = deviceTile;
        this.device = device;
        this.deviceUUID = UUID.randomUUID();
    }

    @ParametersAreNonnullByDefault
    public void subscribe(IEventSink device, UUID deviceID) {
        computers.put(device, deviceID);
    }

    @ParametersAreNonnullByDefault
    public void unsubscribe(IEventSink device) {
        computers.remove(device);
    }

    @Override
    public void sendSignal(String eventName, Object... objects) {
        var event = new JsonObject();
        event.addProperty("name", eventName);
        var data = new JsonArray();
        for (var o : objects) {
            if (o instanceof Boolean bool)
                data.add(bool);
            else if (o instanceof Double d)
                data.add(d);
            else if (o instanceof Float f)
                data.add(f);
            else if (o instanceof Integer integer)
                data.add(integer);
            else
                data.add(o.toString());
        }
        event.add("data", data);
        for (var computer : computers.entrySet()) {
            computer.getKey().postEvent(deviceUUID, event.deepCopy());
        }
    }
}

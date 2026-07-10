package dev.tauri.jsg.core.common.event.config;

import com.google.gson.JsonElement;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import dev.tauri.jsg.core.common.config.json.dimension.JSGDimensionConfig;
import dev.tauri.jsg.core.common.event.JSGEvent;

import java.util.Optional;
import java.util.function.Function;

/**
 * Event is fired BEFORE the dimension config is loaded (or created as a file).
 * You can inject into default values or add custom fields into the config
 */
public class DimensionConfigPreRegisterEvent extends JSGEvent {
    protected final JSGDimensionConfig config;

    public DimensionConfigPreRegisterEvent(JSGDimensionConfig config) {
        this.config = config;
    }

    public JSGDimensionConfig config() {
        return config;
    }

    public void registerField(String name, Function<JsonOps, Dynamic<JsonElement>> dynamicFunction) {
        JSGDimensionConfig.Entry.registerField(name, dynamicFunction);
    }

    public void registerEntry(String name, Function<JsonOps, JSGDimensionConfig.Entry> entryFunction) {
        JSGDimensionConfig.DEFAULTS.put(name, entryFunction);
    }

    public void modifyEntry(String name, Function<Function<JsonOps, JSGDimensionConfig.Entry>, Function<JsonOps, JSGDimensionConfig.Entry>> consumer) {
        Optional.ofNullable(JSGDimensionConfig.DEFAULTS.get(name)).ifPresent(original -> JSGDimensionConfig.DEFAULTS.put(name, consumer.apply(original)));
    }
}

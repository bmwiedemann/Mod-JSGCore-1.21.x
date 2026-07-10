package dev.tauri.jsg.core.common.config.ingame;

public interface IConfigurable {
    BEConfig getConfig();

    default void setConfig(BEConfig config) {
        var oldConfig = getConfig();
        for (var o : config.getOptions().entrySet()) {
            oldConfig.getOption(o.getKey()).ifPresent((oldOption) -> oldOption.parseAndSetValue(o.getValue().getValue()));
        }
        onConfigUpdated();
    }

    default void onConfigUpdated() {
    }
}

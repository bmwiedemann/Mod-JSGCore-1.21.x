package dev.tauri.jsg.core.common.config.ingame.option;

import dev.tauri.jsg.core.common.config.ingame.BEConfigOptionProvider;
import dev.tauri.jsg.core.common.config.ingame.IBEConfigOption;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class ConfigOptionsHolder {
    public static final ConfigOptionsHolder EMPTY = new ConfigOptionsHolder();

    protected final LinkedHashMap<String, BEConfigOptionProvider<?>> providers = new LinkedHashMap<>();

    public ConfigOptionsHolder() {
    }

    public ConfigOptionsHolder(@NotNull ConfigOptionsHolder holder) {
        copy(holder);
    }

    public <T> BEConfigOptionProvider<T> register(String id, Function<Runnable, IBEConfigOption<T>> supplier) {
        var provider = new BEConfigOptionProvider<>(id, supplier);
        providers.put(id, provider);
        return provider;
    }

    public void copy(ConfigOptionsHolder holder) {
        holder.providers.forEach((id, provider) -> this.register(id, provider::create));
    }

    public void init(Runnable onChanged, Map<String, IBEConfigOption<?>> targetMap) {
        targetMap.clear();
        providers.forEach((id, provider) -> targetMap.put(id, provider.create(onChanged)));
    }

    public <T> Optional<T> parseOptionValue(BEConfigOptionProvider<T> provider, Object value) {
        return provider.getStaticInstance().parseValue(value);
    }
}

package dev.tauri.jsg.core.common.config.ingame;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class BEConfigOptionProvider<T> {
    private final String id;
    private final Function<Runnable, IBEConfigOption<T>> optionFactory;
    private final AtomicReference<IBEConfigOption<T>> staticInstance = new AtomicReference<>();

    public BEConfigOptionProvider(String id, Function<Runnable, IBEConfigOption<T>> optionFactory) {
        this.id = id;
        this.optionFactory = optionFactory;
    }

    public IBEConfigOption<T> getStaticInstance() {
        if (staticInstance.get() == null) {
            staticInstance.set(optionFactory.apply(() -> {
            }));
        }
        return staticInstance.get();
    }

    public IBEConfigOption<T> create(Runnable onChanged) {
        return optionFactory.apply(onChanged);
    }

    public String getId() {
        return id;
    }
}

package dev.tauri.jsg.core.common.registry.helper.builder;

import dev.tauri.jsg.core.common.registry.JSGDeferredRegister;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class RegistryHelper<T, B extends RegistryObjectBuilder<?>> {
    public final Supplier<JSGDeferredRegister<T>> registry;
    public final BiFunction<RegistryHelper<T, B>, String, B> builderGetter;

    public RegistryHelper(Supplier<JSGDeferredRegister<T>> registry, BiFunction<RegistryHelper<T, B>, String, B> builderGetter) {
        this.registry = registry;
        this.builderGetter = builderGetter;
    }

    public B builder(String name) {
        return builderGetter.apply(this, name);
    }
}

package dev.tauri.jsg.core.common.registry.helper.builder;

import net.minecraftforge.registries.DeferredRegister;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class RegistryHelper<T, B extends RegistryObjectBuilder<?>> {
    public final Supplier<DeferredRegister<T>> registry;
    public final BiFunction<RegistryHelper<T, B>, String, B> builderGetter;

    public RegistryHelper(Supplier<DeferredRegister<T>> registry, BiFunction<RegistryHelper<T, B>, String, B> builderGetter) {
        this.registry = registry;
        this.builderGetter = builderGetter;
    }

    public B builder(String name) {
        return builderGetter.apply(this, name);
    }
}

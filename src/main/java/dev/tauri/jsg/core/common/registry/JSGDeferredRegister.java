package dev.tauri.jsg.core.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Wrapper around NeoForge's {@link DeferredRegister} whose {@link #register(String, Supplier)}
 * returns the {@link RegistryObject} compatibility shim, keeping 1.20.1-era call sites intact.
 */
public class JSGDeferredRegister<T> {
    protected final DeferredRegister<T> internal;

    public JSGDeferredRegister(DeferredRegister<T> internal) {
        this.internal = internal;
    }

    public static <T> JSGDeferredRegister<T> create(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
        return new JSGDeferredRegister<>(DeferredRegister.create(registryKey, namespace));
    }

    public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> supplier) {
        return RegistryObject.of(internal.register(name, supplier));
    }

    public Collection<DeferredHolder<T, ? extends T>> getEntries() {
        return internal.getEntries();
    }

    public DeferredRegister<T> unwrap() {
        return internal;
    }

    public void register(IEventBus bus) {
        internal.register(bus);
    }
}

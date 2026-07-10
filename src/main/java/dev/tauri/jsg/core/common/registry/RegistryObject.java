package dev.tauri.jsg.core.common.registry;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Source-compatibility shim for Forge's {@code RegistryObject} on top of NeoForge's {@link DeferredHolder},
 * so registration call sites across jsg-core and its addons keep their 1.20.1 shape.
 */
@SuppressWarnings("unchecked")
public class RegistryObject<T> implements Supplier<T> {
    private final DeferredHolder<? super T, T> holder;

    private RegistryObject(DeferredHolder<? super T, T> holder) {
        this.holder = holder;
    }

    public static <T> RegistryObject<T> of(DeferredHolder<? super T, T> holder) {
        return new RegistryObject<>(holder);
    }

    @Override
    public T get() {
        return holder.get();
    }

    public ResourceLocation getId() {
        return holder.getId();
    }

    public ResourceKey<T> getKey() {
        return (ResourceKey<T>) (ResourceKey<?>) holder.getKey();
    }

    public boolean isPresent() {
        return holder.isBound();
    }

    public void ifPresent(Consumer<? super T> consumer) {
        if (isPresent()) consumer.accept(get());
    }

    public Optional<Holder<T>> getHolder() {
        return Optional.of((Holder<T>) (Holder<?>) holder);
    }

    public DeferredHolder<? super T, T> unwrap() {
        return holder;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RegistryObject<?> other && other.holder.equals(holder);
    }

    @Override
    public int hashCode() {
        return holder.hashCode();
    }
}

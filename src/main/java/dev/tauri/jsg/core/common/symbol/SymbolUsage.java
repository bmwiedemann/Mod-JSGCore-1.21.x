package dev.tauri.jsg.core.common.symbol;

import dev.tauri.jsg.core.common.entity.NotebookPageType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public record SymbolUsage(String name, @Nullable Supplier<NotebookPageType<?>> pageTypeSupplier) {
    // for future usage
}

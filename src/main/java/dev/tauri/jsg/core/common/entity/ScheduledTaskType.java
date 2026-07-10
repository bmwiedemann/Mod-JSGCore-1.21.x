package dev.tauri.jsg.core.common.entity;

import dev.tauri.jsg.core.common.registry.JSGCoreRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ScheduledTaskType(String name, int waitTicks, boolean overtime) {
    public ScheduledTaskType(String name) {
        this(name, -1);
    }

    public ScheduledTaskType(String name, boolean overtime) {
        this(name, -1, overtime);
    }

    public ScheduledTaskType(String name, int waitTicks) {
        this(name, waitTicks, true);
    }

    @NotNull
    public ResourceLocation getKey() {
        return Optional.ofNullable(JSGCoreRegistries.R_SCHEDULED_TASK_TYPE.get().getKey(this)).orElseThrow();
    }

    @Nullable
    public static ScheduledTaskType valueOf(ResourceLocation id) {
        return JSGCoreRegistries.R_SCHEDULED_TASK_TYPE.get().get(id);
    }
}

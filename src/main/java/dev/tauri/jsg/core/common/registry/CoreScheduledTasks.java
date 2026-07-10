package dev.tauri.jsg.core.common.registry;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.entity.ScheduledTaskType;
import net.neoforged.neoforge.registries.RegistryObject;

public class CoreScheduledTasks {
    public static final RegistryObject<ScheduledTaskType> GIVE_PAGE = JSGCore.REGISTRY_HELPER.scheduledTask().register("give_page", () -> new ScheduledTaskType("give_page"));

    public static void init() {
    }
}

package dev.tauri.jsg.core.common.registry;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.config.JSGCoreConfig;
import dev.tauri.jsg.core.common.datafixer.CoreDataFixers;
import net.neoforged.bus.api.IEventBus;

public class JSGCoreRegistriesInit {
    public static void init() {
        JSGCoreConfig.load();

        CoreScheduledTasks.init();
        CoreBlocks.init();
        CoreBlockEntities.init();
        CoreItems.init();
        CoreTabs.init();
        CoreBiomeOverlays.init();
        CoreSoundEvents.init();
        CoreStateTypes.init();
        CoreStructureTypes.init();
        CoreFluids.init();

        CoreDataFixers.register();
    }

    public static void register(IEventBus eventBus) {
        JSGCoreConfig.register();

        JSGCore.REGISTRY_HELPER.register(eventBus);
    }
}

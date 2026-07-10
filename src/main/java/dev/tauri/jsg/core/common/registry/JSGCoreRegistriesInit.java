package dev.tauri.jsg.core.common.registry;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.config.JSGCoreConfig;
import net.minecraftforge.eventbus.api.IEventBus;

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
    }

    public static void register(IEventBus eventBus) {
        JSGCoreConfig.register();

        JSGCore.REGISTRY_HELPER.register(eventBus);
    }
}

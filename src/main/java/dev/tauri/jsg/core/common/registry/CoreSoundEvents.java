package dev.tauri.jsg.core.common.registry;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.sound.SoundEvent;
import dev.tauri.jsg.core.mapping.JSGMapping;

public class CoreSoundEvents {
    public static final SoundEvent PAGE_FLIP = new SoundEvent(JSGMapping.rl(JSGCore.MOD_ID, "misc.page.flip"), 1).register(JSGCore.REGISTRY_HELPER.sound());

    public static void init() {
    }
}

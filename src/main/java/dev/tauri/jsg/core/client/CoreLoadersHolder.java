package dev.tauri.jsg.core.client;

import dev.tauri.jsg.core.JSGCore;

public class CoreLoadersHolder {
    public static final LoadersHolder INSTANCE = LoadersHolder.getOrCreate(JSGCore.MOD_ID, JSGCore.class);

    public static void init() {
    }
}

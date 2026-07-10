package dev.tauri.jsg.core.common.registry;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.entity.StateType;
import net.neoforged.neoforge.registries.RegistryObject;

public class CoreStateTypes {
    public static final RegistryObject<StateType> RENDERER_STATE = JSGCore.REGISTRY_HELPER.state().register("renderer_state", () -> new StateType("renderer_state"));
    public static final RegistryObject<StateType> RENDERER_UPDATE = JSGCore.REGISTRY_HELPER.state().register("renderer_update", () -> new StateType("renderer_update"));
    public static final RegistryObject<StateType> GUI_STATE = JSGCore.REGISTRY_HELPER.state().register("gui_state", () -> new StateType("gui_state"));
    public static final RegistryObject<StateType> GUI_UPDATE = JSGCore.REGISTRY_HELPER.state().register("gui_update", () -> new StateType("gui_update"));
    public static final RegistryObject<StateType> CAMO_STATE = JSGCore.REGISTRY_HELPER.state().register("camo_state", () -> new StateType("camo_state"));
    public static final RegistryObject<StateType> BIOME_OVERRIDE_STATE = JSGCore.REGISTRY_HELPER.state().register("biome_override_state", () -> new StateType("biome_override_state"));
    public static final RegistryObject<StateType> SOUND_UPDATE = JSGCore.REGISTRY_HELPER.state().register("sound_update", () -> new StateType("sound_update"));

    public static void init() {
    }
}

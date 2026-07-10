package dev.tauri.jsg.core.common.registry;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.entity.NotebookPageType;
import dev.tauri.jsg.core.common.entity.ScheduledTaskType;
import dev.tauri.jsg.core.common.entity.StateType;
import dev.tauri.jsg.core.common.raycaster.Raycaster;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.symbol.SymbolUsage;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPointOfOriginType;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class JSGCoreRegistries {
    private static final List<DeferredRegister<?>> REGISTERS = new ArrayList<>();

    public static final ResourceKey<Registry<BiomeOverlayInstance>> BIOME_OVERLAY = ResourceKey.createRegistryKey(JSGMapping.rl(JSGCore.MOD_ID, "biome_overlay"));
    public static final ResourceKey<Registry<ScheduledTaskType>> SCHEDULED_TASK_TYPE = ResourceKey.createRegistryKey(JSGMapping.rl(JSGCore.MOD_ID, "scheduled_task_type"));
    public static final ResourceKey<Registry<NotebookPageType<?>>> NOTEBOOK_PAGE_TYPE = ResourceKey.createRegistryKey(JSGMapping.rl(JSGCore.MOD_ID, "notebook_page_type"));
    public static final ResourceKey<Registry<SymbolType<?>>> SYMBOL_TYPE = ResourceKey.createRegistryKey(JSGMapping.rl(JSGCore.MOD_ID, "symbol_type"));
    public static final ResourceKey<Registry<SymbolUsage>> SYMBOL_USAGE = ResourceKey.createRegistryKey(JSGMapping.rl(JSGCore.MOD_ID, "symbol_usage"));
    public static final ResourceKey<Registry<StateType>> STATE_TYPE = ResourceKey.createRegistryKey(JSGMapping.rl(JSGCore.MOD_ID, "state_type"));
    public static final ResourceKey<Registry<IPointOfOriginType>> POINT_OF_ORIGIN_TYPE = ResourceKey.createRegistryKey(JSGMapping.rl(JSGCore.MOD_ID, "point_of_origin_type"));
    public static final ResourceKey<Registry<Raycaster>> RAYCASTER = ResourceKey.createRegistryKey(JSGMapping.rl(JSGCore.MOD_ID, "raycaster"));


    public static final Supplier<Registry<BiomeOverlayInstance>> R_BIOME_OVERLAY = create(BIOME_OVERLAY);
    public static final Supplier<Registry<ScheduledTaskType>> R_SCHEDULED_TASK_TYPE = create(SCHEDULED_TASK_TYPE);
    public static final Supplier<Registry<NotebookPageType<?>>> R_NOTEBOOK_PAGE_TYPE = create(NOTEBOOK_PAGE_TYPE);
    public static final Supplier<Registry<SymbolType<?>>> R_SYMBOL_TYPE = create(SYMBOL_TYPE);
    public static final Supplier<Registry<SymbolUsage>> R_SYMBOL_USAGE = create(SYMBOL_USAGE);
    public static final Supplier<Registry<StateType>> R_STATE_TYPE = create(STATE_TYPE);
    public static final Supplier<Registry<IPointOfOriginType>> R_POINT_OF_ORIGIN_TYPE = create(POINT_OF_ORIGIN_TYPE);
    public static final Supplier<Registry<Raycaster>> R_RAYCASTER = create(RAYCASTER);

    private static <T> Supplier<Registry<T>> create(ResourceKey<Registry<T>> id) {
        DeferredRegister<T> dr = DeferredRegister.create(id, id.location().getNamespace());
        REGISTERS.add(dr);
        // Forge custom registries were synced to clients by default; keep that behavior on NeoForge.
        Registry<T> registry = dr.makeRegistry(builder -> builder.sync(true));
        return () -> registry;
    }

    public static void init() {

    }

    public static void register(IEventBus bus) {
        REGISTERS.forEach(dr -> dr.register(bus));
    }
}

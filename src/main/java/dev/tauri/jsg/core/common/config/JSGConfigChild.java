package dev.tauri.jsg.core.common.config;

import dev.tauri.jsg.core.common.config.values.JSGConfigValue;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class JSGConfigChild {
    public Supplier<ModConfigSpec.Builder> builder;
    @Nullable
    public ModConfigSpec builtSpec;
    public String name;
    public String modId;
    public final List<JSGConfigValue> entries = new ArrayList<>();

    public JSGConfigChild(Supplier<ModConfigSpec.Builder> builder, String name, String modId) {
        this.builder = builder;
        this.name = name;
        this.modId = modId;
    }

    public Component getTitle() {
        return Component.translatable("gui." + modId + ".config_child." + name.toLowerCase().replaceAll(" ", "_"));
    }

    public <T extends JSGConfigValue> T add(T configValue) {
        entries.add(configValue);
        return configValue;
    }
}

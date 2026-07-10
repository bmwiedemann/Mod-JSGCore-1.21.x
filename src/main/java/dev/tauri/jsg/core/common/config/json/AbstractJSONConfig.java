package dev.tauri.jsg.core.common.config.json;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.tauri.jsg.core.common.config.IJSONConfigEntry;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractJSONConfig<E extends IJSONConfigEntry> {
    public final Map<String, E> defaults;
    public final Map<String, E> configEntries = new HashMap<>();
    public final String name;
    private File file;
    protected boolean shouldWriteToFile;

    protected final Codec<E> entryCodec;
    protected final Codec<Map<String, E>> entriesCodec;

    public AbstractJSONConfig(String name, Map<String, E> defaults, Codec<E> codec) {
        this.defaults = defaults;
        this.name = name;
        this.entryCodec = codec;
        this.entriesCodec = Codec.unboundedMap(Codec.STRING, entryCodec);
    }

    @Nullable
    public E getConfigEntry(String id) {
        if (configEntries.isEmpty()) return null;
        return configEntries.get(id);
    }

    protected Codec<Map<String, E>> getEntriesCodec() {
        return entriesCodec;
    }

    public void reload(@Nullable MinecraftServer server) throws IOException {
        load(null);
        update(server);
    }

    public void load(File modConfigDir) {
        configEntries.clear();
        if (modConfigDir != null)
            file = new File(modConfigDir, "jsg/" + name + ".json");
        if (file == null) return;
        try {
            var json = JsonParser.parseReader(new FileReader(file));
            var result = getEntriesCodec().parse(JsonOps.INSTANCE, json);
            if (result.result().isPresent()) {
                configEntries.putAll(result.result().get());
            }
        } catch (FileNotFoundException ignored) {
        }
    }

    @SuppressWarnings("all")
    protected void update(@Nullable MinecraftServer server) throws IOException {
        if (configEntries.isEmpty()) {
            defaults.forEach((id, e) -> configEntries.put(id, e));
            shouldWriteToFile = true;
        }
        if (!shouldWriteToFile) return;
        if (file == null) return;
        var result = getEntriesCodec().encodeStart(JsonOps.INSTANCE, configEntries);
        if (result.result().isEmpty()) return;
        var jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(result.result().get());
        file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(file);
        writer.write(jsonString);
        writer.close();
        shouldWriteToFile = false;
    }
}

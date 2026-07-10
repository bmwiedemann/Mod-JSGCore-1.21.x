package dev.tauri.jsg.core.common.config.json.dimension;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.config.IJSONConfigEntry;
import dev.tauri.jsg.core.common.config.json.AbstractJSONConfig;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.loader.PointOfOriginsLoader;
import dev.tauri.jsg.core.common.registry.CoreBiomeOverlays;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPointOfOriginType;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import dev.tauri.jsg.core.mapping.JSGMapping;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JSGDimensionConfig extends AbstractJSONConfig<JSGDimensionConfig.Entry> {
    public static final JSGDimensionConfig INSTANCE = new JSGDimensionConfig();
    public static final String CONFIG_DIMENSIONS_VERSION = "4.0";
    public static final Map<String, Function<JsonOps, Entry>> DEFAULTS = Util.make(new HashMap<>(), (map) -> {
        map.put(Level.OVERWORLD.location().toString(), Entry.createDefaultExcept(
                Pair.of("groups", (ops) -> new Dynamic<>(ops, ops.createList(Stream.of(ops.createString("milkyway")))))
        ));
        map.put(Level.NETHER.location().toString(), Entry.createDefaultExcept(
                Pair.of("distance", (ops) -> new Dynamic<>(ops, ops.createInt(5))),
                Pair.of("groups", (ops) -> new Dynamic<>(ops, ops.createList(Stream.of(ops.createString("milkyway")))))
        ));
        map.put(Level.END.location().toString(), Entry.createDefaultExcept(
                Pair.of("distance", (ops) -> new Dynamic<>(ops, ops.createInt(10)))
        ));
    });

    public JSGDimensionConfig() {
        super("jsgDimensions_" + CONFIG_DIMENSIONS_VERSION, Map.of(), Entry.DYNAMIC_CODEC);
    }

    protected Pair<Entry, Entry> getPairedEntries(ResourceLocation fromDimId, ResourceLocation toDimId) {
        var eFrom = configEntries.get(fromDimId.toString());
        var eTo = configEntries.get(toDimId.toString());

        if (eFrom == null || eTo == null) {
            JSGCore.logger.error("Tried to get non-existing dimension. This is a bug.");
            JSGCore.logger.error("FromId: {}, ToId: {}, FromEntryNull: {}, ToEntryNull: {}", fromDimId, toDimId, eFrom == null, eTo == null);
            JSGCore.logger.error("JSG dimension entries:{}{}", System.lineSeparator(), configEntries.entrySet().stream()
                    .map(en -> en.getKey() + " | " + en.getValue().toString())
                    .collect(Collectors.joining(System.lineSeparator()))
            );
            JSGCore.logger.error("Stack trace:", new IllegalArgumentException());
            return null;
        }
        return Pair.of(eFrom, eTo);
    }

    public double getDistanceBetween(@Nullable ResourceKey<Level> fromDimId, @Nullable ResourceKey<Level> toDimId) {
        if (toDimId == null || fromDimId == null) return 0;
        var fromTo = getPairedEntries(fromDimId.location(), toDimId.location());
        if (fromTo == null) return 0;
        return Math.abs(fromTo.first().getDistance() - fromTo.second().getDistance());
    }

    public boolean isGroupEqual(ResourceKey<Level> fromDim, ResourceKey<Level> toDim) {
        return isGroupEqual(fromDim.location(), toDim.location());
    }

    public boolean isGroupEqual(ResourceLocation fromDimId, ResourceLocation toDimId) {
        var fromTo = getPairedEntries(fromDimId, toDimId);
        if (fromTo == null) return false;
        return fromTo.first().isGroupEqual(fromTo.second());
    }

    @Nullable
    public Entry getConfigEntry(ResourceKey<Level> dim) {
        if (dim == null) return null;
        if (configEntries.isEmpty()) {
            try {
                reload(null);
            } catch (Exception ignored) {
                return null;
            }
        }
        if (configEntries.isEmpty())
            return null;
        return getConfigEntry(dim.location().toString());
    }

    public Optional<Entry> getConfigEntrySafe(ResourceKey<Level> dim) {
        return Optional.ofNullable(this.getConfigEntry(dim));
    }

    @Override
    protected void update(@Nullable MinecraftServer server) throws IOException {
        if (server != null) {
            for (ResourceKey<Level> location : server.levelKeys()) {
                if (!configEntries.containsKey(location.location().toString())) {
                    shouldWriteToFile = true;
                    String name = location.location().toString();
                    if (DEFAULTS.containsKey(name))
                        configEntries.put(location.location().toString(), DEFAULTS.get(name).apply(JsonOps.INSTANCE));
                    else if (DEFAULTS.containsKey(Level.OVERWORLD.location().toString()))
                        configEntries.put(location.location().toString(), DEFAULTS.get(Level.OVERWORLD.location().toString()).apply(JsonOps.INSTANCE));
                }
            }
        }
        super.update(server);
    }

    public static class Entry implements IJSONConfigEntry {
        protected static final Map<String, Function<JsonOps, Dynamic<JsonElement>>> DEFAULT_FIELDS = Util.make(new HashMap<>(), (map) -> {
            map.put("distance", (ops) -> new Dynamic<>(ops, ops.createInt(0)));
            map.put("groups", (ops) -> new Dynamic<>(ops, ops.createList(Stream.of())));
            map.put("origins", (ops) -> new Dynamic<>(ops, ops.createMap(Stream.of())));
        });
        public static final Codec<Entry> DYNAMIC_CODEC = Codec.unboundedMap(Codec.STRING, Codec.PASSTHROUGH).xmap(Entry::new, Entry::dynamicMap);
        protected final Map<String, Dynamic<?>> dynamicMap;

        public static void registerField(String name, Function<JsonOps, Dynamic<JsonElement>> dynamicFunction) {
            DEFAULT_FIELDS.put(name, dynamicFunction);
        }

        public Entry(Map<String, Dynamic<?>> map) {
            this.dynamicMap = map;
        }

        public void put(String name, Dynamic<?> value) {
            dynamicMap.put(name, value);
        }

        public static Function<JsonOps, Entry> createDefault() {
            return create(DEFAULT_FIELDS.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue())).toList());
        }

        @SafeVarargs
        public static Function<JsonOps, Entry> createDefaultExcept(Pair<String, Function<JsonOps, Dynamic<JsonElement>>>... fields) {
            return createDefaultExcept(List.of(fields));
        }

        public static Function<JsonOps, Entry> createDefaultExcept(Collection<Pair<String, Function<JsonOps, Dynamic<JsonElement>>>> fields) {
            return create(Util.make(new HashMap<>(DEFAULT_FIELDS), map -> {
                for (var f : fields)
                    map.put(f.first(), f.second());
            }).entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue())).toList());
        }

        @SafeVarargs
        public static Function<JsonOps, Entry> create(Pair<String, Function<JsonOps, Dynamic<JsonElement>>>... fields) {
            return create(List.of(fields));
        }

        public static Function<JsonOps, Entry> create(Collection<Pair<String, Function<JsonOps, Dynamic<JsonElement>>>> fields) {
            return (ops) -> new Entry(Util.make(new HashMap<>(), (map) ->
                    fields.forEach(pair -> map.put(pair.first(), pair.second().apply(ops)))));
        }

        protected Map<String, Dynamic<?>> dynamicMap() {
            return dynamicMap;
        }

        public Optional<Dynamic<?>> getDynamic(String key) {
            return Optional.ofNullable(dynamicMap.get(key));
        }

        public Optional<Object> getOptional(String key) {
            var result = dynamicMap.get(key);
            if (result == null) return Optional.empty();
            return Optional.ofNullable(result.getValue());
        }

        public int getInt(String key, int defaultValue) {
            var result = dynamicMap.get(key);
            if (result == null) return defaultValue;
            return result.asInt(defaultValue);
        }

        public double getDouble(String key, double defaultValue) {
            var result = dynamicMap.get(key);
            if (result == null) return defaultValue;
            return result.asDouble(defaultValue);
        }

        public float getFloat(String key, float defaultValue) {
            var result = dynamicMap.get(key);
            if (result == null) return defaultValue;
            return result.asFloat(defaultValue);
        }

        public Optional<String> getString(String key) {
            var result = dynamicMap.get(key);
            if (result == null) return Optional.empty();
            return result.asString().result();
        }

        public Optional<ResourceLocation> getRL(String key) {
            var result = dynamicMap.get(key);
            if (result == null) return Optional.empty();
            return result.asString().result().map(JSGMapping::rl);
        }

        public boolean getBool(String key, boolean defaultValue) {
            var result = dynamicMap.get(key);
            if (result == null) return defaultValue;
            return result.asBoolean(defaultValue);
        }

        // ------------------------------
        // DEFAULT KEYS
        public int getDistance() {
            return getInt("distance", 0);
        }

        public List<String> getGroups() {
            return getDynamic("groups")
                    .map(dynamic -> dynamic
                            .asList(Dynamic::asString)
                            .stream()
                            .filter(d -> d.result().isPresent())
                            .map(d -> d.result().orElseThrow())
                            .toList())
                    .orElse(new ArrayList<>());
        }

        public boolean isGroupEqual(Entry other) {
            if (other == this) return true;
            var thisGroups = this.getGroups();
            if (thisGroups == null)
                return false;
            var otherGroups = other.getGroups();
            if (otherGroups == null)
                return false;
            if (thisGroups.isEmpty() || otherGroups.isEmpty())
                return false;
            for (String thisGroup : thisGroups) {
                if (otherGroups.contains(thisGroup))
                    return true;
            }
            return thisGroups.equals(otherGroups);
        }

        @Override
        public String toString() {
            return "[distance=" + getDistance() + ", groups: '" + getGroups().toString() + "']";
        }

        @Nullable
        public PointOfOrigin getOrigin(IPointOfOriginType type, @Nullable BiomeOverlayInstance overlay) {
            if (overlay == null) overlay = CoreBiomeOverlays.NORMAL.get();
            var originsOpt = getDynamic("origins").map(d -> d.asMap(
                    key -> key.asString(""),
                    value -> value.asMap(
                            k -> k.asString(""),
                            v -> v.asString("")
                    )
            ));
            if (originsOpt.isEmpty()) return null;
            var origins = originsOpt.get();
            var typeMap = origins.get(type.getPoONamespaceIdentifier().toString());
            if (typeMap == null) return null;
            if (!typeMap.containsKey(overlay.getId().toString())) return null;
            return PointOfOriginsLoader.INSTANCE.getOriginByIdOrElse(type, JSGMapping.rl(typeMap.get(overlay.getId().toString())), () -> null);
        }
    }
}

package dev.tauri.jsg.core.common.symbol.pointoforigin;

import dev.tauri.jsg.core.common.config.ingame.BEConfigOptionProvider;
import dev.tauri.jsg.core.common.config.ingame.option.ConfigOptionsHolder;
import dev.tauri.jsg.core.common.config.ingame.option.type.EnumLikeBEConfigOption;
import dev.tauri.jsg.core.common.loader.PointOfOriginsLoader;
import dev.tauri.jsg.core.common.registry.JSGCoreRegistries;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public interface IPointOfOriginType {
    default ResourceLocation getPoONamespaceIdentifier() {
        return JSGCoreRegistries.R_POINT_OF_ORIGIN_TYPE.get().getKey(this);
    }

    List<String> getPoOModelsTypes();

    List<String> getPoOTexturesTypes();

    List<ResourceLocation> getPoODefaults();

    @Nullable
    PointOfOrigin getDefaultPoO();

    static BEConfigOptionProvider<ResourceLocation> registerBEConfigOption(RegistryObject<? extends IPointOfOriginType> pooType, ConfigOptionsHolder holder) {
        return holder.register("point_of_origin", (onChanged) -> new EnumLikeBEConfigOption<>(onChanged,
                pooType.get().getPoODefaults().get(0),
                () -> PointOfOriginsLoader.INSTANCE.getRegisteredPointOfOrigins().get(pooType.get()).toArray(ResourceLocation[]::new),
                (object) -> {
                    if (object instanceof String string) {
                        try {
                            return JSGMapping.rl(string);
                        } catch (Exception ignored) {
                        }
                    }
                    if (!(object instanceof ResourceLocation rl)) return null;
                    return rl;
                },
                (value) -> {
                    if (value == null) return "null";
                    return value.toString();
                },
                (buf, value) -> new FriendlyByteBuf(buf).writeResourceLocation(Objects.requireNonNull(value)),
                (buf) -> new FriendlyByteBuf(buf).readResourceLocation()
        ));
    }

    static BEConfigOptionProvider<ResourceLocation> registerEmptyBEConfigOption(ConfigOptionsHolder holder) {
        return holder.register("point_of_origin", (onChanged) -> new EnumLikeBEConfigOption<>(onChanged,
                JSGMapping.rl("minecraft:empty"),
                () -> new ResourceLocation[0],
                (object) -> {
                    if (object instanceof String string) {
                        try {
                            return JSGMapping.rl(string);
                        } catch (Exception ignored) {
                        }
                    }
                    if (!(object instanceof ResourceLocation rl)) return null;
                    return rl;
                },
                (value) -> {
                    if (value == null) return "null";
                    return value.toString();
                },
                (buf, value) -> new FriendlyByteBuf(buf).writeResourceLocation(Objects.requireNonNull(value)),
                (buf) -> new FriendlyByteBuf(buf).readResourceLocation()
        ));
    }
}

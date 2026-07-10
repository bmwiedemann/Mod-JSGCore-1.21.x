package dev.tauri.jsg.core.common.config.ingame;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface IBEConfigOption<T> {
    @Nullable
    @OnlyIn(Dist.CLIENT)
    BEConfigOptionWidget<T> createGUIWidget(BEConfig config, int tabWidth, String optionId);

    @NotNull
    T getDefaultValue();

    @NotNull
    T getValue();

    void setValue(T value);

    default void setDefault() {
        setValue(getDefaultValue());
    }

    default boolean isChanged() {
        return false;
    }

    default void setChanged() {
    }

    @NotNull
    Optional<T> parseValue(Object value);

    default boolean parseAndSetValue(Object value) {
        var val = parseValue(value);
        if (val.isEmpty()) return false;
        setValue(val.get());
        return true;
    }

    void toBytes(ByteBuf buf);

    void fromBytes(ByteBuf buf);

    void serializeToNBT(String selfId, CompoundTag tag);

    void deserializeFromNBT(String selfId, CompoundTag tag);
}

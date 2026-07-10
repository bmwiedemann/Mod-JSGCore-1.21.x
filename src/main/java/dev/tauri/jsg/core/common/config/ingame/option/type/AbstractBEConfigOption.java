package dev.tauri.jsg.core.common.config.ingame.option.type;

import dev.tauri.jsg.core.common.config.ingame.IBEConfigOption;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class AbstractBEConfigOption<T> implements IBEConfigOption<T> {
    @NotNull
    protected final T defaultValue;
    protected T value;
    protected boolean isModifiedByUser;
    protected final Runnable onChanged;

    public AbstractBEConfigOption(Runnable onChanged, @NotNull T defaultValue) {
        this.defaultValue = defaultValue;
        this.onChanged = onChanged;
    }

    @Override
    public boolean isChanged() {
        return isModifiedByUser;
    }

    @Override
    public void setChanged() {
        isModifiedByUser = true;
        onChanged.run();
    }

    @Override
    public @NotNull T getValue() {
        return Optional.ofNullable(value).orElse(defaultValue);
    }

    @Override
    public @NotNull T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(isModifiedByUser);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        isModifiedByUser = buf.readBoolean();
    }

    @Override
    public void serializeToNBT(String selfId, CompoundTag tag) {

    }

    @Override
    public void deserializeFromNBT(String selfId, CompoundTag tag) {
        isModifiedByUser = true;
    }
}

package dev.tauri.jsg.core.common.config.ingame.option.type;

import dev.tauri.jsg.core.common.config.ingame.BEConfig;
import dev.tauri.jsg.core.common.config.ingame.BEConfigOptionWidget;
import dev.tauri.jsg.core.common.config.ingame.widget.IntegerBEConfigOptionWidget;
import dev.tauri.jsg.core.common.config.ingame.widget.RangedIntBEConfigOptionWidget;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.OptionalInt;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class IntegerBEConfigOption extends AbstractBEConfigOption<Integer> {
    protected final OptionalInt min;
    protected final OptionalInt max;

    public IntegerBEConfigOption(Runnable onChanged, int defaultValue) {
        this(onChanged, defaultValue, OptionalInt.empty(), OptionalInt.empty());
    }

    public IntegerBEConfigOption(Runnable onChanged, int defaultValue, int min) {
        this(onChanged, defaultValue, OptionalInt.of(min), OptionalInt.empty());
    }

    public IntegerBEConfigOption(Runnable onChanged, int defaultValue, int min, int max) {
        this(onChanged, defaultValue, OptionalInt.of(min), OptionalInt.of(max));
    }

    public IntegerBEConfigOption(Runnable onChanged, int defaultValue, OptionalInt min, OptionalInt max) {
        super(onChanged, defaultValue);
        this.min = min;
        this.max = max;
    }

    public OptionalInt getMin() {
        return min;
    }

    public OptionalInt getMax() {
        return max;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public @Nullable BEConfigOptionWidget<Integer> createGUIWidget(BEConfig config, int tabWidth, String optionId) {
        if (min.isPresent() && max.isPresent())
            return new RangedIntBEConfigOptionWidget(config, optionId, tabWidth, this);
        return new IntegerBEConfigOptionWidget(config, optionId, tabWidth, this);
    }

    @Override
    public @NotNull Optional<Integer> parseValue(Object value) {
        if (!(value instanceof Integer intVal))
            return Optional.empty();
        return Optional.of(intVal);
    }

    @Override
    public void setValue(Integer value) {
        if (max.isPresent() && max.getAsInt() < value) value = max.getAsInt();
        if (min.isPresent() && min.getAsInt() > value) value = min.getAsInt();
        super.setValue(value);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(getValue());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        setValue(buf.readInt());
    }

    @Override
    public void serializeToNBT(String selfId, CompoundTag tag) {
        tag.putInt(selfId, getValue());
        super.serializeToNBT(selfId, tag);
    }

    @Override
    public void deserializeFromNBT(String selfId, CompoundTag tag) {
        setValue(tag.getInt(selfId));
        super.deserializeFromNBT(selfId, tag);
    }
}

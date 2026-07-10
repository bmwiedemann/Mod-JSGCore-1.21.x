package dev.tauri.jsg.core.common.config.ingame.option.type;

import dev.tauri.jsg.core.common.config.ingame.BEConfig;
import dev.tauri.jsg.core.common.config.ingame.BEConfigOptionWidget;
import dev.tauri.jsg.core.common.config.ingame.widget.EnumBEConfigOptionWidget;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class EnumLikeBEConfigOption<T> extends AbstractBEConfigOption<T> {
    protected final Supplier<T[]> valuesSupplier;
    protected final Function<Object, T> parser;
    protected final Function<T, String> toStringFunction;

    protected final BiConsumer<ByteBuf, @NotNull T> toBytesConsumer;
    protected final Function<ByteBuf, @NotNull T> fromBytesConsumer;

    @ParametersAreNonnullByDefault
    public EnumLikeBEConfigOption(Runnable onChanged, T defaultValue, Supplier<T[]> valuesSupplier, Function<Object, T> parser, Function<@NotNull T, String> toStringFunction, BiConsumer<ByteBuf, @NotNull T> toBytesConsumer, Function<ByteBuf, @NotNull T> fromBytesConsumer) {
        super(onChanged, defaultValue);
        this.valuesSupplier = valuesSupplier;
        this.parser = parser;
        this.toStringFunction = toStringFunction;
        this.toBytesConsumer = toBytesConsumer;
        this.fromBytesConsumer = fromBytesConsumer;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public @Nullable BEConfigOptionWidget<T> createGUIWidget(BEConfig config, int tabWidth, String optionId) {
        return new EnumBEConfigOptionWidget<>(config, optionId, tabWidth, this);
    }

    @Override
    public @NotNull Optional<T> parseValue(Object value) {
        return Optional.ofNullable(parser.apply(value));
    }

    public T[] values() {
        return valuesSupplier.get();
    }

    public String name(@NotNull T value) {
        return toStringFunction.apply(value);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        toBytesConsumer.accept(buf, getValue());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        setValue(fromBytesConsumer.apply(buf));
    }

    @Override
    public void serializeToNBT(String selfId, CompoundTag tag) {
        var buf = Unpooled.buffer();
        toBytes(buf);
        byte[] dst = new byte[buf.readableBytes()];
        buf.readBytes(dst);
        tag.putByteArray(selfId, dst);
        super.serializeToNBT(selfId, tag);
    }

    @Override
    public void deserializeFromNBT(String selfId, CompoundTag tag) {
        byte[] dst = tag.getByteArray(selfId);
        if (dst.length > 0) {
            var buf = Unpooled.copiedBuffer(dst);
            fromBytes(buf);
        }
        super.deserializeFromNBT(selfId, tag);
    }
}

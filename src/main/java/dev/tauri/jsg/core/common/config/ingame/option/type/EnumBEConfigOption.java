package dev.tauri.jsg.core.common.config.ingame.option.type;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;
import java.util.function.Supplier;

public class EnumBEConfigOption<T extends Enum<T>> extends dev.tauri.jsg.core.common.config.ingame.option.type.EnumLikeBEConfigOption<T> {
    @ParametersAreNonnullByDefault
    public EnumBEConfigOption(Runnable onChanged, T defaultValue, Supplier<T[]> valuesSupplier, Function<Object, T> parser) {
        super(onChanged, defaultValue, valuesSupplier, parser, Enum::name, (buf, val) -> buf.writeInt(val.ordinal()), (buf) -> valuesSupplier.get()[buf.readInt()]);
    }
}

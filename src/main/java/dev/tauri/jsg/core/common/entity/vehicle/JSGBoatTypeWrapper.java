package dev.tauri.jsg.core.common.entity.vehicle;

import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;
import java.util.function.Supplier;

public class JSGBoatTypeWrapper<T extends Enum<T> & JSGBoatTypeWrapper.Type> {
    @SuppressWarnings("deprecation")
    public final StringRepresentable.EnumCodec<T> codec;
    public final IntFunction<T> byId;

    public final T defaultType;
    public final T[] values;

    public JSGBoatTypeWrapper(T defaultType, Supplier<T[]> values) {
        this.defaultType = defaultType;
        this.values = values.get();
        this.codec = StringRepresentable.fromEnum(values);
        this.byId = ByIdMap.continuous(Type::ordinal, values.get(), ByIdMap.OutOfBoundsStrategy.ZERO);
    }

    public interface Type extends StringRepresentable {
        int ordinal();

        @NotNull String getSerializedName();

        String getName();

        Item getDrop(boolean withChest);
    }
}

package dev.tauri.jsg.core.common.config.ingame;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.config.ingame.option.ConfigOptionsHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Optional;

public class BEConfig implements INBTSerializable<CompoundTag> {
    protected final ConfigOptionsHolder optionsHolder;
    protected final LinkedHashMap<String, IBEConfigOption<?>> options = new LinkedHashMap<>();
    protected final Runnable onChanged;

    public BEConfig() {
        this(() -> {
        }, ConfigOptionsHolder.EMPTY);
    }

    public BEConfig(Runnable onChanged, @NotNull ConfigOptionsHolder optionsHolder) {
        this.optionsHolder = optionsHolder;
        this.onChanged = onChanged;
        optionsHolder.init(this.onChanged, options);
    }

    public LinkedHashMap<String, IBEConfigOption<?>> getOptions() {
        return options;
    }

    public ConfigOptionsHolder getOptionsHolder() {
        return optionsHolder;
    }

    public <T> boolean wasValueChanged(@NotNull dev.tauri.jsg.core.common.config.ingame.BEConfigOptionProvider<T> provider) {
        return getOption(provider).map(IBEConfigOption::isChanged).orElse(false);
    }

    public <T> Optional<T> getValue(@NotNull BEConfigOptionProvider<T> provider) {
        return getOption(provider).flatMap(option -> getOptionsHolder().parseOptionValue(provider, option.getValue()));
    }

    public <T> T getValueOrDefault(@NotNull BEConfigOptionProvider<T> provider) {
        return getValue(provider).orElse(provider.getStaticInstance().getDefaultValue());
    }

    public Optional<IBEConfigOption<?>> getOption(@NotNull BEConfigOptionProvider<?> provider) {
        return getOption(provider.getId());
    }

    public Optional<IBEConfigOption<?>> getOption(String id) {
        return Optional.ofNullable(options.get(id));
    }

    public void toBytes(FriendlyByteBuf buf) {
        // we cannot simply write to bytes here, we need to write every option's ID too,
        // because the order of the options on the client can possibly not be same as on the server
        buf.writeInt(options.size());
        options.forEach((id, option) -> {
            buf.writeUtf(id);
            option.toBytes(buf);
        });
    }

    public void fromBytes(FriendlyByteBuf buf) {
        // we cannot simply read from bytes here, we need to get every option by its ID,
        // because the order of the options on the client can possibly not be same as on the server
        var size = buf.readInt();
        for (int i = 0; i < size; i++) {
            String id = buf.readUtf();
            Optional.ofNullable(options.get(id)).ifPresentOrElse((option) -> option.fromBytes(buf), () -> {
                JSGCore.logger.error("Invalid option id '{}'", id);
                throw new IllegalArgumentException("Unknown option: " + id);
            });
        }
    }

    @Override
    public final CompoundTag serializeNBT() {
        return serializeNBT(new CompoundTag());
    }

    public CompoundTag serializeNBT(@NotNull CompoundTag compound) {
        options.forEach((id, option) -> {
            if (!option.isChanged()) return;
            option.serializeToNBT(id, compound);
        });
        return compound;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundTag compound) {
        var ids = compound.getAllKeys();
        ids.forEach(id -> Optional.ofNullable(options.get(id)).ifPresent((option) -> option.deserializeFromNBT(id, compound)));
    }
}

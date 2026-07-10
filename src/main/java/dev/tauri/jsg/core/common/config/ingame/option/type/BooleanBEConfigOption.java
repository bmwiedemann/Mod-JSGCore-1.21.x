package dev.tauri.jsg.core.common.config.ingame.option.type;

import dev.tauri.jsg.core.common.config.ingame.BEConfig;
import dev.tauri.jsg.core.common.config.ingame.BEConfigOptionWidget;
import dev.tauri.jsg.core.common.config.ingame.widget.BooleanBEConfigOptionWidget;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BooleanBEConfigOption extends AbstractBEConfigOption<Boolean> {
    public BooleanBEConfigOption(Runnable onChanged, boolean defaultValue) {
        super(onChanged, defaultValue);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public @Nullable BEConfigOptionWidget<Boolean> createGUIWidget(BEConfig config, int tabWidth, String optionId) {
        return new BooleanBEConfigOptionWidget(config, optionId, tabWidth, this);
    }

    @Override
    public @NotNull Optional<Boolean> parseValue(Object value) {
        if (!(value instanceof Boolean bool)) return Optional.empty();
        return Optional.of(bool);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeBoolean(getValue());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        setValue(buf.readBoolean());
    }

    @Override
    public void serializeToNBT(String selfId, CompoundTag tag) {
        tag.putBoolean(selfId, getValue());
        super.serializeToNBT(selfId, tag);
    }

    @Override
    public void deserializeFromNBT(String selfId, CompoundTag tag) {
        setValue(tag.getBoolean(selfId));
        super.deserializeFromNBT(selfId, tag);
    }
}

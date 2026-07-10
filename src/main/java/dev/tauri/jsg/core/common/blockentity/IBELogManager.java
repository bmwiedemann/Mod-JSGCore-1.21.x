package dev.tauri.jsg.core.common.blockentity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.INBTSerializable;
import org.slf4j.event.Level;

import java.util.LinkedList;

public interface IBELogManager extends INBTSerializable<CompoundTag> {
    interface ILogLine extends INBTSerializable<CompoundTag> {
        Component component();

        Level level();

        default int color() {
            return switch (level()) {
                case ERROR -> 0xffE56A6A;
                case WARN -> 0xffE5B46A;
                case INFO -> 0xffFFFFFF;
                case DEBUG -> 0xffAFAFAF;
                case TRACE -> 0xff686868;
            };
        }

        long time();

        void toBytes(FriendlyByteBuf buffer);

        void fromBytes(FriendlyByteBuf buffer);
    }

    default void debug(Component component) {
        log(Level.DEBUG, component);
    }

    default void info(Component component) {
        log(Level.INFO, component);
    }

    default void warn(Component component) {
        log(Level.WARN, component);
    }

    default void error(Component component) {
        log(Level.ERROR, component);
    }

    void log(Level level, Component component);

    LinkedList<ILogLine> getLogs();

    void clearLogs();

    void toBytes(FriendlyByteBuf buffer);

    void fromBytes(FriendlyByteBuf buffer);
}

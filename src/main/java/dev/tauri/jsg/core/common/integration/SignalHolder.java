package dev.tauri.jsg.core.common.integration;

import net.minecraft.FieldsAreNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Objects;


@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
public class SignalHolder {
    protected final String eventName;
    protected final Object[] args;

    public static SignalHolder of(String eventName, Object... args) {
        return new SignalHolder(eventName, args);
    }

    private SignalHolder(String eventName, Object[] args) {
        this.eventName = eventName;
        this.args = args;
    }

    public void sendVia(dev.tauri.jsg.core.common.integration.ComputerDeviceProvider provider) {
        provider.sendSignal(this);
    }

    public String eventName() {
        return eventName;
    }

    public Object[] args() {
        return args;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other instanceof SignalHolder otherHolder) {
            return Objects.equals(eventName, otherHolder.eventName()) && Arrays.equals(args, otherHolder.args());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return eventName.hashCode() * 19 + Arrays.hashCode(args);
    }

    /**
     * Returns a string representation of this pair in the form &lt;<var>l</var>,<var>r</var>&gt;.
     *
     * @return a string representation of this pair in the form &lt;<var>l</var>,<var>r</var>&gt;.
     */
    @Override
    public String toString() {
        return "<" + eventName() + "," + Arrays.toString(args()) + ">";
    }
}

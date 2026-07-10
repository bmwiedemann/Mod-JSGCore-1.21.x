package dev.tauri.jsg.core.client.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

public class BiCallbackEditBox extends CallbackEditBox {
    protected Consumer<String> onChanged;
    protected String oldValue;
    protected String oldValueKeyUp;

    public BiCallbackEditBox(Font pFont, int pX, int pY, int pWidth, int pHeight, Component pMessage, Consumer<String> onKeyDown, Consumer<String> onChanged) {
        super(pFont, pX, pY, pWidth, pHeight, pMessage, (var) -> {
        });
        setResponder((val) -> {
            if (!Objects.equals(oldValueKeyUp, val) && oldValueKeyUp != null) {
                onKeyDown.accept(val);
                oldValueKeyUp = val;
            }
        });
        this.onChanged = onChanged;
    }

    @Override
    public void setValue(@NotNull String newValue) {
        super.setValue(newValue);
        if (oldValue == null)
            oldValue = newValue;
        if (oldValueKeyUp == null)
            oldValueKeyUp = newValue;
    }

    @Override
    public void setFocused(boolean pFocused) {
        var wasFocused = isFocused();
        super.setFocused(pFocused);
        if (wasFocused && !pFocused && onChanged != null && !Objects.equals(oldValue, getValue())) {
            onChanged.accept(getValue());
            oldValue = getValue();
        }
    }
}

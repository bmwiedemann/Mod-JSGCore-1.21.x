package dev.tauri.jsg.core.client.screen.widget;

import dev.tauri.jsg.core.client.screen.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class CallbackModeButton extends ModeButton {
    protected Consumer<Integer> onChanged;

    public CallbackModeButton(int x, int y, int size, ResourceLocation texture, int textureWidth, int textureHeight, int states, Consumer<Integer> onChanged) {
        this(x, y, size, size, texture, textureWidth, textureHeight, states, onChanged);
    }

    public CallbackModeButton(int x, int y, int width, int height, ResourceLocation texture, int textureWidth, int textureHeight, int states, Consumer<Integer> onChanged) {
        super(-1, x, y, width, height, texture, textureWidth, textureHeight, states);
        this.onChanged = onChanged;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (GuiHelper.isPointInRegion(this.getX(), this.getY(),
                this.width, this.height, mouseX, mouseY)) {
            switch (mouseButton) {
                case 0:
                    if (onChanged != null) {
                        onChanged.accept(getCurrentState());
                    }
                    break;
                case 1:
                    if (Screen.hasShiftDown())
                        this.previousState();
                    else
                        this.nextState();
                    break;
                case 2:
                    this.setCurrentState(0);
                    break;

            }
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }
        return false;
    }
}

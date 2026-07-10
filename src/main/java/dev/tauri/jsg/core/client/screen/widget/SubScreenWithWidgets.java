package dev.tauri.jsg.core.client.screen.widget;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SubScreenWithWidgets extends SubScreen {
    protected final List<AbstractWidget> widgets;
    protected final Map<AbstractWidget, Vector2i> initialPositions = new HashMap<>();

    public SubScreenWithWidgets(int pX, int pY, int pWidth, int pHeight, List<AbstractWidget> widgets) {
        super(pX, pY, pWidth, pHeight, Component.empty(), false);
        this.widgets = widgets;
        this.widgets.forEach(widget -> initialPositions.put(widget, new Vector2i(widget.getX(), widget.getY())));
    }


    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return widgets;
    }

    @Override
    public void tick() {
        super.tick();
        widgets.forEach(widget -> {
            if (widget instanceof EditBox box) box.tick();
            else if (widget instanceof SubScreen screen) screen.tick();
        });
    }

    @Override
    public void setX(int pX) {
        super.setX(pX);
        this.widgets.forEach(widget -> widget.setX(pX + initialPositions.get(widget).x));
    }

    @Override
    public void setY(int pY) {
        super.setY(pY);
        this.widgets.forEach(widget -> widget.setY(pY + initialPositions.get(widget).y));
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> pConsumer) {
        super.visitWidgets(pConsumer);
        widgets.forEach(w -> w.visitWidgets(pConsumer));
    }
}

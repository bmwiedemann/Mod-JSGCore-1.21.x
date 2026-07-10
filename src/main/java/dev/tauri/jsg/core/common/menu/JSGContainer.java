package dev.tauri.jsg.core.common.menu;

import com.google.common.collect.Lists;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class JSGContainer extends AbstractContainerMenu {
    public final List<ContainerListener> listeners = Lists.newArrayList();

    protected JSGContainer(@Nullable MenuType<?> menuType, int containerID) {
        super(menuType, containerID);
    }

    @Override
    public void addSlotListener(@NotNull ContainerListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
        super.addSlotListener(listener);
    }

    @Override
    public void removeSlotListener(@NotNull ContainerListener listener) {
        this.listeners.remove(listener);
        super.removeSlotListener(listener);
    }
}

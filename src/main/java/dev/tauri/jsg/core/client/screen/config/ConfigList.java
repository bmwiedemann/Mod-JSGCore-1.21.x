package dev.tauri.jsg.core.client.screen.config;

import dev.tauri.jsg.core.client.screen.config.entry.AbstractConfigEntry;
import dev.tauri.jsg.core.client.screen.config.entry.IDescriptionEntry;
import dev.tauri.jsg.core.client.screen.config.entry.SectionSeparatorConfigEntry;
import dev.tauri.jsg.core.common.config.JSGConfigChild;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ConfigList extends ObjectSelectionList<AbstractConfigEntry> {
    public ConfigList(Minecraft minecraft, int screenWidth, int screenHeight, int yStart, int yEnd, int itemHeight) {
        super(minecraft, screenWidth, screenHeight, yStart, yEnd, itemHeight);
    }

    public ConfigList(JSGConfigChild jsgConfigChild, Minecraft minecraft, int screenWidth, int screenHeight, int yStart, int yEnd, int itemHeight) {
        this(minecraft, screenWidth, screenHeight, yStart, yEnd, itemHeight);
        loadSection(jsgConfigChild);
    }

    public void add(AbstractConfigEntry entry) {
        this.addEntry(entry);
    }

    public int getRowWidth() {
        return 265;
    }

    @Override
    protected int getScrollbarPosition() {
        return ((this.width + super.getRowWidth()) / 2) + 35;
    }

    @Override
    @Nullable
    protected AbstractConfigEntry nextEntry(@NotNull ScreenDirection dir) {
        return this.nextEntry(dir, (e) -> !(e instanceof IDescriptionEntry));
    }

    protected final Map<String, LinkedList<AbstractConfigEntry>> entriesSorted = new HashMap<>();

    public void loadSection(JSGConfigChild configChild) {
        entriesSorted.clear();
        entriesSorted.put("", new LinkedList<>());
        for (var e : configChild.entries) {
            var r = e.getGuiEntry(this.width);
            if (r == null) continue;
            var splitPath = e.getPath().split("\\.");
            if (splitPath.length > 1) {
                var category = e.getPath().replace("." + splitPath[splitPath.length - 1], "");
                var l = entriesSorted.getOrDefault(category, new LinkedList<>());
                l.add(r);
                entriesSorted.put(category, l);
            } else {
                var l = entriesSorted.getOrDefault("", new LinkedList<>());
                l.add(r);
                entriesSorted.put("", l);
            }
        }
        for (var e : entriesSorted.entrySet()) {
            if (!e.getKey().equalsIgnoreCase(""))
                add(new SectionSeparatorConfigEntry(Component.literal(e.getKey())));
            for (var entry : e.getValue()) {
                add(entry);
            }
        }
    }

    public void tick() {
        for (var e : children())
            e.tick();
    }
}
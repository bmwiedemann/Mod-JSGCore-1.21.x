package dev.tauri.jsg.core.client.screen.tab.tabs;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Arrays;

public class TabInfo extends Tab {

    protected final ArrayList<InfoString> strings;

    protected TabInfo(TabInfoBuilder builder) {
        super(builder);
        this.strings = builder.strings;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!isVisible()) return;
        super.render(graphics, mouseX, mouseY);
        for (InfoString s : strings) {
            graphics.drawString(Minecraft.getInstance().font, s.s, guiLeft+currentOffsetX+s.x, guiTop+defaultY+s.y, 4210752, false);
        }
    }

    public void addString(InfoString string) {
        strings.add(string);
    }

    public void clearStrings() {
        strings.clear();
    }

    public static class InfoString {
        public final int x;
        public final int y;
        public final String s;

        public InfoString(String string, int x, int y) {
            this.x = x;
            this.y = y;
            this.s = string;
        }
    }

    // ------------------------------------------------------------------------------------------------
    // Builder

    public static TabInfoBuilder builder() {
        return new TabInfoBuilder();
    }

    public static class TabInfoBuilder extends TabBuilder {

        protected ArrayList<InfoString> strings = new ArrayList<>();

        public TabInfoBuilder addString(InfoString string) {
            strings.add(string);
            return this;
        }

        public TabInfoBuilder addStrings(InfoString... strings) {
            this.strings.addAll(Arrays.asList(strings));
            return this;
        }

        public TabInfoBuilder clearStrings() {
            strings.clear();
            return this;
        }

        @Override
        public TabInfo build() {
            return new TabInfo(this);
        }
    }
}

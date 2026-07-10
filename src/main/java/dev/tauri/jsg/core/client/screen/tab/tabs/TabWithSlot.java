package dev.tauri.jsg.core.client.screen.tab.tabs;

import dev.tauri.jsg.core.client.screen.tab.OpenTabHolderInterface;
import dev.tauri.jsg.core.common.forgeutil.SlotHandler;
import org.jetbrains.annotations.Nullable;

public class TabWithSlot extends Tab {
    protected SlotTab slot;
    protected OpenTabHolderInterface menu;

    protected TabWithSlot(TabBuilder builder) {
        super(builder);
    }

    public SlotTab createAndSaveSlot(SlotHandler slot) {
        this.slot = new SlotTab(slot, (slotTab) -> {
            int x = currentOffsetX + 106;
            int y = defaultY + 87;
            return slotTab.setXY(x, y);
        });
        return this.slot;
    }

    public @Nullable Tab.SlotTab getSavedSlot() {
        return this.slot;
    }

    public TabWithSlot setMenu(OpenTabHolderInterface menu) {
        this.menu = menu;
        return this;
    }

    @Override
    public void openTab() {
        super.openTab();
        if (menu != null)
            menu.modifyOpenTabSlotId(slot.index, true);
    }

    @Override
    public void closeTab() {
        super.closeTab();
        if (menu != null)
            menu.modifyOpenTabSlotId(slot.index, false);
    }

    @Override
    public void hideTab() {
        super.hideTab();
        if (menu != null)
            menu.modifyOpenTabSlotId(slot.index, false);
    }
}

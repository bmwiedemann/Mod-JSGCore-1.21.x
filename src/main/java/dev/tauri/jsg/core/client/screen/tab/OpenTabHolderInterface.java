package dev.tauri.jsg.core.client.screen.tab;

import dev.tauri.jsg.core.common.packet.JSGCorePacketHandler;
import dev.tauri.jsg.core.common.packet.packets.SetOpenTabSlotIdToServer;

import java.util.List;

public interface OpenTabHolderInterface {
    List<Integer> getOpenTabsSlotsIds();

    void modifyOpenTabSlotId(int slotId, boolean add);


    default void updateTabSlots() {
        JSGCorePacketHandler.sendToServer(new SetOpenTabSlotIdToServer(this.getOpenTabsSlotsIds()));
    }
}

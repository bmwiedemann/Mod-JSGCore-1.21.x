package dev.tauri.jsg.core.common.packet;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.packet.packets.*;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class JSGCorePacketHandler {

    private static final SimplePacketHandler HANDLER = new SimplePacketHandler(JSGMapping.rl(JSGCore.MOD_ID, "main"), "1.0");

    public static void sendToServer(Object packet) {
        HANDLER.sendToServer(packet);
    }

    public static void sendToClient(Object packet, PacketDistributor.TargetPoint point) {
        HANDLER.sendToClient(packet, point);
    }

    public static void sendTo(Object packet, ServerPlayer player) {
        HANDLER.sendTo(packet, player);
    }

    public static void init() {
        // to server
        HANDLER.registerPacketToServer(StateUpdateRequestToServer.class);
        HANDLER.registerPacketToServer(ClientLinkRequestStateToServer.class);
        HANDLER.registerPacketToServer(NotebookActionPacketToServer.class);
        HANDLER.registerPacketToServer(SaveConfigToServer.class);
        HANDLER.registerPacketToServer(SetOpenTabSlotIdToServer.class);

        // to client
        HANDLER.registerPacketToClient(StateUpdatePacketToClient.class);
        HANDLER.registerPacketToClient(SoundPositionedPlayToClient.class);
        HANDLER.registerPacketToClient(LinkChangedStateToClient.class);
    }
}

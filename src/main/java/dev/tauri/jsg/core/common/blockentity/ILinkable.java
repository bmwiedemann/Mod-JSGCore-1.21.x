package dev.tauri.jsg.core.common.blockentity;

import dev.tauri.jsg.core.common.packet.TargetPoint;
import dev.tauri.jsg.core.common.packet.JSGCorePacketHandler;
import dev.tauri.jsg.core.common.packet.packets.ClientLinkRequestStateToServer;
import dev.tauri.jsg.core.common.packet.packets.LinkChangedStateToClient;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public interface ILinkable<T> extends dev.tauri.jsg.core.common.blockentity.ILinkableBE<T> {
    @Override
    default void sendLinkedDeviceToClients(BlockPos sourcePos, TargetPoint targetPoint) {
        JSGCorePacketHandler.sendToClient(new LinkChangedStateToClient(sourcePos, getLinkedPos()), targetPoint);
    }

    @Override
    default void sendLinkedDeviceToClient(BlockPos sourcePos, ServerPlayer player) {
        JSGCorePacketHandler.sendTo(new LinkChangedStateToClient(sourcePos, getLinkedPos()), player);
    }

    @Override
    default void requestLinkedDeviceFromServer(BlockPos sourcePos) {
        JSGCorePacketHandler.sendToServer(new ClientLinkRequestStateToServer(sourcePos));
    }
}

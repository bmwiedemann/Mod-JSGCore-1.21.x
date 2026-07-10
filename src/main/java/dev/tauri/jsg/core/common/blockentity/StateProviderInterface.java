package dev.tauri.jsg.core.common.blockentity;

import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.entity.StateType;
import dev.tauri.jsg.core.common.packet.JSGCorePacketHandler;
import dev.tauri.jsg.core.common.packet.packets.StateUpdatePacketToClient;
import dev.tauri.jsg.core.common.packet.packets.StateUpdateRequestToServer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Implemented by {@link BlockEntity} which provides at least one {@link State}
 *
 */
public interface StateProviderInterface extends IStateProvider {
    @Override
    default void requestState(StateType type) {
        JSGCorePacketHandler.sendToServer(new StateUpdateRequestToServer(getStateHandlerBlockPos(), type));
    }

    @Override
    default void sendState(StateType type, State state) {
        JSGCorePacketHandler.sendToClient(new StateUpdatePacketToClient(getStateHandlerBlockPos(), type, state), getTargetPoint());
    }

    PacketDistributor.TargetPoint getTargetPoint();

    BlockPos getStateHandlerBlockPos();
}

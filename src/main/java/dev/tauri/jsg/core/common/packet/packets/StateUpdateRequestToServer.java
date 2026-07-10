package dev.tauri.jsg.core.common.packet.packets;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.blockentity.IStateProvider;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.entity.StateType;
import dev.tauri.jsg.core.common.packet.JSGCorePacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import dev.tauri.jsg.core.common.packet.NetworkDirection;
import dev.tauri.jsg.core.common.packet.PacketContext;
import org.apache.commons.lang3.NotImplementedException;

public class StateUpdateRequestToServer extends PositionedPacket {
    StateType stateType;

    public StateUpdateRequestToServer(BlockPos pos, StateType stateType) {
        super(pos);
        this.stateType = stateType;
    }

    public StateUpdateRequestToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);

        buf.writeResourceLocation(stateType.getId());
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        super.fromBytes(buf);
        stateType = StateType.byId(buf.readResourceLocation());
    }

    @Override
    public void handle(PacketContext ctx) {
        if (ctx.getDirection() != NetworkDirection.PLAY_TO_SERVER) return;
        ctx.setPacketHandled(true);
        ServerPlayer player = ctx.getSender();
        if (player != null) {
            ServerLevel level = player.serverLevel();
            ctx.enqueueWork(() -> {
                var te = (IStateProvider) level.getBlockEntity(pos);

                if (te != null) {
                    try {
                        State state = te.getState(stateType);

                        if (state != null)
                            JSGCorePacketHandler.sendTo(new StateUpdatePacketToClient(pos, stateType, state), player);
                        else
                            throw new NotImplementedException("State not implemented on " + te.getClass().getCanonicalName() + " : " + stateType.toString());
                    } catch (Exception e) {
                        JSGCore.logger.error("Error while handling packet!", e);
                    }
                }
            });
        }
    }
}

package dev.tauri.jsg.core.common.packet.packets;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.blockentity.IStateProvider;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.entity.StateType;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import dev.tauri.jsg.core.common.packet.NetworkDirection;
import dev.tauri.jsg.core.common.packet.PacketContext;
import org.apache.commons.lang3.NotImplementedException;

import java.util.function.Supplier;

public class StateUpdatePacketToClient extends PositionedPacket {
    private StateType stateType;
    private State state;

    private ByteBuf stateBuf;

    public StateUpdatePacketToClient(BlockPos pos, Supplier<StateType> stateType, State state) {
        this(pos, stateType.get(), state);
    }

    public StateUpdatePacketToClient(BlockPos pos, StateType stateType, State state) {
        super(pos);

        this.stateType = stateType;
        if (state == null) {
            throw new NullPointerException("State was null! (State type: " + stateType.toString() + "; Pos: " + pos.toString() + ")");
        }

        this.state = state;
    }

    public StateUpdatePacketToClient(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeResourceLocation(stateType.getId());
        state.toBytes(buf);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        super.fromBytes(buf);
        stateType = StateType.byId(buf.readResourceLocation());
        stateBuf = buf.copy();
    }

    @Override
    public void handle(PacketContext ctx) {
        if (ctx.getDirection() != NetworkDirection.PLAY_TO_CLIENT) return;
        ctx.setPacketHandled(true);
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        ClientLevel level = player.clientLevel;
        ctx.enqueueWork(() -> {
            IStateProvider te = (IStateProvider) level.getBlockEntity(pos);
            try {
                if (te == null)
                    return;

                State state = te.createState(stateType);

                if (state != null) {
                    state.fromBytes(stateBuf);

                    te.setState(stateType, state);
                } else {
                    throw new NotImplementedException("State not implemented on " + te.getClass().getCanonicalName());
                }
            } catch (Exception e) {
                JSGCore.logger.error("Error while handling packet!", e);
            }
        });
    }
}

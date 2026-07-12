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
    private ByteBuf encodedState;

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
        // NeoForge encodes payloads lazily on the Netty thread; states reference live BE
        // collections, so serialize now on the sender's thread (1.20.1 SimpleChannel semantics).
        this.encodedState = io.netty.buffer.Unpooled.buffer();
        state.toBytes(encodedState);
    }

    public StateUpdatePacketToClient(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeResourceLocation(stateType.getId());
        // Non-consuming copy: the packet is encoded twice (size-measuring pass + real encode).
        buf.writeBytes(encodedState, encodedState.readerIndex(), encodedState.readableBytes());
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        super.fromBytes(buf);
        stateType = StateType.byId(buf.readResourceLocation());
        // State decoding is deferred to handle(). Copy the payload and consume the original:
        // the 1.21 payload codec treats unread bytes as a decode error and kicks the player.
        var copy = buf.copy();
        if (buf instanceof net.minecraft.network.RegistryFriendlyByteBuf registryBuf) {
            stateBuf = new net.minecraft.network.RegistryFriendlyByteBuf(copy, registryBuf.registryAccess());
        } else {
            stateBuf = copy;
        }
        buf.skipBytes(buf.readableBytes());
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
                    JSGCore.logger.debug("applied state {} at {}", stateType.getId(), pos);
                } else {
                    throw new NotImplementedException("State not implemented on " + te.getClass().getCanonicalName());
                }
            } catch (Exception e) {
                JSGCore.logger.error("Error while handling packet!", e);
            }
        });
    }
}

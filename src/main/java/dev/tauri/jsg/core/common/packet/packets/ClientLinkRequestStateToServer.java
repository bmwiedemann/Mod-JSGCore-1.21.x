package dev.tauri.jsg.core.common.packet.packets;

import dev.tauri.jsg.core.common.blockentity.ILinkable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ClientLinkRequestStateToServer extends PositionedPacket {

    public ClientLinkRequestStateToServer(BlockPos source) {
        super(source);
    }

    public ClientLinkRequestStateToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> {
            var p = ctx.getSender();
            if (p == null) return;
            var level = p.level();
            var be = level.getBlockEntity(pos);
            if (be instanceof ILinkable<?> linkable) {
                linkable.sendLinkedDeviceToClient(pos, p);
            }
        });
    }
}

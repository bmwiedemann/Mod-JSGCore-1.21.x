package dev.tauri.jsg.core.common.packet.packets;

import dev.tauri.jsg.core.common.blockentity.ILinkable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import dev.tauri.jsg.core.common.packet.PacketContext;

public class LinkChangedStateToClient extends PositionedPacket {
    BlockPos linkedPos;

    public LinkChangedStateToClient(BlockPos source, BlockPos linkedPos) {
        super(source);
        this.linkedPos = linkedPos;
    }

    public LinkChangedStateToClient(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        if (linkedPos != null) {
            buf.writeBoolean(true);
            buf.writeBlockPos(linkedPos);
        } else
            buf.writeBoolean(false);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        super.fromBytes(buf);
        if (buf.readBoolean())
            linkedPos = buf.readBlockPos();
    }

    @Override
    public void handle(PacketContext ctx) {
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> {
            var p = Minecraft.getInstance().player;
            if (p == null) return;
            var level = p.level();
            var be = level.getBlockEntity(pos);
            if (be instanceof ILinkable<?> linkable) {
                linkable.setLinkedDevice(linkedPos);
            }
        });
    }
}

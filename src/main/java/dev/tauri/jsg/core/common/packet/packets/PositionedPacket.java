package dev.tauri.jsg.core.common.packet.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public abstract class PositionedPacket extends JSGPacket {
    public PositionedPacket() {
    }

    protected BlockPos pos;

    public PositionedPacket(BlockPos pos) {
        this.pos = pos;
    }

    public PositionedPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
    }
}

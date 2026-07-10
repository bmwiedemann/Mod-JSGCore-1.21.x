package dev.tauri.jsg.core.common.packet.packets;

import dev.tauri.jsg.core.common.packet.PacketContext;
import dev.tauri.jsg.core.common.packet.SimplePacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public abstract class JSGPacket implements CustomPacketPayload {
    public JSGPacket() {
    }

    public abstract void toBytes(FriendlyByteBuf buf);

    public abstract void fromBytes(FriendlyByteBuf buf);

    public abstract void handle(PacketContext ctx);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return SimplePacketHandler.typeOf(getClass());
    }

    public JSGPacket(FriendlyByteBuf buf) {
        fromBytes(buf);
    }
}

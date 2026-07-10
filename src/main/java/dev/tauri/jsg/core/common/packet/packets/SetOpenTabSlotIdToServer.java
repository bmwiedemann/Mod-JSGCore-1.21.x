package dev.tauri.jsg.core.common.packet.packets;

import dev.tauri.jsg.core.client.screen.tab.OpenTabHolderInterface;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.NetworkDirection;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;

public class SetOpenTabSlotIdToServer extends JSGPacket {
    public SetOpenTabSlotIdToServer() {
    }

    public List<Integer> openTabSlotIds;

    public SetOpenTabSlotIdToServer(List<Integer> openTabSlotIds) {
        this.openTabSlotIds = new ArrayList<>(openTabSlotIds);
    }

    public SetOpenTabSlotIdToServer(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(openTabSlotIds.size());
        for (var slot : openTabSlotIds)
            buf.writeInt(slot);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        var size = buf.readInt();
        openTabSlotIds = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            openTabSlotIds.add(buf.readInt());
        }
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        if (ctx.getDirection() != NetworkDirection.PLAY_TO_SERVER) return;
        ctx.setPacketHandled(true);
        ServerPlayer player = ctx.getSender();
        if (player == null) return;
        var openTabSlotIds = new ArrayList<>(this.openTabSlotIds);
        ctx.enqueueWork(() -> {
            AbstractContainerMenu container = player.containerMenu;
            if (container instanceof OpenTabHolderInterface c) {
                c.getOpenTabsSlotsIds().clear();
                c.getOpenTabsSlotsIds().addAll(openTabSlotIds);
            }
        });
    }
}

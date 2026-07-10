package dev.tauri.jsg.core.common.packet.packets;

import dev.tauri.jsg.core.common.util.ItemNBT;
import dev.tauri.jsg.core.common.item.notebook.NotebookActionEnum;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.common.registry.CoreSoundEvents;
import dev.tauri.jsg.core.common.sound.JSGSoundHelper;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import dev.tauri.jsg.core.common.packet.PacketContext;

public class NotebookActionPacketToServer extends JSGPacket {
    private NotebookActionEnum action;
    private InteractionHand hand;
    private boolean next;

    public NotebookActionPacketToServer(NotebookActionEnum action, InteractionHand hand, boolean next) {
        this.action = action;
        this.hand = hand;
        this.next = next;
    }

    public NotebookActionPacketToServer(FriendlyByteBuf buf) {
        fromBytes(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(action.ordinal());
        buf.writeInt(hand == InteractionHand.MAIN_HAND ? 0 : 1);
        buf.writeBoolean(next);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        action = NotebookActionEnum.values()[buf.readInt()];
        hand = buf.readInt() == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        next = buf.readBoolean();
    }

    @Override
    public void handle(PacketContext ctx) {
        ctx.setPacketHandled(true);
        var player = ctx.getSender();
        if (player == null) return;
        var world = player.serverLevel();
        ctx.enqueueWork(() -> {
            var stack = player.getItemInHand(hand);

            if (stack.getItem() == CoreItems.NOTEBOOK_ITEM.get() && ItemNBT.hasTag(stack)) {
                var compound = ItemNBT.getOrCreateTag(stack);
                int selected = compound.getInt("selected");

                if (action == NotebookActionEnum.ADDRESS_CHANGE) {
                    int addressCount = compound.getList("pages", Tag.TAG_COMPOUND).size();

                    if (next && selected < addressCount - 1) { // message.offset < 0
                        compound.putInt("selected", (byte) (selected + 1));
                        JSGSoundHelper.playSoundEvent(world, player.blockPosition(), CoreSoundEvents.PAGE_FLIP);
                    }

                    if (!next && selected > 0) {
                        compound.putInt("selected", (byte) (selected - 1));
                        JSGSoundHelper.playSoundEvent(world, player.blockPosition(), CoreSoundEvents.PAGE_FLIP);
                    }
                }
                ItemNBT.setTag(stack, compound);
            }
        });
    }
}

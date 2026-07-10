package dev.tauri.jsg.core.common.state.gui;

import dev.tauri.jsg.core.common.entity.State;
import io.netty.buffer.ByteBuf;

public class CapacitorContainerGuiUpdate extends State {
    public CapacitorContainerGuiUpdate() {
    }

    public int energyStored;
    public int energyTransferredLastTick;

    public CapacitorContainerGuiUpdate(int energyStored, int energyTransferredLastTick) {
        this.energyStored = energyStored;
        this.energyTransferredLastTick = energyTransferredLastTick;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(energyStored);
        buf.writeInt(energyTransferredLastTick);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        energyStored = buf.readInt();
        energyTransferredLastTick = buf.readInt();
    }
}

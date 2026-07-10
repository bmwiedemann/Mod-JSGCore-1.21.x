package dev.tauri.jsg.core.common.state;

import dev.tauri.jsg.core.common.entity.State;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CamoState extends State {
    public CamoState() {
    }

    private BlockState state;

    public CamoState(BlockState state) {
        this.state = state;
    }

    public BlockState getState() {
        return state;
    }


    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(state != null);
        if (state != null) {
            buf.writeInt(Block.getId(state));
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if (buf.readBoolean()) {
            state = Block.stateById(buf.readInt());
        }
    }
}

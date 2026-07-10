package dev.tauri.jsg.core.client.renderer.blockentity.cartouche;

import dev.tauri.jsg.core.common.entity.NotebookPageType;
import dev.tauri.jsg.core.common.entity.State;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.DyeColor;

import javax.annotation.ParametersAreNullableByDefault;

public class CartoucheRendererState extends State {
    public DyeColor color;
    public boolean shiny;
    public NotebookPageType.DataWrapper<?> dataWrapper;

    public CartoucheRendererState() {
    }

    @ParametersAreNullableByDefault
    public CartoucheRendererState(NotebookPageType.DataWrapper<?> dataWrapper, DyeColor color, boolean shiny) {
        this.dataWrapper = dataWrapper;
        this.color = color;
        this.shiny = shiny;
    }

    @Override
    public void toBytes(ByteBuf buff) {
        var buf = new FriendlyByteBuf(buff);
        if (dataWrapper != null) {
            buf.writeBoolean(true);
            buf.writeNbt(dataWrapper.createCompoundTag());
        } else
            buf.writeBoolean(false);
        buf.writeInt(color.getId());
        buf.writeBoolean(shiny);
    }

    @Override
    public void fromBytes(ByteBuf buff) {
        var buf = new FriendlyByteBuf(buff);
        if (buf.readBoolean()) {
            var dNbt = buf.readNbt();
            if (dNbt != null) {
                dataWrapper = NotebookPageType.pageDataFromCompound(dNbt);
            }
        }
        color = DyeColor.byId(buf.readInt());
        shiny = buf.readBoolean();
    }
}

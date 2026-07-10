package dev.tauri.jsg.core.common.packet.packets;

import dev.tauri.jsg.core.common.config.ingame.BEConfig;
import dev.tauri.jsg.core.common.config.ingame.IConfigurable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import dev.tauri.jsg.core.common.packet.NetworkDirection;
import dev.tauri.jsg.core.common.packet.PacketContext;

public class SaveConfigToServer extends PositionedPacket {
    public SaveConfigToServer() {
    }

    BEConfig config = new BEConfig();
    FriendlyByteBuf configBuf;

    public SaveConfigToServer(BlockPos pos, BEConfig config) {
        super(pos);
        this.config = config;
    }

    public SaveConfigToServer(FriendlyByteBuf buf) {
        super(buf);
        configBuf = new FriendlyByteBuf(buf.copy());
        //config.fromBytes(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        config.toBytes(buf);
    }

    @Override
    public void handle(PacketContext ctx) {
        if (ctx.getDirection() != NetworkDirection.PLAY_TO_SERVER) return;
        ServerPlayer player = ctx.getSender();
        ctx.setPacketHandled(true);
        if (player == null) return;
        Level world = player.level();

        ctx.enqueueWork(() -> {
            if (world.getBlockEntity(pos) instanceof IConfigurable configTile && configBuf != null) {
                configTile.getConfig().fromBytes(configBuf);
                configTile.onConfigUpdated();
            }
        });
    }
}

package dev.tauri.jsg.core.common.packet.packets;

import dev.tauri.jsg.core.client.sound.JSGSoundHelperClient;
import dev.tauri.jsg.core.common.sound.IPositionedSound;
import dev.tauri.jsg.core.common.sound.PositionedSound;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class SoundPositionedPlayToClient extends PositionedPacket {
    public IPositionedSound soundEnum;
    public boolean play;

    public SoundPositionedPlayToClient(BlockPos pos, IPositionedSound soundEnum, boolean play) {
        super(pos);

        this.soundEnum = soundEnum;
        this.play = play;
    }

    public SoundPositionedPlayToClient(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(soundEnum.getOrdinal());
        buf.writeBoolean(play);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        super.fromBytes(buf);
        soundEnum = PositionedSound.get(buf.readInt());
        play = buf.readBoolean();
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        if (ctx.getDirection() != NetworkDirection.PLAY_TO_CLIENT) return;
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> {
            JSGSoundHelperClient.playPositionedSoundClientSide(pos, soundEnum, play);
        });
    }
}

package dev.tauri.jsg.core.common.sound;

import dev.tauri.jsg.core.client.sound.JSGSoundHelperClient;
import dev.tauri.jsg.core.common.packet.JSGCorePacketHandler;
import dev.tauri.jsg.core.common.packet.packets.SoundPositionedPlayToClient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;

public class JSGSoundHelper {
    public static void playPositionedSound(@Nullable Level world, BlockPos pos, dev.tauri.jsg.core.common.sound.IPositionedSound soundEnum, boolean play) {
        if (world == null) return;
        if (world.isClientSide) {
            JSGSoundHelperClient.playPositionedSoundClientSide(pos, soundEnum, play);
            return;
        }
        if (play)
            world.gameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Context.of(world.getBlockState(pos)));
        JSGCorePacketHandler.sendToClient(new SoundPositionedPlayToClient(pos, soundEnum, play), new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), 512, world.dimension()));
    }

    public static void playSoundEventClientSide(Level world, BlockPos pos, ISoundEvent soundEventEnum, float pitch) {
        world.gameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Context.of(world.getBlockState(pos)));
        world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, soundEventEnum.getInstance().get(), SoundSource.BLOCKS, soundEventEnum.getVolume(), pitch);
    }

    public static void playSoundEvent(Level world, BlockPos pos, ISoundEvent soundEventEnum) {
        playSoundEvent(world, pos, soundEventEnum, 1f);
    }

    public static void playSoundEvent(Level world, BlockPos pos, ISoundEvent soundEventEnum, float pitch) {
        playSoundEventClientSide(world, pos, soundEventEnum, pitch);
    }

    @SuppressWarnings("deprecation")
    public static void playSoundToPlayer(ServerPlayer player, ISoundEvent soundEventEnum, BlockPos pos) {
        player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(soundEventEnum.getInstance().get()), SoundSource.BLOCKS, pos.getX(), pos.getY(), pos.getZ(), soundEventEnum.getVolume(), 1, player.getRandom().nextLong()));
    }
}

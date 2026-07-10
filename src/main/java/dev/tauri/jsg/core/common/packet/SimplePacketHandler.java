package dev.tauri.jsg.core.common.packet;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.packet.packets.JSGPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Function;

public class SimplePacketHandler {
    public final String networkVersion;
    public final SimpleChannel channelInstance;
    public final ResourceLocation channelName;
    private int currentPacketId = 0;

    public SimplePacketHandler(ResourceLocation channel, String version) {
        networkVersion = version;
        channelName = channel;
        channelInstance = NetworkRegistry.ChannelBuilder.named(channel)
                .clientAcceptedVersions((v) -> Objects.equals(v, networkVersion))
                .serverAcceptedVersions((v) -> Objects.equals(v, networkVersion))
                .networkProtocolVersion(() -> networkVersion)
                .simpleChannel();
    }

    public void sendToServer(Object packet) {
        channelInstance.send(PacketDistributor.SERVER.noArg(), packet);
    }

    public void sendToClient(Object packet, PacketDistributor.TargetPoint point) {
        if (point == null) return;
        channelInstance.send(PacketDistributor.NEAR.with(() -> point), packet);
    }

    public void sendTo(Object packet, ServerPlayer player) {
        if (player == null) return;
        channelInstance.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public <MSG extends JSGPacket> void registerPacket(Class<MSG> clazz, int id, NetworkDirection direction, Function<FriendlyByteBuf, MSG> decoder) {
        try {
            currentPacketId = id;
            channelInstance.messageBuilder(clazz, currentPacketId, direction)
                    .encoder(JSGPacket::toBytes)
                    .decoder(decoder)
                    .consumerNetworkThread(JSGPacket::handleSupplier)
                    .add();
        } catch (Exception e) {
            JSGCore.logger.error("Could not register packet {} for channel {}: ", currentPacketId, channelName, e);
        }
    }

    public <MSG extends JSGPacket> void registerPacket(Class<MSG> clazz, int id, NetworkDirection direction) {
        registerPacket(clazz, id, direction, (buf) -> {
            try {
                return clazz.getConstructor(FriendlyByteBuf.class).newInstance(buf);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <MSG extends JSGPacket> void registerPacket(Class<MSG> clazz, NetworkDirection direction, Function<FriendlyByteBuf, MSG> decoder) {
        registerPacket(clazz, currentPacketId + 1, direction, decoder);
    }

    public <MSG extends JSGPacket> void registerPacket(Class<MSG> clazz, NetworkDirection direction) {
        registerPacket(clazz, currentPacketId + 1, direction);
    }

    public <MSG extends JSGPacket> void registerPacketToServer(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> decoder) {
        registerPacket(clazz, NetworkDirection.PLAY_TO_SERVER, decoder);
    }

    public <MSG extends JSGPacket> void registerPacketToServer(Class<MSG> clazz) {
        registerPacket(clazz, NetworkDirection.PLAY_TO_SERVER);
    }

    public <MSG extends JSGPacket> void registerPacketToClient(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> decoder) {
        registerPacket(clazz, NetworkDirection.PLAY_TO_CLIENT, decoder);
    }

    public <MSG extends JSGPacket> void registerPacketToClient(Class<MSG> clazz) {
        registerPacket(clazz, NetworkDirection.PLAY_TO_CLIENT);
    }
}

package dev.tauri.jsg.core.common.packet;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.packet.packets.JSGPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 1.20.1-shaped packet channel on top of NeoForge's payload system.
 * <p>
 * Registration calls collect into a pending list; the single
 * {@link RegisterPayloadHandlersEvent} listener flushes every handler created during
 * mod construction. Packets keep their Forge-era shape (buf constructor +
 * {@code toBytes}/{@code fromBytes}/{@code handle}).
 * <p>
 * Note: unlike the Forge SimpleChannel setup (network thread), NeoForge invokes the
 * handlers on the main thread; {@code ctx.enqueueWork} calls inside {@code handle}
 * remain correct, just redundant.
 */
public class SimplePacketHandler {
    private static final List<SimplePacketHandler> ALL_HANDLERS = new ArrayList<>();
    private static final Map<Class<?>, CustomPacketPayload.Type<? extends CustomPacketPayload>> PACKET_TYPES = new HashMap<>();

    public final String networkVersion;
    public final ResourceLocation channelName;
    private final List<Consumer<PayloadRegistrar>> pendingRegistrations = new ArrayList<>();

    public SimplePacketHandler(ResourceLocation channel, String version) {
        networkVersion = version;
        channelName = channel;
        synchronized (ALL_HANDLERS) {
            ALL_HANDLERS.add(this);
        }
    }

    public static CustomPacketPayload.Type<? extends CustomPacketPayload> typeOf(Class<?> packetClass) {
        var type = PACKET_TYPES.get(packetClass);
        if (type == null)
            throw new IllegalStateException("Packet " + packetClass.getName() + " was never registered to a SimplePacketHandler");
        return type;
    }

    public void sendToServer(Object packet) {
        PacketDistributor.sendToServer((JSGPacket) packet);
    }

    public void sendToClient(Object packet, TargetPoint point) {
        if (point == null) return;
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        var level = server.getLevel(point.dimension());
        if (level == null) return;
        PacketDistributor.sendToPlayersNear(level, point.excluded(), point.x(), point.y(), point.z(), point.radius(), (JSGPacket) packet);
    }

    public void sendTo(Object packet, ServerPlayer player) {
        if (player == null) return;
        PacketDistributor.sendToPlayer(player, (JSGPacket) packet);
    }

    private <MSG extends JSGPacket> void registerPacket(Class<MSG> clazz, boolean toServer, Function<FriendlyByteBuf, MSG> decoder) {
        var type = new CustomPacketPayload.Type<MSG>(channelName.withSuffix("/" + clazz.getSimpleName().toLowerCase(Locale.ROOT)));
        PACKET_TYPES.put(clazz, type);
        StreamCodec<RegistryFriendlyByteBuf, MSG> codec = StreamCodec.of(
                (buf, packet) -> packet.toBytes(buf),
                decoder::apply);
        pendingRegistrations.add(registrar -> {
            if (toServer)
                registrar.playToServer(type, codec, (payload, ctx) -> payload.handle(new PacketContext(ctx)));
            else
                registrar.playToClient(type, codec, (payload, ctx) -> payload.handle(new PacketContext(ctx)));
        });
    }

    private static <MSG extends JSGPacket> Function<FriendlyByteBuf, MSG> reflectiveDecoder(Class<MSG> clazz) {
        return buf -> {
            try {
                return clazz.getConstructor(FriendlyByteBuf.class).newInstance(buf);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public <MSG extends JSGPacket> void registerPacketToServer(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> decoder) {
        registerPacket(clazz, true, decoder);
    }

    public <MSG extends JSGPacket> void registerPacketToServer(Class<MSG> clazz) {
        registerPacket(clazz, true, reflectiveDecoder(clazz));
    }

    public <MSG extends JSGPacket> void registerPacketToClient(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> decoder) {
        registerPacket(clazz, false, decoder);
    }

    public <MSG extends JSGPacket> void registerPacketToClient(Class<MSG> clazz) {
        registerPacket(clazz, false, reflectiveDecoder(clazz));
    }

    @EventBusSubscriber(modid = JSGCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class RegistrationListener {
        @SubscribeEvent
        public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
            synchronized (ALL_HANDLERS) {
                for (var handler : ALL_HANDLERS) {
                    var registrar = event.registrar(handler.networkVersion);
                    handler.pendingRegistrations.forEach(r -> r.accept(registrar));
                }
            }
        }
    }
}

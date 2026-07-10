package dev.tauri.jsg.core.common.packet;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

/**
 * Source-compatibility shim for Forge's {@code PacketContext} on top of
 * NeoForge's {@link IPayloadContext}.
 */
public class PacketContext {
    private final IPayloadContext ctx;

    public PacketContext(IPayloadContext ctx) {
        this.ctx = ctx;
    }

    public NetworkDirection getDirection() {
        return ctx.flow().isClientbound() ? NetworkDirection.PLAY_TO_CLIENT : NetworkDirection.PLAY_TO_SERVER;
    }

    @Nullable
    public ServerPlayer getSender() {
        return ctx.player() instanceof ServerPlayer sp ? sp : null;
    }

    /**
     * No-op: NeoForge payloads are always marked handled; kept for source compatibility.
     */
    public void setPacketHandled(boolean handled) {
    }

    public CompletableFuture<Void> enqueueWork(Runnable work) {
        return ctx.enqueueWork(work);
    }

    public IPayloadContext unwrap() {
        return ctx;
    }
}

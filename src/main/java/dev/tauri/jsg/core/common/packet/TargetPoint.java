package dev.tauri.jsg.core.common.packet;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * Source-compatibility shim for Forge's {@code TargetPoint},
 * consumed by {@link SimplePacketHandler#sendToClient}.
 */
public record TargetPoint(@Nullable ServerPlayer excluded, double x, double y, double z, double radius,
                          ResourceKey<Level> dimension) {

    public TargetPoint(double x, double y, double z, double radius, ResourceKey<Level> dimension) {
        this(null, x, y, z, radius, dimension);
    }
}

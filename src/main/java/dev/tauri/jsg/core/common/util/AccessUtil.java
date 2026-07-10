package dev.tauri.jsg.core.common.util;

import net.minecraft.server.MinecraftServer;

import javax.annotation.ParametersAreNonnullByDefault;

public class AccessUtil {
    /**
     * @param time absolute time in milliseconds (as returned by {@link net.minecraft.Util#getMillis()});
     *             converted to the nanosecond clock used by the server since 1.20.3.
     */
    @ParametersAreNonnullByDefault
    public static void setNextTickTime(MinecraftServer server, long time) {
        server.nextTickTimeNanos = time * 1_000_000L;
    }
}

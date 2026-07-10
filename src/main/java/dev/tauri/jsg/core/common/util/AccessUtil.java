package dev.tauri.jsg.core.common.util;

import net.minecraft.server.MinecraftServer;

import javax.annotation.ParametersAreNonnullByDefault;

public class AccessUtil {
    @ParametersAreNonnullByDefault
    public static void setNextTickTime(MinecraftServer server, long time) {
        server.nextTickTime = time;
    }
}

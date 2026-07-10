package dev.tauri.jsg.core.common.packet;

/**
 * Source-compatibility shim for Forge's {@code NetworkDirection} play directions,
 * as reported by {@link PacketContext#getDirection()}.
 */
public enum NetworkDirection {
    PLAY_TO_SERVER,
    PLAY_TO_CLIENT
}

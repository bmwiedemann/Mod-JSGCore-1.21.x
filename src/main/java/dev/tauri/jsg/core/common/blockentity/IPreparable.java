package dev.tauri.jsg.core.common.blockentity;

import javax.annotation.Nullable;

/**
 * Marks BE as prepare-able for saving into NBT file
 */
public interface IPreparable {
    boolean prepareBE();

    default boolean prepareBE(@Nullable String arg) {
        return prepareBE();
    }
}

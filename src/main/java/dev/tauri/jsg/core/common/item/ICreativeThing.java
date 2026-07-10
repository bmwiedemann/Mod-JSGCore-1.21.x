package dev.tauri.jsg.core.common.item;

public interface ICreativeThing {
    default boolean isCreativeOnly() {
        return true;
    }
}

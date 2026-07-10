package dev.tauri.jsg.core.common.sound;

import dev.tauri.jsg.core.client.sound.JSGMainMenuSound;
import dev.tauri.jsg.core.client.sound.JSGPositionedSound;
import net.minecraft.core.BlockPos;

import java.util.function.Supplier;

public interface IPositionedSound extends ISoundEvent {
    boolean isLoopSound();

    JSGPositionedSound getInstance(BlockPos pos);

    JSGMainMenuSound getInstanceAbsolute(Supplier<Float> volumeSupplier);
}

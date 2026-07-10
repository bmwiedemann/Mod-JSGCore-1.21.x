package dev.tauri.jsg.core.common.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.RegistryObject;

public interface ISoundEvent {
    int getOrdinal();

    ResourceLocation getLocation();

    float getVolume();

    RegistryObject<SoundEvent> getInstance();
}

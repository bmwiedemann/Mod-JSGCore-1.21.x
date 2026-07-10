package dev.tauri.jsg.core.common.sound;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class SoundEvent implements dev.tauri.jsg.core.common.sound.ISoundEvent {
    private static int currentOrdinal = 0;
    private static final Map<Integer, SoundEvent> REGISTRY = new HashMap<>();

    public final int ordinal;
    public final ResourceLocation resourceLocation;
    public final float volume;

    public RegistryObject<net.minecraft.sounds.SoundEvent> event;

    public final int length;

    public SoundEvent(ResourceLocation id, float volume) {
        this(id, volume, 1);
    }

    public SoundEvent(ResourceLocation id, float volume, int length) {
        this.ordinal = currentOrdinal++;
        this.resourceLocation = id;
        this.volume = volume * 3;
        this.length = length;
        REGISTRY.put(ordinal, this);
    }

    public SoundEvent register(DeferredRegister<net.minecraft.sounds.SoundEvent> soundRegistry) {
        this.event = soundRegistry.register(resourceLocation.getPath(), () -> net.minecraft.sounds.SoundEvent.createFixedRangeEvent(resourceLocation, 64));
        return this;
    }

    public static SoundEvent get(int id) {
        return REGISTRY.get(id);
    }

    public static Collection<SoundEvent> values() {
        return REGISTRY.values();
    }

    public static void load() {
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public ResourceLocation getLocation() {
        return resourceLocation;
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public RegistryObject<net.minecraft.sounds.SoundEvent> getInstance() {
        return event;
    }
}

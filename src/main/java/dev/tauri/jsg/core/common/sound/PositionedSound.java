package dev.tauri.jsg.core.common.sound;

import dev.tauri.jsg.core.client.sound.JSGMainMenuSound;
import dev.tauri.jsg.core.client.sound.JSGPositionedSound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PositionedSound implements dev.tauri.jsg.core.common.sound.IPositionedSound {
    private static int currentOrdinal = 0;
    private static final Map<Integer, PositionedSound> REGISTRY = new HashMap<>();

    public final int ordinal;
    public final ResourceLocation resourceLocation;
    public final boolean loop;
    public final float volume;

    public RegistryObject<SoundEvent> event;

    public PositionedSound(ResourceLocation id, boolean loop, float volume) {
        this.ordinal = currentOrdinal++;
        this.resourceLocation = id;
        this.loop = loop;
        this.volume = volume * 3;
        REGISTRY.put(ordinal, this);
    }

    public PositionedSound register(DeferredRegister<SoundEvent> soundRegistry) {
        this.event = soundRegistry.register(resourceLocation.getPath(), () -> SoundEvent.createFixedRangeEvent(resourceLocation, 64));
        return this;
    }

    public static PositionedSound get(int id) {
        return REGISTRY.get(id);
    }

    public static Collection<PositionedSound> values() {
        return REGISTRY.values();
    }

    public static void load() {
    }

    @Override
    public JSGPositionedSound getInstance(BlockPos pos) {
        return new JSGPositionedSound(pos, event.get(), SoundSource.BLOCKS, SoundInstance.createUnseededRandom(), loop, volume);
    }

    @Override
    public JSGMainMenuSound getInstanceAbsolute(Supplier<Float> volumeSupplier) {
        return new JSGMainMenuSound(event.get(), SoundSource.BLOCKS, SoundInstance.createUnseededRandom(), loop, volumeSupplier);
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
    public boolean isLoopSound() {
        return loop;
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public RegistryObject<SoundEvent> getInstance() {
        return event;
    }
}

package dev.tauri.jsg.core.common.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 1.21: jukebox songs are data-driven. The song key is derived from the sound event id,
 * so each disc needs a matching data/&lt;ns&gt;/jukebox_song/&lt;path&gt;.json file.
 */
public class JSGMusicDiscItem extends Item implements ITabbedItem {
    public JSGMusicDiscItem(Supplier<SoundEvent> soundSupplier, int length) {
        super((new Item.Properties()).stacksTo(1).rarity(Rarity.RARE)
                .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG,
                        Objects.requireNonNull(BuiltInRegistries.SOUND_EVENT.getKey(soundSupplier.get())))));
    }
}

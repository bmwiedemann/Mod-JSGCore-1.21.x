package dev.tauri.jsg.core.common.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;

import java.util.function.Supplier;

public class JSGMusicDiscItem extends RecordItem implements ITabbedItem {
    public JSGMusicDiscItem(Supplier<SoundEvent> soundSupplier, int length) {
        super(15, soundSupplier, (new Item.Properties()).stacksTo(1).rarity(Rarity.RARE), length * 20);
    }
}

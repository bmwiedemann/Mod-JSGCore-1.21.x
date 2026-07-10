package dev.tauri.jsg.core.common.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.function.Supplier;

public class JSGSpawnEggItem extends ForgeSpawnEggItem implements ITabbedItem {

    public JSGSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Properties props) {
        super(type, backgroundColor, highlightColor, props);
    }

    // tab is handled in separated event - we are hooking into vanilla tab
}

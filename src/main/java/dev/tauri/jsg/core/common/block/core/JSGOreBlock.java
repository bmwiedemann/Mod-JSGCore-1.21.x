package dev.tauri.jsg.core.common.block.core;

import dev.tauri.jsg.core.common.item.ITabbedItem;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraftforge.registries.RegistryObject;

public class JSGOreBlock extends DropExperienceBlock implements ITabbedItem {
    public JSGOreBlock(Properties pProperties) {
        super(pProperties.strength(4.5F, 3.0F).requiresCorrectToolForDrops(), UniformInt.of(2, 5));
    }

    @Override
    public RegistryObject<CreativeModeTab> getTab() {
        return null;
    }
}

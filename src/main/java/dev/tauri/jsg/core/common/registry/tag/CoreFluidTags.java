package dev.tauri.jsg.core.common.registry.tag;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class CoreFluidTags {
    public static TagKey<Fluid> MOLTEN_NAQUADAH = tag("molten_naquadah");
    public static TagKey<Fluid> MOLTEN_NAQUADAH_ALLOY = tag("molten_naquadah_alloy");
    public static TagKey<Fluid> MOLTEN_NAQUADAH_REFINED = tag("molten_naquadah_refined");
    public static TagKey<Fluid> MOLTEN_TITANIUM = tag("molten_titanium");
    public static TagKey<Fluid> MOLTEN_TRINIUM = tag("molten_trinium");

    private static TagKey<Fluid> tag(String name) {
        return FluidTags.create(JSGMapping.rl(JSGCore.MOD_ID, name));
    }
}

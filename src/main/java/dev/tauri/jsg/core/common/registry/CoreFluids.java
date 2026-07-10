package dev.tauri.jsg.core.common.registry;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.block.cauldron.CauldronRecipe;
import dev.tauri.jsg.core.common.registry.helper.CoreRegistryHelpers;
import dev.tauri.jsg.core.common.registry.helper.FluidHelper;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class CoreFluids {
    // TODO: Refactor to use one texture and hex colors
    public static final FluidHelper.MoltenFluid MOLTEN_NAQUADAH_RAW = CoreRegistryHelpers.FLUID_HELPER.createGenericFluid(JSGMapping.rl(JSGCore.MOD_ID, "naquadah_molten_raw"), 0xFFFFFFFF);
    public static final FluidHelper.MoltenFluid MOLTEN_NAQUADAH_ALLOY = CoreRegistryHelpers.FLUID_HELPER.createGenericFluid(JSGMapping.rl(JSGCore.MOD_ID, "naquadah_molten_alloy"), 0xFFFFFFFF);
    public static final FluidHelper.MoltenFluid MOLTEN_NAQUADAH_REFINED = CoreRegistryHelpers.FLUID_HELPER.createGenericFluid(JSGMapping.rl(JSGCore.MOD_ID, "naquadah_molten_refined"), 0xFFFFFFFF);
    public static final FluidHelper.MoltenFluid MOLTEN_TITANIUM = CoreRegistryHelpers.FLUID_HELPER.createGenericFluid(JSGMapping.rl(JSGCore.MOD_ID, "titanium_molten"), 0xFFFFFFFF);
    public static final FluidHelper.MoltenFluid MOLTEN_TRINIUM = CoreRegistryHelpers.FLUID_HELPER.createGenericFluid(JSGMapping.rl(JSGCore.MOD_ID, "trinium_molten"), 0xFFFFFFFF);

    public static void init() {
    }

    // TODO: refactor to datapack
    public static void registerCauldrons() {
        // generic recipes (empty/fill cauldrons)
        new CauldronRecipe.CauldronGenericRecipes(MOLTEN_NAQUADAH_RAW).insertInteractions();
        new CauldronRecipe.CauldronGenericRecipes(MOLTEN_NAQUADAH_ALLOY).insertInteractions();
        new CauldronRecipe.CauldronGenericRecipes(MOLTEN_NAQUADAH_REFINED).insertInteractions();
        new CauldronRecipe.CauldronGenericRecipes(MOLTEN_TITANIUM).insertInteractions();
        new CauldronRecipe.CauldronGenericRecipes(MOLTEN_TRINIUM).insertInteractions();

        // melting
        CauldronRecipe.melting()
                .setItemToMelt(CoreItems.NAQUADAH_ALLOY_RAW::get)
                .setNewCauldronStateSupplier(() -> MOLTEN_NAQUADAH_RAW.cauldron.get().defaultBlockState())
                .setNewFluid(() -> new FluidStack(CoreFluids.MOLTEN_NAQUADAH_RAW.get(), 90))
                .setRecipeHandlers(List.of(CoreFluids.MOLTEN_NAQUADAH_RAW.cauldronInteractionMap, CauldronInteraction.EMPTY))
                .requireHeating()
                .insertInteractions();
        CauldronRecipe.melting()
                .setItemToMelt(CoreItems.NAQUADAH_ALLOY::get)
                .setNewCauldronStateSupplier(() -> MOLTEN_NAQUADAH_ALLOY.cauldron.get().defaultBlockState())
                .setNewFluid(() -> new FluidStack(CoreFluids.MOLTEN_NAQUADAH_ALLOY.get(), 90))
                .setRecipeHandlers(List.of(CoreFluids.MOLTEN_NAQUADAH_ALLOY.cauldronInteractionMap, CauldronInteraction.EMPTY))
                .insertInteractions();
        CauldronRecipe.melting()
                .setItemToMelt(CoreItems.NAQUADAH_ALLOY_REFINED::get)
                .setNewCauldronStateSupplier(() -> MOLTEN_NAQUADAH_REFINED.cauldron.get().defaultBlockState())
                .setNewFluid(() -> new FluidStack(CoreFluids.MOLTEN_NAQUADAH_REFINED.get(), 90))
                .setRecipeHandlers(List.of(CoreFluids.MOLTEN_NAQUADAH_REFINED.cauldronInteractionMap, CauldronInteraction.EMPTY))
                .insertInteractions();
        CauldronRecipe.melting()
                .setItemToMelt(CoreItems.TITANIUM_INGOT::get)
                .setNewCauldronStateSupplier(() -> MOLTEN_TITANIUM.cauldron.get().defaultBlockState())
                .setNewFluid(() -> new FluidStack(CoreFluids.MOLTEN_TITANIUM.get(), 90))
                .setRecipeHandlers(List.of(CoreFluids.MOLTEN_TITANIUM.cauldronInteractionMap, CauldronInteraction.EMPTY))
                .insertInteractions();
        CauldronRecipe.melting()
                .setItemToMelt(CoreItems.TRINIUM_INGOT::get)
                .setNewCauldronStateSupplier(() -> MOLTEN_TRINIUM.cauldron.get().defaultBlockState())
                .setNewFluid(() -> new FluidStack(CoreFluids.MOLTEN_TRINIUM.get(), 90))
                .setRecipeHandlers(List.of(CoreFluids.MOLTEN_TRINIUM.cauldronInteractionMap, CauldronInteraction.EMPTY))
                .insertInteractions();

        // mixture
        CauldronRecipe.mixture()
                .setItemToMelt(() -> Items.IRON_INGOT)
                .setNewCauldronStateSupplier(() -> MOLTEN_NAQUADAH_ALLOY.cauldron.get().defaultBlockState())
                .setNewFluid(MOLTEN_NAQUADAH_ALLOY::get)
                .setBaseFluid(MOLTEN_NAQUADAH_RAW::get)
                .setRecipeHandlers(MOLTEN_NAQUADAH_RAW.cauldronInteractionMap)
                .insertInteractions();
        CauldronRecipe.mixture()
                .setItemToMelt(() -> Items.QUARTZ)
                .setNewCauldronStateSupplier(() -> MOLTEN_NAQUADAH_REFINED.cauldron.get().defaultBlockState())
                .setNewFluid(MOLTEN_NAQUADAH_REFINED::get)
                .setBaseFluid(MOLTEN_NAQUADAH_ALLOY::get)
                .setRecipeHandlers(MOLTEN_NAQUADAH_ALLOY.cauldronInteractionMap)
                .insertInteractions();

        // bathing
        CauldronRecipe.bathing()
                .setItemToBath(CoreItems.CIRCUIT_CONTROL_BASE::get)
                .setDrop(() -> new ItemStack(CoreItems.CIRCUIT_CONTROL_NAQUADAH.get()))
                .setBaseFluid(() -> new FluidStack(MOLTEN_NAQUADAH_ALLOY.get(), 90))
                .setRecipeHandlers(MOLTEN_NAQUADAH_ALLOY.cauldronInteractionMap)
                .insertInteractions();
        CauldronRecipe.bathing()
                .setItemToBath(CoreItems.CIRCUIT_CONTROL_BASE::get)
                .setDrop(() -> new ItemStack(CoreItems.CIRCUIT_CONTROL_CRYSTAL.get()))
                .setBaseFluid(() -> new FluidStack(MOLTEN_TRINIUM.get(), 90))
                .setRecipeHandlers(MOLTEN_TRINIUM.cauldronInteractionMap)
                .insertInteractions();

        // casting ingots from fluids
        CauldronRecipe.casting()
                .setDrop(() -> new ItemStack(CoreItems.NAQUADAH_ALLOY_RAW.get()))
                .setBaseFluid(() -> new FluidStack(MOLTEN_NAQUADAH_RAW.get(), 90))
                .setRecipeHandlers(MOLTEN_NAQUADAH_RAW.cauldronInteractionMap)
                .insertInteractions();
        CauldronRecipe.casting()
                .setDrop(() -> new ItemStack(CoreItems.NAQUADAH_ALLOY.get()))
                .setBaseFluid(() -> new FluidStack(MOLTEN_NAQUADAH_ALLOY.get(), 90))
                .setRecipeHandlers(MOLTEN_NAQUADAH_ALLOY.cauldronInteractionMap)
                .insertInteractions();
        CauldronRecipe.casting()
                .setDrop(() -> new ItemStack(CoreItems.NAQUADAH_ALLOY_REFINED.get()))
                .setBaseFluid(() -> new FluidStack(MOLTEN_NAQUADAH_REFINED.get(), 90))
                .setRecipeHandlers(MOLTEN_NAQUADAH_REFINED.cauldronInteractionMap)
                .insertInteractions();
        CauldronRecipe.casting()
                .setDrop(() -> new ItemStack(CoreItems.TITANIUM_INGOT.get()))
                .setBaseFluid(() -> new FluidStack(MOLTEN_TITANIUM.get(), 90))
                .setRecipeHandlers(MOLTEN_TITANIUM.cauldronInteractionMap)
                .insertInteractions();
        CauldronRecipe.casting()
                .setDrop(() -> new ItemStack(CoreItems.TRINIUM_INGOT.get()))
                .setBaseFluid(() -> new FluidStack(MOLTEN_TRINIUM.get(), 90))
                .setRecipeHandlers(MOLTEN_TRINIUM.cauldronInteractionMap)
                .insertInteractions();
    }
}

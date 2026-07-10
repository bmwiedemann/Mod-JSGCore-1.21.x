package dev.tauri.jsg.core.datagen.tag;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.registry.CoreFluids;
import dev.tauri.jsg.core.common.registry.tag.CoreFluidTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

public class JSGFluidTagGenerator extends FluidTagsProvider {
    public JSGFluidTagGenerator(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pProvider, JSGCore.MOD_ID, existingFileHelper);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(CoreFluidTags.MOLTEN_NAQUADAH)
                .add(CoreFluids.MOLTEN_NAQUADAH_RAW.get());

        tag(CoreFluidTags.MOLTEN_NAQUADAH_ALLOY)
                .add(CoreFluids.MOLTEN_NAQUADAH_ALLOY.get());

        tag(CoreFluidTags.MOLTEN_NAQUADAH_REFINED)
                .add(CoreFluids.MOLTEN_NAQUADAH_REFINED.get());

        tag(CoreFluidTags.MOLTEN_TITANIUM)
                .add(CoreFluids.MOLTEN_TITANIUM.get());

        tag(CoreFluidTags.MOLTEN_TRINIUM)
                .add(CoreFluids.MOLTEN_TRINIUM.get());
    }
}

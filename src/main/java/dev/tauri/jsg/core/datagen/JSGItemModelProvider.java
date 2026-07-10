package dev.tauri.jsg.core.datagen;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.client.model.JSGOBJModelLoaderBuilder;
import dev.tauri.jsg.core.common.registry.CoreBlocks;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public class JSGItemModelProvider extends ItemModelProvider {
    public JSGItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, JSGCore.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        var sandstoneRL = dev.tauri.jsg.core.datagen.JSGBlockStateProvider.getRL(CoreBlocks.STATIC_SMOOTH_SANDSTONE.get());
        getBuilder(sandstoneRL.toString())
                .parent(new ModelFile.ExistingModelFile(JSGMapping.rl("block/" + JSGBlockStateProvider.getRL(Blocks.SMOOTH_SANDSTONE).getPath()), existingFileHelper));

        CoreBlocks.CARTOUCHES.forEach((block, types) ->
                types.forEach((type, blockRegObj) ->
                        blockOBJModel(blockRegObj, JSGOBJModelLoaderBuilder.DEFAULT_RENDER_TYPES)
                )
        );
    }

    private void blockOBJModel(RegistryObject<Block> block, ItemDisplayContext... renderTypes) {
        itemOBJModel(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block.get())), renderTypes);
    }

    private void itemOBJModel(RegistryObject<Item> item, ItemDisplayContext... renderTypes) {
        itemOBJModel(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item.get())), renderTypes);
    }

    private void itemOBJModel(ResourceLocation item, ItemDisplayContext... renderTypes) {
        getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", JSGMapping.rl(JSGCore.MOD_ID, "block/wip"))
                .customLoader((parent, existingFileHelper) -> new JSGOBJModelLoaderBuilder<>(parent, existingFileHelper).renderTypes(renderTypes));
    }
}

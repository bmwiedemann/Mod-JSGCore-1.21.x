package dev.tauri.jsg.core.datagen;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.registry.CoreBlocks;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class JSGBlockStateProvider extends BlockStateProvider {
    public JSGBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, JSGCore.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        getVariantBuilder(CoreBlocks.STATIC_SMOOTH_SANDSTONE.get())
                .partialState()
                .modelForState()
                .modelFile(models()
                        .withExistingParent(getRL(CoreBlocks.STATIC_SMOOTH_SANDSTONE.get()).toString(), JSGMapping.rl("block/" + getRL(Blocks.SMOOTH_SANDSTONE).getPath()))
                ).addModel();
        CoreBlocks.CARTOUCHES_BLOCKS.forEach((type, blockState) -> {
            var loc = getRL(blockState.get().getBlock());
            if (loc == null) {
                throw new RuntimeException("Can not get location of block " + blockState.get().getBlock().getDescriptionId());
            }
            var model = models().withExistingParent(type + "_cartouche", JSGMapping.rl(loc.getNamespace(), "block/" + loc.getPath()));
            CoreBlocks.CARTOUCHES.get(type).forEach((variant, cartouche) -> {
                getVariantBuilder(cartouche.get()).partialState().modelForState().modelFile(model).addModel();
            });
        });
    }

    public static ResourceLocation getRL(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}

package dev.tauri.jsg.core.common.listener;

import net.neoforged.fml.common.EventBusSubscriber;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.block.crystal.CrystalColor;
import dev.tauri.jsg.core.common.recipe.notebook.NotebookCloneRecipe;
import dev.tauri.jsg.core.common.recipe.notebook.NotebookCreationRecipe;
import dev.tauri.jsg.core.common.recipe.notebook.NotebookMergePageRecipe;
import dev.tauri.jsg.core.common.recipe.notebook.NotebookMergeRecipe;
import dev.tauri.jsg.core.common.registry.CoreBlocks;
import dev.tauri.jsg.core.common.registry.CoreItems;
import dev.tauri.jsg.core.common.registry.helper.builder.block.OreBlockVariant;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import java.util.concurrent.Executor;

@EventBusSubscriber(modid = JSGCore.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CoreForgeEventHandler {

    @SubscribeEvent
    public static void onClickWithCrystalSeed(PlayerInteractEvent.RightClickBlock e) {
        if (e.getUseItem() == Event.Result.ALLOW) return;
        if (e.getUseBlock() == Event.Result.ALLOW) return;
        var item = e.getEntity().getItemInHand(e.getHand());
        if (item.isEmpty()) return;
        var bHit = e.getHitVec();
        var block = e.getLevel().getBlockState(bHit.getBlockPos());
        if (block.isAir()) return;
        var crystalBuddingBase = OreBlockVariant.fromBlock(block, () -> CoreBlocks.CRYSTAL_BLOCK);
        if (crystalBuddingBase == null) return;
        var crystalItemColor = CrystalColor.fromItem(item, () -> CoreItems.CRYSTAL_SEEDS);
        if (crystalBuddingBase == OreBlockVariant.SELF) {
            var crystalBlockColor = CrystalColor.fromBlock(block, () -> CoreBlocks.CRYSTAL_BLOCK);
            if (crystalBlockColor == null) return;
            if (crystalBlockColor != crystalItemColor) return;
        }
        var newBlockMap = CoreBlocks.UNSTABLE_CRYSTAL_BUDDING.get(crystalBuddingBase);
        if (newBlockMap == null) return;
        var newBlock = newBlockMap.get(crystalItemColor);
        if (newBlock == null) return;

        if (!e.getEntity().isCreative())
            item.shrink(1);

        var newState = newBlock.get().defaultBlockState().setValue(BlockStateProperties.AGE_2, 0);
        e.getLevel().setBlock(bHit.getBlockPos(), newState, 3);
        e.getLevel().playSound(null, bHit.getBlockPos(), SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.BLOCKS, 1, 1);
    }

    @SubscribeEvent
    public static void onServerResourceReload(AddReloadListenerEvent event) {
        event.addListener((PreparableReloadListener.PreparationBarrier pPreparationBarrier,
                           ResourceManager pResourceManager,
                           ProfilerFiller pPreparationsProfiler,
                           ProfilerFiller pReloadProfiler,
                           Executor pBackgroundExecutor,
                           Executor pGameExecutor) -> pPreparationBarrier.wait(Unit.INSTANCE).thenRun(() -> {
            var recipesManager = event.getServerResources().getRecipeManager();
            var recipes = recipesManager.getRecipes();

            recipes.add(new NotebookCloneRecipe());
            recipes.add(new NotebookCreationRecipe());
            recipes.add(new NotebookMergePageRecipe());
            recipes.add(new NotebookMergeRecipe());

            recipesManager.replaceRecipes(recipes);
            JSGCore.logger.info("Recipes successfully reloaded!");
        }));
    }
}

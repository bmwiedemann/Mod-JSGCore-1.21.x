package dev.tauri.jsg.core.common.registry.helper;

import dev.tauri.jsg.core.common.block.util.IItemBlock;
import dev.tauri.jsg.core.common.entity.BiomeOverlayInstance;
import dev.tauri.jsg.core.common.entity.NotebookPageType;
import dev.tauri.jsg.core.common.entity.ScheduledTaskType;
import dev.tauri.jsg.core.common.entity.StateType;
import dev.tauri.jsg.core.common.item.ITabbedItem;
import dev.tauri.jsg.core.common.item.JSGBlockItem;
import dev.tauri.jsg.core.common.raycaster.Raycaster;
import dev.tauri.jsg.core.common.registry.JSGCoreRegistries;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.symbol.SymbolUsage;
import dev.tauri.jsg.core.common.symbol.pointoforigin.IPointOfOriginType;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.network.IContainerFactory;
import dev.tauri.jsg.core.common.registry.JSGDeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import dev.tauri.jsg.core.common.registry.RegistryObject;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RegistryHelper {
    protected final String modId;
    protected Runnable menuScreensRegisterRun = () -> {
    };
    protected Runnable entityRendererRegisterRun = () -> {
    };
    protected Supplier<List<BlockEntityRendererPair<?>>> blockEntityRenderers = List::of;
    protected List<JSGDeferredRegister<?>> registerList = new ArrayList<>();

    public void register(IEventBus eventBus) {
        if (blockRegistry != null) {
            var itemReg = item();
            for (var blockObject : blockRegistry.getEntries()) {
                var id = blockObject.getId();
                if (id == null) continue;
                itemReg.register(id.getPath(), () -> {
                    List<RegistryObject<CreativeModeTab>> tabs = List.of();
                    if (blockObject.get() instanceof ITabbedItem t) {
                        tabs = new ArrayList<>(t.getTabs());
                    }
                    if (blockObject.get() instanceof IItemBlock itemBlock)
                        return itemBlock.getItemBlock();
                    return new JSGBlockItem(blockObject.get(), new Item.Properties(), tabs);
                });
            }
        }

        eventBus.addListener(EventPriority.NORMAL, false, FMLClientSetupEvent.class, event -> event.enqueueWork(() -> entityRendererRegisterRun.run()));

        eventBus.addListener(EventPriority.NORMAL, false, net.neoforged.neoforge.client.event.RegisterMenuScreensEvent.class, event -> {
            currentMenuScreensEvent = event;
            menuScreensRegisterRun.run();
            currentMenuScreensEvent = null;
        });

        eventBus.addListener(EventPriority.NORMAL, false, EntityRenderersEvent.RegisterRenderers.class, event -> blockEntityRenderers.get().forEach(r -> r.register(event)));

        registerList.forEach(r -> r.register(eventBus));
    }

    public void toRegister(JSGDeferredRegister<?> deferredRegister) {
        registerList.add(deferredRegister);
    }

    public RegistryHelper(String modId) {
        this.modId = modId;
    }

    public RegistryHelper guiRegister(Runnable runnable) {
        this.menuScreensRegisterRun = runnable;
        return this;
    }

    public RegistryHelper entityRendererRegister(Runnable runnable) {
        this.entityRendererRegisterRun = runnable;
        return this;
    }

    public RegistryHelper beRenderers(Supplier<List<BlockEntityRendererPair<?>>> supplier) {
        this.blockEntityRenderers = supplier;
        return this;
    }

    public JSGDeferredRegister<FluidType> fluidType() {
        if (fluidTypeRegistry == null) {
            fluidTypeRegistry = JSGDeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, modId);
            toRegister(fluidTypeRegistry);
        }
        return fluidTypeRegistry;
    }

    public JSGDeferredRegister<Fluid> fluid() {
        if (fluidRegistry == null) {
            fluidRegistry = JSGDeferredRegister.create(Registries.FLUID, modId);
            toRegister(fluidRegistry);
        }
        return fluidRegistry;
    }

    public JSGDeferredRegister<PoiType> poi() {
        if (villagerPoiRegistry == null) {
            villagerPoiRegistry = JSGDeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, modId);
            toRegister(villagerPoiRegistry);
        }
        return villagerPoiRegistry;
    }

    public JSGDeferredRegister<VillagerType> villagerType() {
        if (villagerTypeRegistry == null) {
            villagerTypeRegistry = JSGDeferredRegister.create(Registries.VILLAGER_TYPE, modId);
            toRegister(villagerTypeRegistry);
        }
        return villagerTypeRegistry;
    }

    public JSGDeferredRegister<VillagerProfession> villagerProfession() {
        if (villagerProfessionRegistry == null) {
            villagerProfessionRegistry = JSGDeferredRegister.create(Registries.VILLAGER_PROFESSION, modId);
            toRegister(villagerProfessionRegistry);
        }
        return villagerProfessionRegistry;
    }

    public JSGDeferredRegister<EntityType<?>> entity() {
        if (entityRegistry == null) {
            entityRegistry = JSGDeferredRegister.create(Registries.ENTITY_TYPE, modId);
            toRegister(entityRegistry);
        }
        return entityRegistry;
    }

    public JSGDeferredRegister<StructureType<?>> structureType() {
        if (structureTypeRegistry == null) {
            structureTypeRegistry = JSGDeferredRegister.create(Registries.STRUCTURE_TYPE, modId);
            toRegister(structureTypeRegistry);
        }
        return structureTypeRegistry;
    }

    public JSGDeferredRegister<Feature<?>> feature() {
        if (featureRegistry == null) {
            featureRegistry = JSGDeferredRegister.create(Registries.FEATURE, modId);
            toRegister(featureRegistry);
        }
        return featureRegistry;
    }

    public JSGDeferredRegister<MenuType<?>> menu() {
        if (menuTypeRegistry == null) {
            menuTypeRegistry = JSGDeferredRegister.create(Registries.MENU, modId);
            toRegister(menuTypeRegistry);
        }
        return menuTypeRegistry;
    }

    public JSGDeferredRegister<StateType> state() {
        if (stateTypeRegistry == null) {
            stateTypeRegistry = JSGDeferredRegister.create(JSGCoreRegistries.STATE_TYPE, modId);
            toRegister(stateTypeRegistry);
        }
        return stateTypeRegistry;
    }

    public JSGDeferredRegister<SymbolUsage> symbolUsage() {
        if (symbolUsageRegistry == null) {
            symbolUsageRegistry = JSGDeferredRegister.create(JSGCoreRegistries.SYMBOL_USAGE, modId);
            toRegister(symbolUsageRegistry);
        }
        return symbolUsageRegistry;
    }

    public JSGDeferredRegister<SymbolType<?>> symbolType() {
        if (symbolTypeRegistry == null) {
            symbolTypeRegistry = JSGDeferredRegister.create(JSGCoreRegistries.SYMBOL_TYPE, modId);
            toRegister(symbolTypeRegistry);
        }
        return symbolTypeRegistry;
    }

    public JSGDeferredRegister<SoundEvent> sound() {
        if (soundRegistry == null) {
            soundRegistry = JSGDeferredRegister.create(Registries.SOUND_EVENT, modId);
            toRegister(soundRegistry);
        }
        return soundRegistry;
    }

    public JSGDeferredRegister<CreativeModeTab> tab() {
        if (tabRegistry == null) {
            tabRegistry = JSGDeferredRegister.create(Registries.CREATIVE_MODE_TAB, modId);
            toRegister(tabRegistry);
        }
        return tabRegistry;
    }

    public JSGDeferredRegister<BlockEntityType<?>> be() {
        if (beRegistry == null) {
            beRegistry = JSGDeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, modId);
            toRegister(beRegistry);
        }
        return beRegistry;
    }

    public JSGDeferredRegister<Block> block() {
        if (blockRegistry == null) {
            blockRegistry = JSGDeferredRegister.create(Registries.BLOCK, modId);
            toRegister(blockRegistry);
        }
        return blockRegistry;
    }

    public JSGDeferredRegister<Item> item() {
        if (itemRegistry == null) {
            itemRegistry = JSGDeferredRegister.create(Registries.ITEM, modId);
            toRegister(itemRegistry);
        }
        return itemRegistry;
    }

    public JSGDeferredRegister<ScheduledTaskType> scheduledTask() {
        if (scheduledTaskTypeRegistry == null) {
            scheduledTaskTypeRegistry = JSGDeferredRegister.create(JSGCoreRegistries.SCHEDULED_TASK_TYPE, modId);
            toRegister(scheduledTaskTypeRegistry);
        }
        return scheduledTaskTypeRegistry;
    }

    public JSGDeferredRegister<BiomeOverlayInstance> biomeOverlay() {
        if (biomeOverlayRegistry == null) {
            biomeOverlayRegistry = JSGDeferredRegister.create(JSGCoreRegistries.BIOME_OVERLAY, modId);
            toRegister(biomeOverlayRegistry);
        }
        return biomeOverlayRegistry;
    }

    public JSGDeferredRegister<NotebookPageType<?>> notebookPage() {
        if (notebookPageTypeRegistry == null) {
            notebookPageTypeRegistry = JSGDeferredRegister.create(JSGCoreRegistries.NOTEBOOK_PAGE_TYPE, modId);
            toRegister(notebookPageTypeRegistry);
        }
        return notebookPageTypeRegistry;
    }

    public JSGDeferredRegister<IPointOfOriginType> pooType() {
        if (pointOfOriginTypeRegistry == null) {
            pointOfOriginTypeRegistry = JSGDeferredRegister.create(JSGCoreRegistries.POINT_OF_ORIGIN_TYPE, modId);
            toRegister(pointOfOriginTypeRegistry);
        }
        return pointOfOriginTypeRegistry;
    }

    public JSGDeferredRegister<Raycaster> raycaster() {
        if (raycasterRegistry == null) {
            raycasterRegistry = JSGDeferredRegister.create(JSGCoreRegistries.RAYCASTER, modId);
            toRegister(raycasterRegistry);
        }
        return raycasterRegistry;
    }

    protected JSGDeferredRegister<Item> itemRegistry;
    protected JSGDeferredRegister<Block> blockRegistry;
    protected JSGDeferredRegister<CreativeModeTab> tabRegistry;
    protected JSGDeferredRegister<BlockEntityType<?>> beRegistry;
    protected JSGDeferredRegister<SoundEvent> soundRegistry;
    protected JSGDeferredRegister<EntityType<?>> entityRegistry;
    protected JSGDeferredRegister<VillagerProfession> villagerProfessionRegistry;
    protected JSGDeferredRegister<VillagerType> villagerTypeRegistry;
    protected JSGDeferredRegister<PoiType> villagerPoiRegistry;
    protected JSGDeferredRegister<StructureType<?>> structureTypeRegistry;
    protected JSGDeferredRegister<Feature<?>> featureRegistry;
    protected JSGDeferredRegister<MenuType<?>> menuTypeRegistry;
    protected JSGDeferredRegister<Fluid> fluidRegistry;
    protected JSGDeferredRegister<FluidType> fluidTypeRegistry;
    protected JSGDeferredRegister<ScheduledTaskType> scheduledTaskTypeRegistry;
    protected JSGDeferredRegister<BiomeOverlayInstance> biomeOverlayRegistry;
    protected JSGDeferredRegister<NotebookPageType<?>> notebookPageTypeRegistry;
    protected JSGDeferredRegister<SymbolType<?>> symbolTypeRegistry;
    protected JSGDeferredRegister<SymbolUsage> symbolUsageRegistry;
    protected JSGDeferredRegister<StateType> stateTypeRegistry;
    protected JSGDeferredRegister<IPointOfOriginType> pointOfOriginTypeRegistry;
    protected JSGDeferredRegister<Raycaster> raycasterRegistry;


    // STATIC
    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> beSupplier(BlockEntityType.BlockEntitySupplier<T> beSupplier, Supplier<? extends Block> blockSupplier) {
        return beSupplier(beSupplier, List.of(blockSupplier));
    }

    @SuppressWarnings("all")
    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> beSupplier(BlockEntityType.BlockEntitySupplier<T> beSupplier, List<? extends Supplier<? extends Block>> blockSuppliers) {
        return () -> {
            List<Block> blocks = new ArrayList<>();
            for (var object : blockSuppliers) {
                blocks.add(object.get());
            }
            return BlockEntityType.Builder.of(beSupplier, blocks.toArray(new Block[0])).build(null);
        };
    }

    public static <T extends AbstractContainerMenu> Supplier<MenuType<T>> menu(IContainerFactory<T> factory) {
        return () -> new MenuType<>(factory, FeatureFlags.DEFAULT_FLAGS);
    }

    private static net.neoforged.neoforge.client.event.RegisterMenuScreensEvent currentMenuScreensEvent;

    @OnlyIn(Dist.CLIENT)
    public static <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void bindScreenToMenu(MenuType<M> menu, JSGScreenConstructor<M, S> screenConstructor) {
        if (currentMenuScreensEvent == null)
            throw new IllegalStateException("bindScreenToMenu must be called from the guiRegister runnable");
        currentMenuScreensEvent.register(menu, screenConstructor);
    }

    @OnlyIn(Dist.CLIENT)
    public interface JSGScreenConstructor<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> extends MenuScreens.ScreenConstructor<T, U> {
        U apply(T pMenu, Inventory pInventory, Component pTitle);

        default U create(T pMenu, Inventory pInventory, Component pTitle) {
            return apply(pMenu, pInventory, pTitle);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static <T extends Entity> void registerEntityRenderer(EntityType<T> entityType, EntityRendererProvider<T> pProvider) {
        EntityRenderers.register(entityType, pProvider);
    }

    public record BlockEntityRendererPair<BE extends BlockEntity>(BlockEntityType<? extends BE> blockEntityType,
                                                                  BlockEntityRendererProvider<BE> rendererProvider) {

        @OnlyIn(Dist.CLIENT)
        public void register(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(blockEntityType, rendererProvider);
        }
    }
}

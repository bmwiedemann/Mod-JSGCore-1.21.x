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
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

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
    protected List<DeferredRegister<?>> registerList = new ArrayList<>();

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

        eventBus.addListener(EventPriority.NORMAL, false, FMLClientSetupEvent.class, event -> event.enqueueWork(() -> {
            menuScreensRegisterRun.run();
            entityRendererRegisterRun.run();
        }));

        eventBus.addListener(EventPriority.NORMAL, false, EntityRenderersEvent.RegisterRenderers.class, event -> blockEntityRenderers.get().forEach(r -> r.register(event)));

        registerList.forEach(r -> r.register(eventBus));
    }

    public void toRegister(DeferredRegister<?> deferredRegister) {
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

    public DeferredRegister<FluidType> fluidType() {
        if (fluidTypeRegistry == null) {
            fluidTypeRegistry = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, modId);
            toRegister(fluidTypeRegistry);
        }
        return fluidTypeRegistry;
    }

    public DeferredRegister<Fluid> fluid() {
        if (fluidRegistry == null) {
            fluidRegistry = DeferredRegister.create(ForgeRegistries.FLUIDS, modId);
            toRegister(fluidRegistry);
        }
        return fluidRegistry;
    }

    public DeferredRegister<PoiType> poi() {
        if (villagerPoiRegistry == null) {
            villagerPoiRegistry = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, modId);
            toRegister(villagerPoiRegistry);
        }
        return villagerPoiRegistry;
    }

    public DeferredRegister<VillagerType> villagerType() {
        if (villagerTypeRegistry == null) {
            villagerTypeRegistry = DeferredRegister.create(Registries.VILLAGER_TYPE, modId);
            toRegister(villagerTypeRegistry);
        }
        return villagerTypeRegistry;
    }

    public DeferredRegister<VillagerProfession> villagerProfession() {
        if (villagerProfessionRegistry == null) {
            villagerProfessionRegistry = DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, modId);
            toRegister(villagerProfessionRegistry);
        }
        return villagerProfessionRegistry;
    }

    public DeferredRegister<EntityType<?>> entity() {
        if (entityRegistry == null) {
            entityRegistry = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, modId);
            toRegister(entityRegistry);
        }
        return entityRegistry;
    }

    public DeferredRegister<StructureType<?>> structureType() {
        if (structureTypeRegistry == null) {
            structureTypeRegistry = DeferredRegister.create(Registries.STRUCTURE_TYPE, modId);
            toRegister(structureTypeRegistry);
        }
        return structureTypeRegistry;
    }

    public DeferredRegister<Feature<?>> feature() {
        if (featureRegistry == null) {
            featureRegistry = DeferredRegister.create(ForgeRegistries.FEATURES, modId);
            toRegister(featureRegistry);
        }
        return featureRegistry;
    }

    public DeferredRegister<MenuType<?>> menu() {
        if (menuTypeRegistry == null) {
            menuTypeRegistry = DeferredRegister.create(ForgeRegistries.MENU_TYPES, modId);
            toRegister(menuTypeRegistry);
        }
        return menuTypeRegistry;
    }

    public DeferredRegister<StateType> state() {
        if (stateTypeRegistry == null) {
            stateTypeRegistry = DeferredRegister.create(JSGCoreRegistries.STATE_TYPE, modId);
            toRegister(stateTypeRegistry);
        }
        return stateTypeRegistry;
    }

    public DeferredRegister<SymbolUsage> symbolUsage() {
        if (symbolUsageRegistry == null) {
            symbolUsageRegistry = DeferredRegister.create(JSGCoreRegistries.SYMBOL_USAGE, modId);
            toRegister(symbolUsageRegistry);
        }
        return symbolUsageRegistry;
    }

    public DeferredRegister<SymbolType<?>> symbolType() {
        if (symbolTypeRegistry == null) {
            symbolTypeRegistry = DeferredRegister.create(JSGCoreRegistries.SYMBOL_TYPE, modId);
            toRegister(symbolTypeRegistry);
        }
        return symbolTypeRegistry;
    }

    public DeferredRegister<SoundEvent> sound() {
        if (soundRegistry == null) {
            soundRegistry = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, modId);
            toRegister(soundRegistry);
        }
        return soundRegistry;
    }

    public DeferredRegister<CreativeModeTab> tab() {
        if (tabRegistry == null) {
            tabRegistry = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, modId);
            toRegister(tabRegistry);
        }
        return tabRegistry;
    }

    public DeferredRegister<BlockEntityType<?>> be() {
        if (beRegistry == null) {
            beRegistry = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, modId);
            toRegister(beRegistry);
        }
        return beRegistry;
    }

    public DeferredRegister<Block> block() {
        if (blockRegistry == null) {
            blockRegistry = DeferredRegister.create(ForgeRegistries.BLOCKS, modId);
            toRegister(blockRegistry);
        }
        return blockRegistry;
    }

    public DeferredRegister<Item> item() {
        if (itemRegistry == null) {
            itemRegistry = DeferredRegister.create(ForgeRegistries.ITEMS, modId);
            toRegister(itemRegistry);
        }
        return itemRegistry;
    }

    public DeferredRegister<ScheduledTaskType> scheduledTask() {
        if (scheduledTaskTypeRegistry == null) {
            scheduledTaskTypeRegistry = DeferredRegister.create(JSGCoreRegistries.SCHEDULED_TASK_TYPE, modId);
            toRegister(scheduledTaskTypeRegistry);
        }
        return scheduledTaskTypeRegistry;
    }

    public DeferredRegister<BiomeOverlayInstance> biomeOverlay() {
        if (biomeOverlayRegistry == null) {
            biomeOverlayRegistry = DeferredRegister.create(JSGCoreRegistries.BIOME_OVERLAY, modId);
            toRegister(biomeOverlayRegistry);
        }
        return biomeOverlayRegistry;
    }

    public DeferredRegister<NotebookPageType<?>> notebookPage() {
        if (notebookPageTypeRegistry == null) {
            notebookPageTypeRegistry = DeferredRegister.create(JSGCoreRegistries.NOTEBOOK_PAGE_TYPE, modId);
            toRegister(notebookPageTypeRegistry);
        }
        return notebookPageTypeRegistry;
    }

    public DeferredRegister<IPointOfOriginType> pooType() {
        if (pointOfOriginTypeRegistry == null) {
            pointOfOriginTypeRegistry = DeferredRegister.create(JSGCoreRegistries.POINT_OF_ORIGIN_TYPE, modId);
            toRegister(pointOfOriginTypeRegistry);
        }
        return pointOfOriginTypeRegistry;
    }

    public DeferredRegister<Raycaster> raycaster() {
        if (raycasterRegistry == null) {
            raycasterRegistry = DeferredRegister.create(JSGCoreRegistries.RAYCASTER, modId);
            toRegister(raycasterRegistry);
        }
        return raycasterRegistry;
    }

    protected DeferredRegister<Item> itemRegistry;
    protected DeferredRegister<Block> blockRegistry;
    protected DeferredRegister<CreativeModeTab> tabRegistry;
    protected DeferredRegister<BlockEntityType<?>> beRegistry;
    protected DeferredRegister<SoundEvent> soundRegistry;
    protected DeferredRegister<EntityType<?>> entityRegistry;
    protected DeferredRegister<VillagerProfession> villagerProfessionRegistry;
    protected DeferredRegister<VillagerType> villagerTypeRegistry;
    protected DeferredRegister<PoiType> villagerPoiRegistry;
    protected DeferredRegister<StructureType<?>> structureTypeRegistry;
    protected DeferredRegister<Feature<?>> featureRegistry;
    protected DeferredRegister<MenuType<?>> menuTypeRegistry;
    protected DeferredRegister<Fluid> fluidRegistry;
    protected DeferredRegister<FluidType> fluidTypeRegistry;
    protected DeferredRegister<ScheduledTaskType> scheduledTaskTypeRegistry;
    protected DeferredRegister<BiomeOverlayInstance> biomeOverlayRegistry;
    protected DeferredRegister<NotebookPageType<?>> notebookPageTypeRegistry;
    protected DeferredRegister<SymbolType<?>> symbolTypeRegistry;
    protected DeferredRegister<SymbolUsage> symbolUsageRegistry;
    protected DeferredRegister<StateType> stateTypeRegistry;
    protected DeferredRegister<IPointOfOriginType> pointOfOriginTypeRegistry;
    protected DeferredRegister<Raycaster> raycasterRegistry;


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

    @OnlyIn(Dist.CLIENT)
    public static <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void bindScreenToMenu(MenuType<M> menu, JSGScreenConstructor<M, S> screenConstructor) {
        MenuScreens.register(menu, screenConstructor);
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

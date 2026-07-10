package dev.tauri.jsg.core.common.registry;

import net.neoforged.fml.common.EventBusSubscriber;
import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.block.cartouche.CartoucheType;
import dev.tauri.jsg.core.common.integration.Integrations;
import dev.tauri.jsg.core.common.registry.helper.TabBuilder;
import dev.tauri.jsg.core.common.registry.helper.builder.block.OreBlockVariant;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import dev.tauri.jsg.core.common.registry.RegistryObject;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@EventBusSubscriber(modid = JSGCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CoreTabs {
    public static final RegistryObject<CreativeModeTab> TAB_TOOLS = JSGCore.REGISTRY_HELPER.tab().register("tools", TabBuilder.create(JSGMapping.rl(JSGCore.MOD_ID, "tools")).build());
    public static final RegistryObject<CreativeModeTab> TAB_ENERGY = JSGCore.REGISTRY_HELPER.tab().register("energy", TabBuilder.create(JSGMapping.rl(JSGCore.MOD_ID, "energy"))
            .withIcon(() -> CoreItems.CRYSTAL_ENERGY_CREATIVE).build());
    public static final RegistryObject<CreativeModeTab> TAB_RESOURCES = JSGCore.REGISTRY_HELPER.tab().register("resources", TabBuilder.create(JSGMapping.rl(JSGCore.MOD_ID, "resources"))
            .withIcon(() -> CoreItems.NAQUADAH_ORE_RAW)
            .withIcon(() -> CoreItems.NAQUADAH_ALLOY_RAW)
            .withIcon(() -> CoreItems.NAQUADAH_RAW_NUGGET)
            .withIcon(() -> CoreItems.NAQUADAH_RAW_DUST)
            .withIcon(() -> CoreItems.PLATE_NAQUADAH_RAW)
            .withIcon(() -> CoreItems.GEAR_NAQUADAH_RAW)
            .build());
    public static final RegistryObject<CreativeModeTab> TAB_BUILDING_BLOCKS = JSGCore.REGISTRY_HELPER.tab().register("building_blocks", TabBuilder.create(JSGMapping.rl(JSGCore.MOD_ID, "building_blocks"))
            .withIcons(() -> CoreBlocks.CRYSTAL_BUDDING.get(OreBlockVariant.STONE).values().stream().map(RegistryObject::get).toList())
            .build());
    public static final RegistryObject<CreativeModeTab> TAB_UPGRADES = JSGCore.REGISTRY_HELPER.tab().register("upgrades", TabBuilder.create(JSGMapping.rl(JSGCore.MOD_ID, "upgrades")).build());
    public static final RegistryObject<CreativeModeTab> TAB_FLUIDS = JSGCore.REGISTRY_HELPER.tab().register("fluids", TabBuilder.create(JSGMapping.rl(JSGCore.MOD_ID, "fluids"))
            .withIcon(() -> CoreFluids.MOLTEN_NAQUADAH_REFINED.bucket).build());
    public static final RegistryObject<CreativeModeTab> TAB_CARTOUCHES = JSGCore.REGISTRY_HELPER.tab().register("cartouches", TabBuilder.create(
                    JSGMapping.rl(JSGCore.MOD_ID, "cartouches"))
            .withIcon(() -> CoreBlocks.CARTOUCHES.get("stone").get(CartoucheType.SEVEN_POO)).build());

    private static final AtomicReference<RegistryObject<CreativeModeTab>> TAB_TRANSPORTATION_HOLDER = new AtomicReference<>(null);
    @SuppressWarnings("unused")
    public static final Supplier<RegistryObject<CreativeModeTab>> TAB_TRANSPORTATION = TAB_TRANSPORTATION_HOLDER::get;

    public static RegistryObject<CreativeModeTab> TAB_INTEGRATIONS;

    // TODO(Mine): investigate whether or not make this publicly accessible to register from other mods?
    private static void registerIntegrationsTab() {
        if (TAB_INTEGRATIONS != null) return;
        TAB_INTEGRATIONS = JSGCore.REGISTRY_HELPER.tab().register("integrations", TabBuilder.create(JSGMapping.rl(JSGCore.MOD_ID, "integrations"))
                .withIcon(() -> CoreItems.GEAR_TITANIUM).build());
    }

    @SuppressWarnings("unused")
    public static void registerTransportationTab(Supplier<RegistryObject<? extends ItemLike>> icon) {
        synchronized (TAB_TRANSPORTATION_HOLDER) {
            if (TAB_TRANSPORTATION_HOLDER.get() != null) return;
            TAB_TRANSPORTATION_HOLDER.set(JSGCore.REGISTRY_HELPER.tab().register("transportation", TabBuilder.create(JSGMapping.rl(JSGCore.MOD_ID, "transportation"))
                    .withIcon(icon).build()));
        }
    }

    @SubscribeEvent
    public static void buildTabsContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(CoreItems.FOOD_LEMON);
        }
    }

    public static void init() {
        Integrations.CREATE.addOnLoad(CoreTabs::registerIntegrationsTab);
        Integrations.TCONSTRUCT.addOnLoad(CoreTabs::registerIntegrationsTab);
    }
}

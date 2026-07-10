package dev.tauri.jsg.core;

import dev.tauri.jsg.core.client.CoreLoadersHolder;
import dev.tauri.jsg.core.client.LoadersHolder;
import dev.tauri.jsg.core.client.loader.model.ModelLoader;
import dev.tauri.jsg.core.client.loader.texture.TextureLoader;
import dev.tauri.jsg.core.client.model.JSGBlockModel;
import dev.tauri.jsg.core.common.advancement.JSGCriterion;
import dev.tauri.jsg.core.common.config.json.dimension.JSGDimensionConfig;
import dev.tauri.jsg.core.common.event.config.DimensionConfigPreRegisterEvent;
import dev.tauri.jsg.core.common.integration.InternalIntegrations;
import dev.tauri.jsg.core.common.integration.cctweaked.CCIntegrationWrapper;
import dev.tauri.jsg.core.common.integration.oc2.OCIntegrationWrapper;
import dev.tauri.jsg.core.common.integration.oculus.OculusAPIWrapper;
import dev.tauri.jsg.core.common.loader.PointOfOriginsLoader;
import dev.tauri.jsg.core.common.loot.LootTableInjector;
import dev.tauri.jsg.core.common.packet.JSGCorePacketHandler;
import dev.tauri.jsg.core.common.registry.CoreFluids;
import dev.tauri.jsg.core.common.registry.JSGCoreRegistries;
import dev.tauri.jsg.core.common.registry.JSGCoreRegistriesInit;
import dev.tauri.jsg.core.common.registry.helper.RegistryHelper;
import dev.tauri.jsg.core.common.worldgen.TemplatePoolInjector;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static net.minecraftforge.eventbus.api.EventPriority.HIGHEST;

@Mod(value = JSGCore.MOD_ID)
public class JSGCore {
    public static final String MOD_ID = "jsg_core";
    public static final String MOD_NAME = "JSG: Core";
    public static String MOD_VERSION = "";
    public static String MOD_VERSION_ONLY = "";
    public static final String MC_VERSION = "1.20.1";
    public static final LoggerWrapper logger = new LoggerWrapper("[" + MOD_ID + "] ", LoggerFactory.getLogger(MOD_NAME));
    public static File modConfigDir;
    public static File modsDirectory;
    public static File clientModPath;
    public static MinecraftServer currentServer = null;
    public static BlockPos lastPlayerPosInWorld = new BlockPos(0, 0, 0);
    public static final Supplier<BiFunction<String, Class<?>, LoadersHolder>> loadersHolderGetter = () -> (modId, mainModClass) -> new LoadersHolder(modId, new TextureLoader(modId, mainModClass), new ModelLoader(modId, mainModClass));

    public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MOD_ID);

    public static final String OC_WRAPPER_LOADED = "dev.tauri.jsg.core.common.integration.oc2.OCIntegrationLoaded";
    public static final String OC_WRAPPER_NOT_LOADED = "dev.tauri.jsg.core.common.integration.oc2.OCIntegrationNotLoaded";
    public static OCIntegrationWrapper ocWrapper;

    public static final String CC_WRAPPER_LOADED = "dev.tauri.jsg.core.common.integration.cctweaked.CCIntegrationLoaded";
    public static final String CC_WRAPPER_NOT_LOADED = "dev.tauri.jsg.core.common.integration.cctweaked.CCIntegrationNotLoaded";
    public static CCIntegrationWrapper ccWrapper;

    public static final String OCULUS_WRAPPER_LOADED = "dev.tauri.jsg.core.common.integration.oculus.OculusAPIWrapperLoaded";
    public static final String OCULUS_WRAPPER_NOT_LOADED = "dev.tauri.jsg.core.common.integration.oculus.OculusAPIWrapperNotLoaded";
    public static OculusAPIWrapper oculusWrapper;

    @SuppressWarnings("unused")
    public static Component getInProgress() {
        return Component.literal(ChatFormatting.AQUA + "Work In Progress Item!");
    }

    public JSGCore() {
        ModList.get().getModContainerById(MOD_ID).ifPresentOrElse(container -> {
            MOD_VERSION_ONLY = container.getModInfo().getVersion().getQualifier();
            MOD_VERSION = MC_VERSION + "-" + MOD_VERSION_ONLY;
            clientModPath = container.getModInfo().getOwningFile().getFile().getFilePath().toFile();
        }, () -> {
        });
        modConfigDir = FMLPaths.CONFIGDIR.get().toFile();
        modsDirectory = FMLPaths.MODSDIR.get().toFile();
        PointOfOriginsLoader.INSTANCE.setConfigFolder(modConfigDir);

        JSGCorePacketHandler.init();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        JSGCoreRegistries.init();
        JSGCoreRegistries.register(modEventBus);
        JSGCoreRegistriesInit.init();
        JSGCoreRegistriesInit.register(modEventBus);

        InternalIntegrations.tryLoad();

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        PointOfOriginsLoader.INSTANCE.loadServer();
        JSGAddons.onCoreCommonSetup();
        JSGCriterion.registerInternally();
        CoreFluids.registerCauldrons();
    }

    @SubscribeEvent
    public void onServerBeginStarting(ServerAboutToStartEvent event) {
        new DimensionConfigPreRegisterEvent(JSGDimensionConfig.INSTANCE).post();
        JSGDimensionConfig.INSTANCE.load(JSGCore.modConfigDir);
        TemplatePoolInjector.inject(event.getServer());
        LootTableInjector.inject(event.getServer());
    }

    @SubscribeEvent(priority = HIGHEST)
    public void onServerStarting(ServerStartingEvent event) {
        currentServer = event.getServer();
    }

    @SubscribeEvent
    public void serverStarted(ServerStartedEvent event) throws IOException {
        JSGDimensionConfig.INSTANCE.reload(event.getServer());
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            CoreLoadersHolder.init();
        }

        @SubscribeEvent
        public static void onLoadersRegister(ModelEvent.RegisterGeometryLoaders event) {
            event.register("handheld_item_model", JSGBlockModel.Loader.INSTANCE);
        }
    }
}

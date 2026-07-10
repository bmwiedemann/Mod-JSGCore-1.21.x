package dev.tauri.jsg.core.common.integration;

import dev.tauri.jsg.core.JSGCore;
import dev.tauri.jsg.core.common.integration.cctweaked.CCIntegrationWrapper;
import dev.tauri.jsg.core.common.integration.oc2.OCIntegrationWrapper;
import dev.tauri.jsg.core.common.integration.oculus.OculusAPIWrapper;
import dev.tauri.jsg.core.common.integration.tconstruct.TConstructIntegration;
import net.minecraftforge.fml.ModList;

public class InternalIntegrations {
    static {
        // OC
        Integrations.OC2.addOnLoad(() -> JSGCore.ocWrapper = (OCIntegrationWrapper) Class.forName(JSGCore.OC_WRAPPER_LOADED).getConstructor().newInstance())
                .addOnNotLoaded(() -> JSGCore.ocWrapper = (OCIntegrationWrapper) Class.forName(JSGCore.OC_WRAPPER_NOT_LOADED).getConstructor().newInstance());
        // CC
        Integrations.CCT.addOnLoad(() -> JSGCore.ccWrapper = (CCIntegrationWrapper) Class.forName(JSGCore.CC_WRAPPER_LOADED).getConstructor().newInstance())
                .addOnNotLoaded(() -> JSGCore.ccWrapper = (CCIntegrationWrapper) Class.forName(JSGCore.CC_WRAPPER_NOT_LOADED).getConstructor().newInstance());
        // TConstruct
        Integrations.TCONSTRUCT.addOnLoad(TConstructIntegration::load);
        // Oculus
        Integrations.OCULUS.addOnLoad(() -> JSGCore.oculusWrapper = (OculusAPIWrapper) Class.forName(JSGCore.OCULUS_WRAPPER_LOADED).getConstructor().newInstance())
                .addOnNotLoaded(() -> JSGCore.oculusWrapper = (OculusAPIWrapper) Class.forName(JSGCore.OCULUS_WRAPPER_NOT_LOADED).getConstructor().newInstance());
    }

    public static void tryLoad() {
        for (var i : Integrations.values()) {
            try {
                if (i.modNames.stream().anyMatch((name) -> ModList.get().isLoaded(name))) {
                    JSGCore.logger.info("{} found and connection is enabled... Connecting...", i.name);
                    i.isLoaded = true;
                    for (var t : i.onLoad)
                        t.run();
                    JSGCore.logger.info("Successfully connected into {}!", i.name);
                } else {
                    i.isLoaded = false;
                    for (var t : i.onNotLoaded)
                        t.run();
                }
            } catch (Exception e) {
                JSGCore.logger.error("Exception loading {} wrapper", i.name, e);
            }
        }
    }
}

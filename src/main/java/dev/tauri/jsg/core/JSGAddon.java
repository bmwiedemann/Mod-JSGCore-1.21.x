package dev.tauri.jsg.core;

import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface JSGAddon {
    @Deprecated
    default String getName(){
        return JSGAddons.getInfo(this).get(JSGAddons.AddonInfo.NAME);
    }

    String getId();

    @Nullable
    default String getAuthors(){
        return null;
    }

    @Deprecated
    default String getVersion() {
        return JSGAddons.getInfo(this).get(JSGAddons.AddonInfo.VERSION);
    }

    default String[] getWelcomeLogo() {
        return new String[0];
    }

    default void onJSGCoreLoad() {
    }

    default Optional<LoggerWrapper> getLoggerWrapper() {
        return Optional.empty();
    }
}

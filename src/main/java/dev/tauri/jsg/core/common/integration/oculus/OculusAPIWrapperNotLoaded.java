package dev.tauri.jsg.core.common.integration.oculus;

public class OculusAPIWrapperNotLoaded implements OculusAPIWrapper {
    @Override
    public boolean isShaderPackActive() {
        return false;
    }
}

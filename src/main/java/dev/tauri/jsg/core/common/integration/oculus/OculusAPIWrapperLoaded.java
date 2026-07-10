package dev.tauri.jsg.core.common.integration.oculus;

import net.irisshaders.iris.api.v0.IrisApi;

public class OculusAPIWrapperLoaded implements OculusAPIWrapper {
    @Override
    public boolean isShaderPackActive() {
        return IrisApi.getInstance().isShaderPackInUse();
    }
}

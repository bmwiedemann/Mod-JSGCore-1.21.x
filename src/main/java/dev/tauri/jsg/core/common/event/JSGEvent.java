package dev.tauri.jsg.core.common.event;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public abstract class JSGEvent extends Event {

    /**
     * Post event to MinecraftForge.EVENT_BUS. Internal use only
     *
     * @return true if event canceled, false if not
     */
    public boolean post() {
        return MinecraftForge.EVENT_BUS.post(this);
    }
}

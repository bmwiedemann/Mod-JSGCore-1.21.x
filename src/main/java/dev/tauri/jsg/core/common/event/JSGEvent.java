package dev.tauri.jsg.core.common.event;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.Event;

public abstract class JSGEvent extends Event {

    /**
     * Post event to NeoForge.EVENT_BUS. Internal use only
     *
     * @return true if event canceled, false if not
     */
    public boolean post() {
        NeoForge.EVENT_BUS.post(this);
        return this instanceof net.neoforged.bus.api.ICancellableEvent cancellable && cancellable.isCanceled();
    }
}

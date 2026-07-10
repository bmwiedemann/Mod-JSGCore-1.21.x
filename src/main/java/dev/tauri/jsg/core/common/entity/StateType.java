package dev.tauri.jsg.core.common.entity;

import dev.tauri.jsg.core.common.registry.JSGCoreRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public record StateType(String name) {
    public static StateType byId(ResourceLocation id) {
        return JSGCoreRegistries.R_STATE_TYPE.get().get(id);
    }

    public ResourceLocation getId() {
        return JSGCoreRegistries.R_STATE_TYPE.get().getKey(this);
    }

    @Override
    public @NonNull String toString() {
        return name();
    }

    public StateExecutor stateExecutor() {
        return new StateExecutor(this);
    }

    public StateSupplier stateSupplier() {
        return new StateSupplier(this);
    }

    public static class StateSupplier {
        private Supplier<State> result = null;
        private final StateType original;

        public StateSupplier(StateType original) {
            this.original = original;
        }

        @Nullable
        public State get() {
            return result.get();
        }

        public State orElseGet(Supplier<State> defaultSupplier) {
            if (result == null) return defaultSupplier.get();
            return result.get();
        }

        public State orElseThrow(Object caller) {
            if (result == null)
                throw new UnsupportedOperationException("EnumStateType." + original.name() + " not implemented on " + caller.getClass().getName());
            return result.get();
        }

        public StateSupplier tryType(Supplier<StateType> otherStateType, Supplier<State> stateSupplier) {
            return tryType(otherStateType.get(), stateSupplier);
        }

        public StateSupplier tryType(StateType otherStateType, Supplier<State> stateSupplier) {
            if (original.equals(otherStateType)) result = stateSupplier;
            return this;
        }
    }

    public static class StateExecutor {
        private Runnable result = null;
        private final StateType original;

        public StateExecutor(StateType original) {
            this.original = original;
        }

        public void run() {
            if (result != null)
                result.run();
        }

        public void runOrElse(Runnable defaultRunnable) {
            if (result == null) defaultRunnable.run();
            else run();
        }

        public StateExecutor tryType(Supplier<StateType> otherStateType, Runnable stateRunnable) {
            return tryType(otherStateType.get(), stateRunnable);
        }

        public StateExecutor tryType(StateType otherStateType, Runnable stateRunnable) {
            if (original.equals(otherStateType)) result = stateRunnable;
            return this;
        }
    }
}

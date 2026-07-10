package dev.tauri.jsg.core.client.renderer;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

@SuppressWarnings("unused")
public abstract class Activation<K> implements INBTSerializable<CompoundTag> {

    /**
     * Texture index on the list.
     * <p>
     * Previously "activation".
     */
    protected final K textureKey;

    /**
     * When the {@link Activation} was created.
     * <p>
     * Previously "activationStateChange".
     */
    public long stateChange;

    /**
     * Are we dimming?
     */
    public boolean dim;

    /**
     * {@link ActivationState} containing texture of the {@link Activation#textureKey} and removal state.
     */
    private final ActivationState state;

    /**
     * Is this {@link Activation} actively called from the render loop?
     */
    private boolean active;

    /**
     * Main constructor
     *
     * @param textureKey  Index on the texture list.
     * @param stateChange When the {@link Activation} was created.
     * @param dim         Are we dimming?
     */
    public Activation(K textureKey, long stateChange, boolean dim) {
        this.textureKey = textureKey;
        this.stateChange = stateChange;
        this.dim = dim;

        state = new ActivationState(dim ? getMaxStage() : 0);
        active = true;
    }

    public Activation(K textureKey, CompoundTag compoundTag) {
        this.textureKey = textureKey;
        deserializeNBT(compoundTag);
        state = new ActivationState(dim ? getMaxStage() : 0);
    }

    public Activation(K textureKey, ByteBuf buf) {
        this.textureKey = textureKey;
        fromBytes(buf);
        state = new ActivationState(dim ? getMaxStage() : 0);
    }

    /**
     * Get max activation stage inclusive.
     *
     * @return Max activation stage.
     */
    protected abstract float getMaxStage();

    /**
     * Get tick multiplier
     *
     * @return Tick multiplier.
     */
    protected abstract float getTickMultiplier();

    /**
     * Mark this {@link Activation} inactive.
     * Prevents {@link Activation#activate(long, double)} from being called in the render loop.
     *
     * @return This instance.
     */
    public Activation<K> inactive() {
        this.active = false;

        return this;
    }

    /**
     * Mark this {@link Activation} active.
     *
     * @return This instance.<br>
     * see {@link Activation#inactive()} .
     */
    public Activation<K> active() {
        this.active = true;

        return this;
    }

    /**
     * Getter for active
     *
     * @return active state.
     */
    public boolean isActive() {
        return active;
    }

    public float getFinalState() {
        if (dim)
            return 0;
        return getMaxStage();
    }

    public ActivationState activate(long worldTicks, double partialTicks) {
        double stage = (worldTicks - stateChange + partialTicks) * getTickMultiplier();

        if (stage >= 0) {

            if (stage <= getMaxStage()) {
                if (dim)
                    stage = getMaxStage() - stage;

                state.stage = (float) stage;
            } else {
                onActivated();

                state.stage = (dim ? 0 : getMaxStage());
                state.remove = true;
            }
        }

        return state;
    }

    /**
     * Called on stage exceeding {@link Activation#getMaxStage()}
     */
    protected void onActivated() {
    }

    public static class ActivationState {
        public float stage;
        public boolean remove;

        public ActivationState(float stage) {
            this.stage = stage;
            this.remove = false;
        }
    }

    /**
     * SAM interface used by {@link Activation#iterate(List, long, double, IActivationCallback)}.
     *
     * @author MrJake222
     */
    public interface IActivationCallback<K> {
        void run(K textureKey, float stage);
    }

    public static <K> void iterate(List<Activation<K>> activationList, long ticks, double partialTicks, IActivationCallback<K> callback) {
        for (int i = 0; i < activationList.size(); ) {
            Activation<K> activation = activationList.get(i);

            if (activation.isActive()) {
                ActivationState activationState = activation.activate(ticks, partialTicks);

                callback.run(activation.textureKey, activationState.stage);

                if (activationState.remove) {
                    activationList.remove(activation);
                } else i++;
            } else i++;
        }
    }

    // Eclipse generated methods
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + textureKey.hashCode();
        result = prime * result + (dim ? 1231 : 1237);
        result = prime * result + (int) (stateChange ^ (stateChange >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("unchecked")
        Activation<K> other = (Activation<K>) obj;
        if (!textureKey.equals(other.textureKey))
            return false;
        if (dim != other.dim)
            return false;
        return stateChange == other.stateChange;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeLong(stateChange);
        buf.writeBoolean(dim);
        buf.writeBoolean(active);
    }

    public void fromBytes(ByteBuf buf) {
        stateChange = buf.readLong();
        dim = buf.readBoolean();
        active = buf.readBoolean();
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();
        compound.putLong("stateChanged", stateChange);
        compound.putBoolean("dim", dim);
        compound.putBoolean("active", active);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        stateChange = compound.getLong("stateChange");
        dim = compound.getBoolean("dim");
        active = compound.getBoolean("active");
    }
}

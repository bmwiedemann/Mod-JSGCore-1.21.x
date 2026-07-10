package dev.tauri.jsg.core.common.blockentity;

import dev.tauri.jsg.core.common.packet.TargetPoint;
import dev.tauri.jsg.core.client.renderer.blockentity.cartouche.CartoucheRenderer;
import dev.tauri.jsg.core.client.renderer.blockentity.cartouche.CartoucheRendererState;
import dev.tauri.jsg.core.common.block.cartouche.CartoucheBlock;
import dev.tauri.jsg.core.common.entity.IAddressNotebookPageData;
import dev.tauri.jsg.core.common.entity.NotebookPageType;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.entity.StateType;
import dev.tauri.jsg.core.common.item.notebook.PageNotebookItemFilled;
import dev.tauri.jsg.core.common.packet.JSGCorePacketHandler;
import dev.tauri.jsg.core.common.packet.packets.StateUpdatePacketToClient;
import dev.tauri.jsg.core.common.registry.CoreBlockEntities;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import dev.tauri.jsg.core.common.symbol.address.IAddress;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import dev.tauri.jsg.core.common.util.JSGAxisAlignedBB;
import dev.tauri.jsg.core.common.util.RotationUtil;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Optional;


@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CartoucheBE extends CamouflageBE implements ITickable, BEStateProvider, IPreparable {
    public NotebookPageType.DataWrapper<?> notebookPageDataWrapper;
    public CartoucheRenderer renderer;
    public DyeColor color = DyeColor.BLACK;
    public boolean shiny;
    public CartoucheRendererState renderStateClient;

    protected boolean needRegeneration;
    protected ResourceLocation regenerationForDim;
    protected ResourceLocation regenerationForSymbolType;

    public CartoucheBE(BlockPos pPos, BlockState pBlockState) {
        super(CoreBlockEntities.CARTOUCHE.get(), pPos, pBlockState);
    }

    public void setAddress(NotebookPageType.DataWrapper<?> notebookPageDataWrapper) {
        if (getLevel() == null || getLevel().isClientSide()) return;
        // copy
        this.notebookPageDataWrapper = NotebookPageType.pageDataFromCompound(notebookPageDataWrapper.createCompoundTag());
        if (this.notebookPageDataWrapper != null && this.notebookPageDataWrapper.data() instanceof IAddressNotebookPageData addressNotebookPageData) {
            addressNotebookPageData.setSymbolsToDisplay(processSymbolsToDisplay(addressNotebookPageData.getSymbolsToDisplay()));
        }

        setChanged();
        getAndSendState(CoreStateTypes.RENDERER_STATE.get());
    }

    public ItemStack getNoteBookPage(Level level, BlockPos pos) {
        if (this.notebookPageDataWrapper == null) return ItemStack.EMPTY;
        // copy
        var notebookPageDataWrapper = NotebookPageType.pageDataFromCompound(this.notebookPageDataWrapper.createCompoundTag());
        if (notebookPageDataWrapper != null && notebookPageDataWrapper.data() instanceof IAddressNotebookPageData addressNotebookPageData) {
            addressNotebookPageData.setSymbolsToDisplay(processSymbolsToDisplay(addressNotebookPageData.getSymbolsToDisplay()));
        }
        if (notebookPageDataWrapper == null) return ItemStack.EMPTY;
        return notebookPageDataWrapper.createPage(PageNotebookItemFilled.getBiomeKeyFromWorld(level, pos));
    }

    protected int[] processSymbolsToDisplay(@Nullable int[] symbolsToDisplay) {
        if (symbolsToDisplay == null) return new int[0];
        var block = getBlockState().getBlock();
        if (!(block instanceof CartoucheBlock cartoucheBlock)) return new int[0];
        var type = cartoucheBlock.type;
        return Arrays.stream(symbolsToDisplay).filter(symbolPos -> {
            if (symbolPos > type.symbolsCount && symbolPos != 9) return false;
            return symbolPos != 9 || type.hasPoo;
        }).toArray();
    }

    public void setColor(DyeColor color) {
        this.color = color;
        setChanged();
        getAndSendState(CoreStateTypes.RENDERER_STATE.get());
    }

    public void setShiny(boolean shiny) {
        this.shiny = shiny;
        setChanged();
        getAndSendState(CoreStateTypes.RENDERER_STATE.get());
    }

    public int[] getSymbolsToDisplay() {
        return Optional.ofNullable(getAddressNotebookPageData())
                .map(d -> processSymbolsToDisplay(d.getSymbolsToDisplay()))
                .orElse(new int[0]);
    }

    @Nullable
    public IAddress getAddress() {
        return Optional.ofNullable(getAddressNotebookPageData())
                .map(IAddressNotebookPageData::getAddress)
                .orElse(null);
    }

    @Nullable
    public PointOfOrigin getPointOfOrigin() {
        return Optional.ofNullable(getAddressNotebookPageData())
                .map(IAddressNotebookPageData::getOrigin)
                .orElse(null);
    }

    @Nullable
    public IAddressNotebookPageData getAddressNotebookPageData() {
        if (notebookPageDataWrapper == null || !(notebookPageDataWrapper.data() instanceof IAddressNotebookPageData addressNotebookPageData))
            return null;
        return addressNotebookPageData;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (getLevel() == null) return;
        if (getLevel().isClientSide())
            requestState(CoreStateTypes.RENDERER_STATE.get());
        else {
            getAndSendState(CoreStateTypes.RENDERER_STATE.get());
            tryRegenerate(getLevel());
        }
    }

    protected void tryRegenerate(Level level) {
        if (!needRegeneration) return;
        this.needRegeneration = false;
        this.color = DyeColor.BLACK;

        var random = level.random;
        if (notebookPageDataWrapper == null) return;
        var extra = new CompoundTag();
        if (regenerationForDim != null)
            extra.putString("regenerationForDim", regenerationForDim.toString());
        if (regenerationForSymbolType != null)
            extra.putString("regenerationForSymbolType", regenerationForSymbolType.toString());
        var data = notebookPageDataWrapper.generateData(level, getBlockPos(), random, extra);
        if (data.data() == null) return;
        this.notebookPageDataWrapper = data;
        setChanged();
    }

    public JSGAxisAlignedBB getRenderBoundingBox() {
        return RotationUtil.rotate(new JSGAxisAlignedBB(0, 0, 0, 1, 3, 0.3), RotationUtil.getRotation(getBlockState()), new Vec3(0.5, 0.5, 0.5)).offset(getBlockPos());
    }

    @Override
    public void tick(Level level) {

    }

    @Override
    public State getState(StateType stateType) {
        if (stateType == CoreStateTypes.RENDERER_STATE.get()) {
            return new CartoucheRendererState(notebookPageDataWrapper, color, shiny);
        }
        return super.getState(stateType);
    }

    @Override
    public State createState(StateType stateType) {
        if (stateType == CoreStateTypes.RENDERER_STATE.get()) {
            return new CartoucheRendererState();
        }
        return super.createState(stateType);
    }

    @Override
    public void setState(StateType stateType, State state) {
        if (stateType == CoreStateTypes.RENDERER_STATE.get()) {
            renderStateClient = (CartoucheRendererState) state;
            notebookPageDataWrapper = renderStateClient.dataWrapper;
            color = renderStateClient.color;
            shiny = renderStateClient.shiny;
            setChanged();
        }
        super.setState(stateType, state);
    }

    private TargetPoint targetPoint;

    public TargetPoint getTargetPoint() {
        if (getLevel() == null) return targetPoint;
        if (targetPoint == null) {
            var pos = getBlockPos();
            targetPoint = new TargetPoint(pos.getX(), pos.getY(), pos.getZ(), 512, getLevel().dimension());
        }
        return targetPoint;
    }

    @Override
    public void sendState(StateType type, State state) {
        JSGCorePacketHandler.sendToClient(new StateUpdatePacketToClient(getBlockPos(), type, state), getTargetPoint());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void saveAdditional(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries) {
        compound.putBoolean("needRegeneration", needRegeneration);
        if (regenerationForDim != null)
            compound.putString("regenerationForDim", regenerationForDim.toString());
        if (regenerationForSymbolType != null)
            compound.putString("regenerationForSymbolType", regenerationForSymbolType.toString());
        if (notebookPageDataWrapper != null && notebookPageDataWrapper.data() != null) {
            compound.put("notebookPageData", notebookPageDataWrapper.createCompoundTag());
        }
        compound.putInt("color", color.getId());
        compound.putBoolean("shiny", shiny);
        super.saveAdditional(compound, registries);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void loadAdditional(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries) {
        needRegeneration = compound.getBoolean("needRegeneration");
        if (compound.contains("regenerationForDim"))
            regenerationForDim = JSGMapping.rl(compound.getString("regenerationForDim"));
        if (compound.contains("regenerationForSymbolType"))
            regenerationForSymbolType = JSGMapping.rl(compound.getString("regenerationForSymbolType"));
        if (compound.contains("notebookPageData")) {
            notebookPageDataWrapper = NotebookPageType.pageDataFromCompound(compound.getCompound("notebookPageData"));
        }
        if (compound.contains("color"))
            color = DyeColor.byId(compound.getInt("color"));
        shiny = compound.getBoolean("shiny");
        super.loadAdditional(compound, registries);
    }

    @Override
    public boolean prepareBE() {
        return prepareBE(null);
    }

    @Override
    public boolean prepareBE(@Nullable String arg) {
        needRegeneration = true;
        //this.notebookPageDataWrapper = null;
        if (arg == null) regenerationForDim = null;
        else {
            var splitArgs = arg.split(",");
            regenerationForDim = JSGMapping.rl(splitArgs[0]);
            if (splitArgs.length > 1)
                regenerationForSymbolType = JSGMapping.rl(splitArgs[1]);
            else
                regenerationForSymbolType = null;
        }
        setChanged();
        getAndSendState(CoreStateTypes.RENDERER_STATE.get());
        return true;
    }
}

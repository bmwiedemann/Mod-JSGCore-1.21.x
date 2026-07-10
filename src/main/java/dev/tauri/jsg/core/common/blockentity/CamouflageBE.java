package dev.tauri.jsg.core.common.blockentity;

import dev.tauri.jsg.core.common.block.core.InvisibleBlock;
import dev.tauri.jsg.core.common.entity.State;
import dev.tauri.jsg.core.common.entity.StateType;
import dev.tauri.jsg.core.common.helper.ItemHandlerHelper;
import dev.tauri.jsg.core.common.registry.CoreStateTypes;
import dev.tauri.jsg.core.common.state.CamoState;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

public abstract class CamouflageBE extends JSGBlockEntity implements BEStateProvider {
    private BlockState camoBlock = Blocks.AIR.defaultBlockState();
    private PacketDistributor.TargetPoint targetPointInternal;

    public void onCamoBlockChanged() {
        if (level == null || level.isClientSide) return;
        if (targetPointInternal == null) {
            targetPointInternal = new PacketDistributor.TargetPoint(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 512, level.dimension());
        }
        getAndSendState(CoreStateTypes.CAMO_STATE.get());
    }

    public void dropCamo() {
        if (camoBlock == null || camoBlock.isAir()) return;
        ItemHandlerHelper.spawnItemStack(getLevel(), getBlockPos(), new ItemStack(camoBlock.getBlock()));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null) return;
        if (level.isClientSide)
            requestState(CoreStateTypes.CAMO_STATE.get());
        else
            onCamoBlockChanged();
    }

    public CamouflageBE(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        onCamoBlockChanged();
    }

    public BlockState getCamoBlock() {
        if (camoBlock == null) return Blocks.AIR.defaultBlockState();
        return camoBlock;
    }

    protected boolean canBeUsedAsCamoBlock(BlockState blockState) {
        return true;
    }

    public boolean setCamoBlockByHeldItem(ItemStack stack, Player player, @Nullable BlockPlaceContext ctx) {
        if (stack.isEmpty()) return removeCamoBlockAndDrop(player);
        var item = stack.getItem();
        if (item instanceof BucketItem) return false;
        var block = Block.byItem(stack.getItem());
        if (block == Blocks.AIR) return removeCamoBlockAndDrop(player);
        if (block instanceof FlowerBlock) return false;
        if (block instanceof BannerBlock) return false;
        if (block instanceof InvisibleBlock) return false;
        if (!getCamoBlock().isAir()) return false;
        var blockState = ctx == null ? block.defaultBlockState() : block.getStateForPlacement(ctx);
        if (!canBeUsedAsCamoBlock(blockState)) return false;
        setCamoBlock(blockState);
        if (!player.isCreative())
            stack.shrink(1);
        if (level != null)
            level.playSound(null, getBlockPos(), block.defaultBlockState().getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1, 1);
        return true;
    }

    public boolean removeCamoBlockAndDrop(Player player) {
        if (!camoBlock.isAir()) {
            if (!player.isCreative())
                ItemHandlerHelper.spawnItemStack(level, getBlockPos(), new ItemStack(camoBlock.getBlock()));
            removeCamoBlock();
            if (level != null)
                level.playSound(null, getBlockPos(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1, 1);
            return true;
        }
        return false;
    }

    public void setCamoBlock(BlockState blockState) {
        camoBlock = blockState;
        setChanged();
        onCamoBlockChanged();
    }

    public void removeCamoBlock() {
        camoBlock = Blocks.AIR.defaultBlockState();
        setChanged();
        onCamoBlockChanged();
    }


    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        var camoBlockData = new CompoundTag();
        String[] data = camoBlock.toString().split("\\[");
        camoBlockData.putString("id", data[0].replace("Block{", "").replace("}", ""));
        if (data.length > 1) {
            CompoundTag properties = new CompoundTag();
            Stream.of(data[1].substring(0, data[1].length() - 1).split(",")).forEach(di -> {
                String[] keyValue = di.split("=");
                if (keyValue.length == 2) {
                    properties.putString(keyValue[0].trim(), keyValue[1].trim());
                }
            });
            camoBlockData.put("properties", properties);
        }
        // compound.putInt("camoBlock_id", Block.getId(camoBlock));
        compound.put("camoBlock_data", camoBlockData);
        super.saveAdditional(compound);
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        if (compound.contains("camoBlock_data")) {
            var camoBlockData = compound.getCompound("camoBlock_data");
            Block block = BuiltInRegistries.BLOCK.get(JSGMapping.rl(camoBlockData.getString("id")));
            if (block != null) {
                camoBlock = block.defaultBlockState();
                if (camoBlockData.contains("properties")) {
                    CompoundTag camoProperties = camoBlockData.getCompound("properties");
                    block.defaultBlockState().getProperties().forEach(p -> {
                        if (camoProperties.contains(p.getName()) && p.getValue(camoProperties.getString(p.getName())).isPresent()) {
                            modifyCamoBLock(p, p.getValue(camoProperties.getString(p.getName())));
                        }
                    });
                }
            }
        } else if (compound.contains("camoBlock_id"))
            camoBlock = Block.stateById(compound.getInt("camoBlock_id"));
        super.load(compound);
    }

    @SuppressWarnings("unchecked")
    protected final <T extends Comparable<T>, V extends T> void modifyCamoBLock(Property<?> p, Optional<?> value) {
        if (camoBlock.hasProperty((Property<T>) p) && value.isPresent()) {
            camoBlock = camoBlock.setValue((Property<T>) p, (V) value.get());
        }
    }

    @Override
    public State getState(StateType stateType) {
        if (stateType == CoreStateTypes.CAMO_STATE.get())
            return new CamoState(getCamoBlock());
        return null;
    }

    @Override
    public State createState(StateType stateType) {
        if (stateType == CoreStateTypes.CAMO_STATE.get())
            return new CamoState();
        return null;
    }

    @Override
    public void setState(StateType stateType, State state) {
        if (stateType == CoreStateTypes.CAMO_STATE.get()) {
            setCamoBlock(((CamoState) state).getState());
        }
    }
}

package dev.tauri.jsg.core.common.block.cauldron;

import dev.tauri.jsg.core.common.block.ITickableBEBlock;
import dev.tauri.jsg.core.common.blockentity.FluidCauldronBE;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidType;
import org.jspecify.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JSGFluidCauldron extends LayeredCauldronBlock implements ITickableBEBlock {
    protected final Supplier<FluidType> fluidType;

    public JSGFluidCauldron(Supplier<FluidType> fluidType, Map<Item, CauldronInteraction> interactionMap) {
        super(BlockBehaviour.Properties.copy(Blocks.CAULDRON), (precipitation) -> false, interactionMap);
        this.fluidType = fluidType;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidCauldronBE(pos, state);
    }

    @Override
    public Item asItem() {
        return Items.CAULDRON;
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos pos, Entity entity) {
    }
}

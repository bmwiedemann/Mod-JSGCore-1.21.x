package dev.tauri.jsg.core.common.block.cauldron;

import dev.tauri.jsg.core.common.blockentity.FluidCauldronBE;
import dev.tauri.jsg.core.common.helper.ItemHandlerHelper;
import dev.tauri.jsg.core.common.registry.helper.FluidHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.ForgeCapabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class CauldronRecipe {
    public static final List<CauldronRecipe> RECIPES = new ArrayList<>();
    protected List<Map<Item, CauldronInteraction>> baseFluidInteractionMaps;
    protected boolean requireHeating = false;

    public CauldronRecipe() {
        RECIPES.add(this);
    }

    public static CauldronMixtureRecipe mixture() {
        return CauldronMixtureRecipe.of();
    }

    public static CauldronMeltingRecipe melting() {
        return CauldronMeltingRecipe.of();
    }

    public static CauldronCastingRecipe casting() {
        return CauldronCastingRecipe.of();
    }

    public static CauldronBathingRecipe bathing() {
        return CauldronBathingRecipe.of();
    }

    public CauldronRecipe requireHeating() {
        this.requireHeating = true;
        return this;
    }

    public boolean getRequireHeating() {
        return this.requireHeating;
    }


    public CauldronRecipe setRecipeHandlers(Map<Item, CauldronInteraction> recipeHandlers) {
        this.baseFluidInteractionMaps = List.of(recipeHandlers);
        return this;
    }

    public CauldronRecipe setRecipeHandlers(List<Map<Item, CauldronInteraction>> recipeHandlers) {
        this.baseFluidInteractionMaps = recipeHandlers;
        return this;
    }

    public abstract void insertInteractions();

    public interface ItemResult {
        Supplier<Item> getItemInput();

        Supplier<ItemStack> getItemResult();

        Supplier<FluidStack> getItemBaseFluid();
    }

    public interface FluidResult {
        Supplier<FluidStack> getFluidResult();

        Supplier<Item> getFluidInput();

        Supplier<FluidStack> getFluidBaseFluid();
    }

    /**
     * Holds recipe that converts fluid in cauldron to another fluid
     */
    public static class CauldronMixtureRecipe extends CauldronRecipe implements FluidResult {
        protected Supplier<Item> itemToMelt;
        protected Supplier<BlockState> newCauldronState;
        protected Supplier<Fluid> newFluid = () -> null;
        protected Supplier<Fluid> baseFluid = () -> null;

        public CauldronMixtureRecipe setItemToMelt(Supplier<Item> itemToMelt) {
            this.itemToMelt = itemToMelt;
            return this;
        }

        public CauldronMixtureRecipe setNewCauldronStateSupplier(Supplier<BlockState> newCauldronState) {
            this.newCauldronState = newCauldronState;
            return this;
        }

        public CauldronMixtureRecipe setNewFluid(Supplier<Fluid> newFluid) {
            this.newFluid = newFluid;
            return this;
        }

        public CauldronMixtureRecipe setBaseFluid(Supplier<Fluid> baseFluid) {
            this.baseFluid = baseFluid;
            return this;
        }

        @Override
        public void insertInteractions() {
            baseFluidInteractionMaps.forEach(map -> map.put(itemToMelt.get(), (BlockState pBlockState, Level pLevel, BlockPos pBlockPos, Player pPlayer, InteractionHand pHand, ItemStack pStack) -> {
                if (!pLevel.isClientSide) {
                    if (getRequireHeating() && !FluidCauldronBE.isCauldronHeated(pLevel, pBlockPos))
                        return InteractionResult.FAIL;

                    var oldCauldron = pLevel.getBlockEntity(pBlockPos);
                    FluidStack oldFluid = null;
                    if (oldCauldron != null) {
                        oldFluid = oldCauldron.getCapability(ForgeCapabilities.FLUID_HANDLER).resolve()
                                .filter(handler -> handler.getTanks() > 0)
                                .map(handler -> handler.getFluidInTank(0))
                                .orElse(null);
                    }
                    if (oldFluid == null)
                        return InteractionResult.FAIL;
                    if (oldFluid.getFluid() != baseFluid.get())
                        return InteractionResult.FAIL;

                    var newFluid = this.newFluid.get() != null ? new FluidStack(this.newFluid.get(), oldFluid.getAmount()) : null;

                    Item item = pStack.getItem();
                    pStack.shrink(1);
                    pPlayer.awardStat(Stats.USE_CAULDRON);
                    pPlayer.awardStat(Stats.ITEM_USED.get(item));

                    pLevel.setBlockAndUpdate(pBlockPos, newCauldronState.get());

                    var newCauldron = pLevel.getBlockEntity(pBlockPos);
                    if (newCauldron != null) {
                        var newCauldronCap = newCauldron.getCapability(ForgeCapabilities.FLUID_HANDLER).resolve();
                        if (newCauldronCap.isPresent() && newFluid != null) {
                            for (var tank = 0; tank < newCauldronCap.get().getTanks(); tank++) {
                                Optional.of(newCauldronCap.get().getFluidInTank(tank))
                                        .filter(fs -> !fs.isEmpty())
                                        .ifPresent(fs -> fs.setAmount(0));
                            }
                            newCauldronCap.get().fill(newFluid, IFluidHandler.FluidAction.EXECUTE);
                        }
                    }

                    pLevel.playSound(null, pBlockPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    pLevel.gameEvent(null, GameEvent.BLOCK_CHANGE, pBlockPos);
                }
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }));
        }

        @Override
        public Supplier<FluidStack> getFluidResult() {
            return () -> new FluidStack(newFluid.get(), 1000);
        }

        @Override
        public Supplier<Item> getFluidInput() {
            return itemToMelt;
        }

        @Override
        public Supplier<FluidStack> getFluidBaseFluid() {
            return () -> new FluidStack(baseFluid.get(), 1000);
        }


        public static CauldronRecipe.CauldronMixtureRecipe of() {
            return new CauldronRecipe.CauldronMixtureRecipe();
        }
    }

    public static class CauldronMeltingRecipe extends CauldronRecipe implements FluidResult {
        protected Supplier<Item> itemToMelt;
        protected Supplier<BlockState> newCauldronState;
        protected Supplier<FluidStack> newFluid = () -> null;

        public CauldronMeltingRecipe setItemToMelt(Supplier<Item> itemToMelt) {
            this.itemToMelt = itemToMelt;
            return this;
        }

        public CauldronMeltingRecipe setNewCauldronStateSupplier(Supplier<BlockState> newCauldronState) {
            this.newCauldronState = newCauldronState;
            return this;
        }

        public CauldronMeltingRecipe setNewFluid(Supplier<FluidStack> newFluid) {
            this.newFluid = newFluid;
            return this;
        }

        @Override
        public void insertInteractions() {
            baseFluidInteractionMaps.forEach(map -> map.put(itemToMelt.get(), (BlockState pBlockState, Level pLevel, BlockPos pBlockPos, Player pPlayer, InteractionHand pHand, ItemStack pStack) -> {
                if (!pLevel.isClientSide) {
                    if (getRequireHeating() && !FluidCauldronBE.isCauldronHeated(pLevel, pBlockPos))
                        return InteractionResult.FAIL;

                    var oldCauldron = pLevel.getBlockEntity(pBlockPos);
                    FluidStack oldFluid = null;
                    if (oldCauldron != null) {
                        oldFluid = oldCauldron.getCapability(ForgeCapabilities.FLUID_HANDLER).resolve()
                                .filter(handler -> handler.getTanks() > 0)
                                .map(handler -> handler.getFluidInTank(0))
                                .orElse(null);
                    }
                    var newFluid = getFluidResult().get();
                    if (oldFluid != null && oldFluid.getAmount() >= 1000)
                        return InteractionResult.FAIL;

                    if (oldFluid != null && newFluid != null && newFluid.getFluid() != oldFluid.getFluid()) {
                        return InteractionResult.FAIL;
                    }


                    Item item = pStack.getItem();
                    pStack.shrink(1);
                    pPlayer.awardStat(Stats.USE_CAULDRON);
                    pPlayer.awardStat(Stats.ITEM_USED.get(item));

                    pLevel.setBlockAndUpdate(pBlockPos, newCauldronState.get());

                    var newCauldron = pLevel.getBlockEntity(pBlockPos);
                    if (newCauldron != null) {
                        var newCauldronCap = newCauldron.getCapability(ForgeCapabilities.FLUID_HANDLER).resolve();
                        if (newCauldronCap.isPresent()) {
                            var copy = oldFluid != null ? oldFluid.copy() : null;
                            for (var tank = 0; tank < newCauldronCap.get().getTanks(); tank++) {
                                Optional.of(newCauldronCap.get().getFluidInTank(tank))
                                        .filter(fs -> !fs.isEmpty())
                                        .ifPresent(fs -> fs.setAmount(0));
                            }
                            if (oldFluid != null)
                                newCauldronCap.get().fill(copy, IFluidHandler.FluidAction.EXECUTE);
                            if (newFluid != null)
                                newCauldronCap.get().fill(newFluid, IFluidHandler.FluidAction.EXECUTE);
                        }
                    }

                    pLevel.playSound(null, pBlockPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    pLevel.gameEvent(null, GameEvent.BLOCK_CHANGE, pBlockPos);
                }
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }));
        }

        @Override
        public Supplier<FluidStack> getFluidResult() {
            return newFluid;
        }

        @Override
        public Supplier<Item> getFluidInput() {
            return itemToMelt;
        }

        @Override
        public Supplier<FluidStack> getFluidBaseFluid() {
            return () -> null;
        }

        public static CauldronRecipe.CauldronMeltingRecipe of() {
            return new CauldronRecipe.CauldronMeltingRecipe();
        }
    }

    /**
     * Holds recipe that can make for example ingot from fluid - the fluid is then removed from the cauldron
     */
    public static class CauldronCastingRecipe extends CauldronRecipe implements ItemResult {
        protected Supplier<ItemStack> drop;
        protected Supplier<FluidStack> baseFluid;

        public CauldronCastingRecipe setDrop(Supplier<ItemStack> drop) {
            this.drop = drop;
            return this;
        }

        public CauldronCastingRecipe setBaseFluid(Supplier<FluidStack> baseFluid) {
            this.baseFluid = baseFluid;
            return this;
        }

        @Override
        public void insertInteractions() {
            baseFluidInteractionMaps.forEach(map -> map.put(Blocks.SNOW_BLOCK.asItem(), (BlockState pBlockState, Level pLevel, BlockPos pBlockPos, Player pPlayer, InteractionHand pHand, ItemStack pStack) -> {
                if (!pLevel.isClientSide) {
                    if (getRequireHeating() && !FluidCauldronBE.isCauldronHeated(pLevel, pBlockPos))
                        return InteractionResult.FAIL;

                    var oldCauldron = pLevel.getBlockEntity(pBlockPos);
                    FluidStack oldFluid = null;
                    if (oldCauldron != null) {
                        oldFluid = oldCauldron.getCapability(ForgeCapabilities.FLUID_HANDLER).resolve()
                                .filter(handler -> handler.getTanks() > 0)
                                .map(handler -> handler.getFluidInTank(0))
                                .orElse(null);
                    }
                    if (baseFluid.get() != null && oldFluid == null)
                        return InteractionResult.FAIL;
                    if (baseFluid.get() != null && oldFluid != null && oldFluid.getFluid() != baseFluid.get().getFluid())
                        return InteractionResult.FAIL;
                    if (baseFluid.get() != null && oldFluid != null && oldFluid.getAmount() < baseFluid.get().getAmount()) {
                        return InteractionResult.FAIL;
                    }

                    Item item = pStack.getItem();
                    pStack.shrink(1);
                    pPlayer.awardStat(Stats.USE_CAULDRON);
                    pPlayer.awardStat(Stats.ITEM_USED.get(item));

                    if (oldFluid == null) oldFluid = new FluidStack(Fluids.EMPTY, 0);
                    if (baseFluid.get() != null && oldFluid.getAmount() > 0)
                        oldFluid.shrink(baseFluid.get().getAmount());

                    if (oldFluid.isEmpty())
                        pLevel.setBlockAndUpdate(pBlockPos, Blocks.CAULDRON.defaultBlockState());

                    var newCauldron = pLevel.getBlockEntity(pBlockPos);
                    if (newCauldron != null) {
                        var newCauldronCap = newCauldron.getCapability(ForgeCapabilities.FLUID_HANDLER).resolve();
                        if (newCauldronCap.isPresent()) {
                            var copy = oldFluid.copy();
                            for (var tank = 0; tank < newCauldronCap.get().getTanks(); tank++) {
                                Optional.of(newCauldronCap.get().getFluidInTank(tank))
                                        .filter(fs -> !fs.isEmpty())
                                        .ifPresent(fs -> fs.setAmount(0));
                            }
                            newCauldronCap.get().fill(copy, IFluidHandler.FluidAction.EXECUTE);
                        }
                    }

                    pLevel.playSound(null, pBlockPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    pLevel.gameEvent(null, GameEvent.BLOCK_CHANGE, pBlockPos);
                    ItemHandlerHelper.spawnItemStack(pLevel, pBlockPos.above(), drop.get());
                }
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }));
        }

        @Override
        public Supplier<Item> getItemInput() {
            return Blocks.SNOW_BLOCK::asItem;
        }

        @Override
        public Supplier<ItemStack> getItemResult() {
            return drop;
        }

        @Override
        public Supplier<FluidStack> getItemBaseFluid() {
            return baseFluid;
        }


        public static CauldronRecipe.CauldronCastingRecipe of() {
            return new CauldronRecipe.CauldronCastingRecipe();
        }
    }

    /**
     * Holds recipe for making for example circuits - when clicked with correct item, item is removed will spawn new item - fluid will remain in the cauldron
     */
    public static class CauldronBathingRecipe extends CauldronRecipe implements ItemResult {
        protected Supplier<Item> itemToBath;
        protected Supplier<ItemStack> drop;
        protected Supplier<FluidStack> baseFluid;

        public CauldronBathingRecipe setItemToBath(Supplier<Item> itemToBath) {
            this.itemToBath = itemToBath;
            return this;
        }

        public CauldronBathingRecipe setDrop(Supplier<ItemStack> drop) {
            this.drop = drop;
            return this;
        }

        public CauldronBathingRecipe setBaseFluid(Supplier<FluidStack> baseFluid) {
            this.baseFluid = baseFluid;
            return this;
        }


        @Override
        public void insertInteractions() {
            baseFluidInteractionMaps.forEach(map -> map.put(itemToBath.get(), (BlockState pBlockState, Level pLevel, BlockPos pBlockPos, Player pPlayer, InteractionHand pHand, ItemStack pStack) -> {
                if (!pLevel.isClientSide) {
                    if (getRequireHeating() && !FluidCauldronBE.isCauldronHeated(pLevel, pBlockPos))
                        return InteractionResult.FAIL;

                    var oldCauldron = pLevel.getBlockEntity(pBlockPos);
                    FluidStack oldFluid = null;
                    if (oldCauldron != null) {
                        oldFluid = oldCauldron.getCapability(ForgeCapabilities.FLUID_HANDLER).resolve()
                                .filter(handler -> handler.getTanks() > 0)
                                .map(handler -> handler.getFluidInTank(0))
                                .orElse(null);
                    }
                    if (baseFluid.get() != null && oldFluid == null)
                        return InteractionResult.FAIL;
                    if (baseFluid.get() != null && oldFluid != null && oldFluid.getFluid() != baseFluid.get().getFluid())
                        return InteractionResult.FAIL;
                    if (baseFluid.get() != null && oldFluid != null && oldFluid.getAmount() < baseFluid.get().getAmount()) {
                        return InteractionResult.FAIL;
                    }

                    Item item = pStack.getItem();
                    pStack.shrink(1);
                    pPlayer.awardStat(Stats.USE_CAULDRON);
                    pPlayer.awardStat(Stats.ITEM_USED.get(item));

                    if (oldFluid == null) oldFluid = new FluidStack(Fluids.EMPTY, 0);
                    if (baseFluid.get() != null && oldFluid.getAmount() > 0)
                        oldFluid.shrink(baseFluid.get().getAmount());

                    if (oldFluid.isEmpty())
                        pLevel.setBlockAndUpdate(pBlockPos, Blocks.CAULDRON.defaultBlockState());

                    var newCauldron = pLevel.getBlockEntity(pBlockPos);
                    if (newCauldron != null) {
                        var newCauldronCap = newCauldron.getCapability(ForgeCapabilities.FLUID_HANDLER).resolve();
                        if (newCauldronCap.isPresent()) {
                            var copy = oldFluid.copy();
                            for (var tank = 0; tank < newCauldronCap.get().getTanks(); tank++) {
                                newCauldronCap.get().getFluidInTank(tank).setAmount(0);
                            }
                            newCauldronCap.get().fill(copy, IFluidHandler.FluidAction.EXECUTE);
                        }
                    }

                    pLevel.playSound(null, pBlockPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    pLevel.gameEvent(null, GameEvent.BLOCK_CHANGE, pBlockPos);
                    ItemHandlerHelper.spawnItemStack(pLevel, pBlockPos.above(), drop.get());
                }
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }));
        }

        @Override
        public Supplier<Item> getItemInput() {
            return itemToBath;
        }

        @Override
        public Supplier<ItemStack> getItemResult() {
            return drop;
        }

        @Override
        public Supplier<FluidStack> getItemBaseFluid() {
            return baseFluid;
        }


        public static CauldronRecipe.CauldronBathingRecipe of() {
            return new CauldronRecipe.CauldronBathingRecipe();
        }
    }

    public static class CauldronGenericRecipes extends CauldronRecipe {
        protected final FluidHelper.MoltenFluid forFluid;

        public CauldronGenericRecipes(FluidHelper.MoltenFluid forFluid) {
            super();
            this.forFluid = forFluid;
            setRecipeHandlers(forFluid.cauldronInteractionMap);
        }

        @Override
        public void insertInteractions() {
            CauldronInteraction.EMPTY.put(forFluid.bucket.get(), (BlockState pBlockState, Level pLevel, BlockPos pBlockPos, Player pPlayer, InteractionHand pHand, ItemStack pStack) ->
                    emptyBucket(pLevel, pBlockPos, pPlayer, pHand, pStack, forFluid.cauldron.get().defaultBlockState(), SoundEvents.BUCKET_EMPTY_LAVA)
            );
            CauldronInteraction.LAVA.put(forFluid.bucket.get(), (BlockState pBlockState, Level pLevel, BlockPos pBlockPos, Player pPlayer, InteractionHand pHand, ItemStack pStack) ->
                    emptyBucket(pLevel, pBlockPos, pPlayer, pHand, pStack, forFluid.cauldron.get().defaultBlockState(), SoundEvents.BUCKET_EMPTY_LAVA)
            );
            CauldronInteraction.WATER.put(forFluid.bucket.get(), (BlockState pBlockState, Level pLevel, BlockPos pBlockPos, Player pPlayer, InteractionHand pHand, ItemStack pStack) ->
                    emptyBucket(pLevel, pBlockPos, pPlayer, pHand, pStack, forFluid.cauldron.get().defaultBlockState(), SoundEvents.BUCKET_EMPTY_LAVA)
            );

            baseFluidInteractionMaps.forEach(map -> {
                CauldronInteraction.addDefaultInteractions(map);
                map.put(Items.BUCKET, (BlockState pBlockState, Level pLevel, BlockPos pBlockPos, Player pPlayer, InteractionHand pHand, ItemStack pStack) ->
                        fillBucket(pBlockState, pLevel, pBlockPos, pPlayer, pHand, pStack, new ItemStack(forFluid.bucket.get()), (state) -> true, SoundEvents.BUCKET_FILL_LAVA)
                );
            });
        }
    }


    @SuppressWarnings("all")
    public static InteractionResult fillBucket(BlockState pBlockState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, ItemStack pEmptyStack, ItemStack pFilledStack, Predicate<BlockState> pStatePredicate, SoundEvent pFillSound) {
        if (!pStatePredicate.test(pBlockState)) {
            return InteractionResult.PASS;
        } else {
            if (!pLevel.isClientSide) {
                Item item = pEmptyStack.getItem();
                pPlayer.setItemInHand(pHand, ItemUtils.createFilledResult(pEmptyStack, pPlayer, pFilledStack));
                pPlayer.awardStat(Stats.USE_CAULDRON);
                pPlayer.awardStat(Stats.ITEM_USED.get(item));
                pLevel.setBlockAndUpdate(pPos, Blocks.CAULDRON.defaultBlockState());
                pLevel.playSound(null, pPos, pFillSound, SoundSource.BLOCKS, 1.0F, 1.0F);
                pLevel.gameEvent(null, GameEvent.FLUID_PICKUP, pPos);
            }

            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
    }

    @SuppressWarnings("all")
    public static InteractionResult emptyBucket(Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, ItemStack pFilledStack, BlockState pState, SoundEvent pEmptySound) {
        if (!pLevel.isClientSide) {
            Item item = pFilledStack.getItem();
            pPlayer.setItemInHand(pHand, ItemUtils.createFilledResult(pFilledStack, pPlayer, new ItemStack(Items.BUCKET)));
            pPlayer.awardStat(Stats.FILL_CAULDRON);
            pPlayer.awardStat(Stats.ITEM_USED.get(item));
            pLevel.setBlockAndUpdate(pPos, pState);
            pLevel.playSound(null, pPos, pEmptySound, SoundSource.BLOCKS, 1.0F, 1.0F);
            pLevel.gameEvent(null, GameEvent.FLUID_PLACE, pPos);
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }
}

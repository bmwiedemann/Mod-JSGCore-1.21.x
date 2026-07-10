package dev.tauri.jsg.core.common.item;

import dev.tauri.jsg.core.common.helper.ItemHelper;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import dev.tauri.jsg.core.common.registry.tag.CoreBlockTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Rotation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class JSGWrench extends JSGItem {
    public JSGWrench() {
        super(new Item.Properties().stacksTo(1), CoreTabs.TAB_TOOLS);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        ItemHelper.applyGenericToolTip(this.getDescriptionId(), components, tooltipFlag);
        components.add(Component.empty());
        components.add(Component.literal(String.format("%.2f", (((double) (getMaxDamage(stack) - getDamage(stack)) / ((double) getMaxDamage(stack))) * 100)) + "%").withStyle(ChatFormatting.GRAY));
    }

    @Nonnull
    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemStack = new ItemStack(this);
        setDamage(itemStack, 0);
        return itemStack;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, net.minecraft.core.Holder<Enchantment> enchantment) {
        return enchantment.is(net.minecraft.world.item.enchantment.Enchantments.UNBREAKING);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getEnchantmentValue() {
        return 3;
    }

    @Override
    public boolean hasCraftingRemainingItem(@Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, damage);
        if (getMaxDamage(stack) <= damage) stack.setCount(0);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 255;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack it = itemStack.copy();
        it.setDamageValue(itemStack.getDamageValue() + 1);
        return it;
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public InteractionResult useOn(UseOnContext pContext) {
        var pos = pContext.getClickedPos();
        var state = pContext.getLevel().getBlockState(pos);
        if (state.getBlock() instanceof dev.tauri.jsg.core.common.block.util.WrenchRotatable rotatable) {
            rotatable.onWrenchUse(state, pContext);
            return InteractionResult.sidedSuccess(pContext.getLevel().isClientSide());
        } else if (state.is(CoreBlockTags.WRENCH_ROTATABLE)) {
            state = state.rotate(pContext.getLevel(), pos, Rotation.CLOCKWISE_90);
            pContext.getLevel().setBlock(pos, state, 3);
            return InteractionResult.sidedSuccess(pContext.getLevel().isClientSide());
        }
        return InteractionResult.PASS;
    }
}

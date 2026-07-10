package dev.tauri.jsg.core.common.entity.tab;

import dev.tauri.jsg.core.common.helper.JSGMinecraftHelper;
import dev.tauri.jsg.core.mapping.JSGMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CoreCreativeTab extends CreativeModeTab {
    protected final List<Supplier<ItemStack>> iconsGenerator;
    protected final List<ItemStack> icons = new ArrayList<>();

    protected CoreCreativeTab(Builder builder) {
        super(builder);
        this.iconsGenerator = builder.iconsGenerator;
    }

    @Override
    @NonNull
    public ItemStack getIconItem() {
        if (icons.isEmpty()) {
            if (iconsGenerator.isEmpty()) {
                getDisplayItems().forEach(item -> icons.add(item.copy()));
            } else
                iconsGenerator.forEach(item -> icons.add(item.get()));
        }
        if (icons.isEmpty()) return ItemStack.EMPTY;
        return icons.get((int) (JSGMinecraftHelper.getClientTick() / 100) % icons.size());
    }


    public static class Builder extends CreativeModeTab.Builder {
        public Builder() {
            super(Row.TOP, 0);
        }

        protected final List<Supplier<ItemStack>> iconsGenerator = new ArrayList<>();

        @ParametersAreNonnullByDefault
        public Builder withIcons(List<Supplier<ItemStack>> icons) {
            this.iconsGenerator.addAll(icons);
            return this;
        }

        @Override
        @ParametersAreNonnullByDefault
        @NotNull
        public CreativeModeTab.Builder icon(Supplier<ItemStack> pIcon) {
            this.iconsGenerator.add(pIcon);
            return this;
        }

        @Override
        @NotNull
        public CoreCreativeTab build() {
            return new CoreCreativeTab(this);
        }

    }
}

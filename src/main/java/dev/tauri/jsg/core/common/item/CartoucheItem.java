package dev.tauri.jsg.core.common.item;

import dev.tauri.jsg.core.client.renderer.AbstractItemBEWLR;
import dev.tauri.jsg.core.client.renderer.item.CartoucheItemBEWLR;
import dev.tauri.jsg.core.common.block.cartouche.CartoucheBlock;
import dev.tauri.jsg.core.common.block.cartouche.CartoucheType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import dev.tauri.jsg.core.common.registry.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CartoucheItem extends JSGBlockItem {
    public final Supplier<BlockState> material;
    public final CartoucheType type;

    public CartoucheItem(CartoucheBlock pBlock, Properties pProperties, @Nullable List<RegistryObject<CreativeModeTab>> tabs) {
        super(pBlock, pProperties, tabs);
        this.material = pBlock.material;
        this.type = pBlock.type;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(AbstractItemBEWLR.create(CartoucheItemBEWLR::new));
    }


}

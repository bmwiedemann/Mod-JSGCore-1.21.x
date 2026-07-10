package dev.tauri.jsg.core.common.item;

import dev.tauri.jsg.core.common.entity.vehicle.JSGBoat;
import dev.tauri.jsg.core.common.entity.vehicle.JSGBoatTypeWrapper;
import dev.tauri.jsg.core.common.entity.vehicle.JSGChestBoat;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BoatItem<T extends Enum<T> & JSGBoatTypeWrapper.Type> extends JSGItem {
    private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
    private final T type;
    private final boolean hasChest;
    public final JSGBoatTypeWrapper<T> boatTypeWrapper;
    public final Supplier<? extends EntityType<? extends JSGBoat<?>>> boatEntityTypeSupplier;
    public final Supplier<? extends EntityType<? extends JSGChestBoat<?>>> chestBoatEntityTypeSupplier;

    public static <T extends Enum<T> & JSGBoatTypeWrapper.Type> BoatItem<T> createNormal(Supplier<? extends EntityType<? extends JSGBoat<?>>> boatEntityTypeSupplier, JSGBoatTypeWrapper<T> boatTypeWrapper, T pType, Item.Properties pProperties, List<RegistryObject<CreativeModeTab>> tabs) {
        return new BoatItem<>(boatEntityTypeSupplier, () -> null, boatTypeWrapper, false, pType, pProperties, tabs);
    }

    public static <T extends Enum<T> & JSGBoatTypeWrapper.Type> BoatItem<T> createChest(Supplier<? extends EntityType<? extends JSGChestBoat<?>>> chestBoatEntityTypeSupplier, JSGBoatTypeWrapper<T> boatTypeWrapper, T pType, Item.Properties pProperties, List<RegistryObject<CreativeModeTab>> tabs) {
        return new BoatItem<>(() -> null, chestBoatEntityTypeSupplier, boatTypeWrapper, true, pType, pProperties, tabs);
    }

    private BoatItem(Supplier<? extends EntityType<? extends JSGBoat<?>>> boatEntityTypeSupplier, Supplier<? extends EntityType<? extends JSGChestBoat<?>>> chestBoatEntityTypeSupplier, JSGBoatTypeWrapper<T> boatTypeWrapper, boolean pHasChest, T pType, Item.Properties pProperties, List<RegistryObject<CreativeModeTab>> tabs) {
        super(pProperties, tabs);
        this.hasChest = pHasChest;
        this.type = pType;
        this.boatTypeWrapper = boatTypeWrapper;
        this.boatEntityTypeSupplier = boatEntityTypeSupplier;
        this.chestBoatEntityTypeSupplier = chestBoatEntityTypeSupplier;
    }

    @ParametersAreNonnullByDefault
    @NotNull
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        HitResult hitresult = getPlayerPOVHitResult(pLevel, pPlayer, ClipContext.Fluid.ANY);
        if (hitresult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
        } else {
            Vec3 vec3 = pPlayer.getViewVector(1.0F);
            List<Entity> list = pLevel.getEntities(pPlayer, pPlayer.getBoundingBox().expandTowards(vec3.scale(5.0D)).inflate(1.0D), ENTITY_PREDICATE);
            if (!list.isEmpty()) {
                Vec3 vec31 = pPlayer.getEyePosition();

                for (Entity entity : list) {
                    AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
                    if (aabb.contains(vec31)) {
                        return InteractionResultHolder.pass(itemstack);
                    }
                }
            }

            if (hitresult.getType() == HitResult.Type.BLOCK) {
                var boat = this.getBoat(pLevel, hitresult);
                boat.setYRot(pPlayer.getYRot());
                if (!pLevel.noCollision(boat, boat.getBoundingBox())) {
                    return InteractionResultHolder.fail(itemstack);
                } else {
                    if (!pLevel.isClientSide) {
                        pLevel.addFreshEntity(boat);
                        pLevel.gameEvent(pPlayer, GameEvent.ENTITY_PLACE, hitresult.getLocation());
                        if (!pPlayer.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                    }

                    pPlayer.awardStat(Stats.ITEM_USED.get(this));
                    return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
                }
            } else {
                return InteractionResultHolder.pass(itemstack);
            }
        }
    }

    private Boat getBoat(Level level, HitResult hit) {
        if (this.hasChest) {
            var boat = new JSGChestBoat<>(boatTypeWrapper, chestBoatEntityTypeSupplier.get(), level, hit.getLocation().x, hit.getLocation().y, hit.getLocation().z);
            boat.setVariant(this.type);
            return boat;
        }
        var boat = new JSGBoat<>(boatTypeWrapper, boatEntityTypeSupplier.get(), level, hit.getLocation().x, hit.getLocation().y, hit.getLocation().z);
        boat.setVariant(this.type);
        return boat;
    }
}

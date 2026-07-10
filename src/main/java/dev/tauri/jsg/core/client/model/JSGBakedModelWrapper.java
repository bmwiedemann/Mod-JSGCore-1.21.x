package dev.tauri.jsg.core.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.BakedModelWrapper;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JSGBakedModelWrapper extends BakedModelWrapper<BakedModel> {
    private final List<ItemDisplayContext> customRenderTypes;
    private final boolean ignoreJsonTransformations;
    private boolean isCustomRender = true;

    public static Map<ItemDisplayContext, ItemTransform> DEFAULT_TRANSFORMS = Util.make(new HashMap<>(), map -> {
        map.put(ItemDisplayContext.GROUND, new ItemTransform(
                new Vector3f(0, 0, 0),
                new Vector3f(0, 2, 0).mul(0.0625F),
                new Vector3f(0.5f, 0.5f, 0.5f)
        ));
        map.put(ItemDisplayContext.HEAD, new ItemTransform(
                new Vector3f(0, 180, 0),
                new Vector3f(0, 13, 7).mul(0.0625F),
                new Vector3f(1, 1, 1)
        ));
        map.put(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, new ItemTransform(
                new Vector3f(0, 0, 0),
                new Vector3f(0, 3, 1).mul(0.0625F),
                new Vector3f(0.55f, 0.55f, 0.55f)
        ));
        map.put(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, new ItemTransform(
                new Vector3f(0, -90, 25),
                new Vector3f(1.13f, 3.2f, 1.13f).mul(0.0625F),
                new Vector3f(0.68f, 0.68f, 0.68f)
        ));
        map.put(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, new ItemTransform(
                new Vector3f(0, 0, 0),
                new Vector3f(0, 3, 1).mul(0.0625F),
                new Vector3f(0.55f, 0.55f, 0.55f)
        ));
        map.put(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, new ItemTransform(
                new Vector3f(0, -90, 25),
                new Vector3f(1.13f, 3.2f, 1.13f).mul(0.0625F),
                new Vector3f(0.68f, 0.68f, 0.68f)
        ));
        map.put(ItemDisplayContext.FIXED, new ItemTransform(
                new Vector3f(0, 180, 0),
                new Vector3f(0, 0, 0).mul(0.0625F),
                new Vector3f(1, 1, 1)
        ));
    });

    public JSGBakedModelWrapper(BakedModel parent, List<ItemDisplayContext> customRenderTypes, boolean ignoreJsonTransformations) {
        super(parent);
        this.customRenderTypes = customRenderTypes;
        this.ignoreJsonTransformations = ignoreJsonTransformations;
    }

    @Override
    @ParametersAreNonnullByDefault
    public @NotNull BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        isCustomRender = customRenderTypes.contains(transformType);
        if (!isCustomRender || !ignoreJsonTransformations)
            super.applyTransform(transformType, poseStack, applyLeftHandTransform);
        else {
            Optional.ofNullable(DEFAULT_TRANSFORMS.get(transformType)).ifPresent(transform -> transform.apply(applyLeftHandTransform, poseStack));
        }
        return this;
    }

    @Override
    public boolean isCustomRenderer() {
        return isCustomRender || super.isCustomRenderer();
    }
}

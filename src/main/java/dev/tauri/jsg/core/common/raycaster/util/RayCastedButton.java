package dev.tauri.jsg.core.common.raycaster.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.tauri.jsg.core.common.symbol.SymbolInterface;
import dev.tauri.jsg.core.common.symbol.SymbolType;
import dev.tauri.jsg.core.common.symbol.pointoforigin.PointOfOrigin;
import dev.tauri.jsg.core.common.util.vectors.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RayCastedButton {
    public final List<Vector3f> vectors = new ArrayList<>();
    public final int buttonId;

    @Nullable
    public final Function<PointOfOrigin, Component> titleLocalization;

    public RayCastedButton(int buttonId, List<Vector3f> vectors) {
        this(buttonId, (poo) -> null, vectors, false);
    }

    public RayCastedButton(int buttonId, List<Vector3f> vectors, boolean correctXYZ) {
        this(buttonId, (poo) -> null, vectors, correctXYZ);
    }

    public RayCastedButton(int buttonId, @NotNull RegistryObject<? extends SymbolType<?>> symbolType, List<Vector3f> vectors) {
        this(buttonId, (poo) -> Component.literal(symbolType.get().valueOf(buttonId).getEnglishName(poo)), vectors, false);
    }

    public RayCastedButton(int buttonId, @NotNull String titleLocalization, List<Vector3f> vectors) {
        this(buttonId, titleLocalization, vectors, false);
    }

    public RayCastedButton(int buttonId, @Nullable Function<PointOfOrigin, Component> titleLocalization, List<Vector3f> vectors) {
        this(buttonId, titleLocalization, vectors, false);
    }

    public RayCastedButton(int buttonId, @NotNull String titleLocalization, List<Vector3f> vectors, boolean correctXYZ) {
        this(buttonId, (poo) -> Component.translatable(titleLocalization), vectors, correctXYZ);
    }

    public RayCastedButton(SymbolInterface symbol, List<Vector3f> vectors) {
        this(symbol, vectors, false);
    }

    public RayCastedButton(SymbolInterface symbol, List<Vector3f> vectors, boolean correctXYZ) {
        this(symbol.getId(), (poo) -> Component.literal(symbol.getEnglishName(poo)), vectors, correctXYZ);
    }

    public RayCastedButton(int buttonId, @Nullable Function<PointOfOrigin, Component> titleLocalization, List<Vector3f> vectors, boolean correctXYZ) {
        this.titleLocalization = titleLocalization;
        this.buttonId = buttonId;
        this.vectors.addAll(vectors);
        if (correctXYZ) {
            this.vectors.clear();
            for (var v : vectors) {
                this.vectors.add(new Vector3f(v.getX(), -v.getZ(), v.getY()));
            }
        }
    }


    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack stack) {
        stack.pushPose();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Matrix4f matrix = stack.last().pose();

        Tesselator t = Tesselator.getInstance();
        BufferBuilder b = t.getBuilder();
        b.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        for (var vertex : vectors) {
            b.vertex(matrix, vertex.x, vertex.z, -vertex.y).color(1.0f, 1.0f, 1.0f, 1).endVertex();
        }
        b.vertex(matrix, vectors.get(0).x, vectors.get(0).z, -vectors.get(0).y).color(1.0f, 1.0f, 1.0f, 1).endVertex();

        t.end();
        stack.popPose();
    }

    @NotNull
    public Vector3f getTitlePos() {
        if (vectors.isEmpty()) return new Vector3f();
        var vector = new Vector3f(0, Float.MIN_VALUE, 0);
        for (var v : vectors) {
            if (v.z > vector.y) vector.y = v.z;
            vector.x += v.x;
            vector.z -= v.y;
        }
        vector.x /= vectors.size();
        vector.z /= vectors.size();
        return vector;
    }

    @OnlyIn(Dist.CLIENT)
    public void renderTitle(@Nullable PointOfOrigin pointOfOrigin, float rotation, PoseStack stack, MultiBufferSource bufferSource) {
        if (titleLocalization == null) return;
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        var titleComponent = titleLocalization.apply(pointOfOrigin);
        if (titleComponent == null) return;
        var titlePos = getTitlePos();

        stack.pushPose();
        stack.translate(titlePos.getX(), titlePos.getY() + 0.1f, titlePos.getZ());
        stack.mulPose(Axis.YP.rotationDegrees(-rotation));
        stack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        stack.translate(0, 0, -0.1);
        var scale = 0.2f;
        stack.scale(-0.025f * scale, -0.025f * scale, 0.025f * scale);
        Matrix4f matrix4f = stack.last().pose();
        float backgroundOpacityConfig = Minecraft.getInstance().options.getBackgroundOpacity(0.25f);
        int backgroundOpacity = (int) (backgroundOpacityConfig * 255.0f) << 24;
        Font font = Minecraft.getInstance().font;
        float x = (float) (-font.width(titleComponent) / 2);
        font.drawInBatch(titleComponent, x, 0, 553648127, false, matrix4f, bufferSource, Font.DisplayMode.SEE_THROUGH, backgroundOpacity, LightTexture.FULL_BRIGHT);
        font.drawInBatch(titleComponent, x, 0, -1, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
        stack.popPose();
    }
}

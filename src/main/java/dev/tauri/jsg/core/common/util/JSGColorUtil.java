package dev.tauri.jsg.core.common.util;

import java.awt.*;

@SuppressWarnings("unused")
public class JSGColorUtil {
    public static int blendColors(int colorA, int colorB, float colorBRatio) {
        colorBRatio = Math.min(1f, colorBRatio);
        return blendColors(colorA, colorB, 1.0f - colorBRatio, colorBRatio);
    }

    public static int blendColors(int a, int b, float colorARation, float colorBRatio) {

        int aA = (a >> 24) & 0xff;
        int aR = ((a & 0xff0000) >> 16);
        int aG = ((a & 0xff00) >> 8);
        int aB = (a & 0xff);

        int bA = (b >> 24) & 0xff;
        int bR = ((b & 0xff0000) >> 16);
        int bG = ((b & 0xff00) >> 8);
        int bB = (b & 0xff);

        int alpha = ((int) (aA * colorARation) + (int) (bA * colorBRatio));
        int red = ((int) (aR * colorARation) + (int) (bR * colorBRatio));
        int green = ((int) (aG * colorARation) + (int) (bG * colorBRatio));
        int blue = ((int) (aB * colorARation) + (int) (bB * colorBRatio));

        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static Color toColor(int hex) {
        return new Color(hex);
    }

    public static int fromColor(Color color) {
        return color.getRGB();
    }

    public static int blendColors(int backgroundColor, int foregroundColor) {
        int a1 = (backgroundColor >> 24) & 0xFF;
        int r1 = (backgroundColor >> 16) & 0xFF;
        int g1 = (backgroundColor >> 8) & 0xFF;
        int b1 = backgroundColor & 0xFF;

        int a2 = (foregroundColor >> 24) & 0xFF;
        int r2 = (foregroundColor >> 16) & 0xFF;
        int g2 = (foregroundColor >> 8) & 0xFF;
        int b2 = foregroundColor & 0xFF;

        float alphaTop = a2 / 255f;
        float alphaBottom = a1 / 255f;

        float outA = alphaTop + alphaBottom * (1 - alphaTop);

        if (outA == 0) {
            return 0;
        }

        int outR = (int) ((r2 * alphaTop + r1 * alphaBottom * (1 - alphaTop)) / outA);
        int outG = (int) ((g2 * alphaTop + g1 * alphaBottom * (1 - alphaTop)) / outA);
        int outB = (int) ((b2 * alphaTop + b1 * alphaBottom * (1 - alphaTop)) / outA);
        int outAlpha = (int) (outA * 255);

        return (outAlpha << 24) | (outR << 16) | (outG << 8) | outB;
    }
}

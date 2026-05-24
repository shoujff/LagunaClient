package me.deqes.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.BufferBuilder;

import net.minecraft.client.render.VertexFormats;
import org.joml.Matrix4f;
import java.awt.*;

import static me.deqes.util.Wrapper.mc;

public class RenderUtil implements Wrapper {

    public static void drawRect(DrawContext context, int x, int y, int width, int height, Color color) {
        context.fill(x, y, x + width, y + height, color.getRGB());
    }

    public static void drawText(DrawContext context, int x, int y, String text, Color color) {
        context.drawText(mc.textRenderer, text, x, y, color.getRGB(), false);
    }

    public static void drawText(DrawContext context, float scale, int x, int y, String text, Color color) {
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(scale);
        context.drawText(mc.textRenderer, text, (int) (x / scale), (int) (y / scale), color.getRGB(), false);
        context.getMatrices().popMatrix();
    }
}


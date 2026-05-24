package me.deqes.module.impl.render;

import com.google.common.eventbus.Subscribe;
import jdk.jfr.Event;
import me.deqes.event.EventRender;
import me.deqes.module.Category;
import me.deqes.module.Module;
import me.deqes.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import me.deqes.util.Wrapper;
import java.awt.*;

import static me.deqes.util.Wrapper.mc;

public class Watermark  extends Module {
    public Watermark() {
        super("Watermark", Category.RENDER, 80);

    }

    @Subscribe
    public void onRender(EventRender e) {

        DrawContext context = e.getContext();

        int x = 10;
        int y = 10;

        String username = mc.getSession().getUsername();
        String fps = String.valueOf(mc.getCurrentFps());

        String text = "LAGUNA  |  " + username + "  |  " + fps + " FPS";

        int width = mc.textRenderer.getWidth(text) + 24;
        int height = 24;

        // BIG SHADOW
        for (int i = 8; i >= 1; i--) {

            context.fill(
                    x - i,
                    y - i,
                    x + width + i,
                    y + height + i,
                    new Color(0, 0, 0, 8).getRGB()
            );
        }

        // MAIN ROUNDED BODY (fake celestial style)
        drawSmoothRect(
                context,
                x,
                y,
                width,
                height,
                new Color(18, 18, 18, 210)
        );

        // INNER LIGHT
        drawSmoothRect(
                context,
                x + 1,
                y + 1,
                width - 2,
                height - 2,
                new Color(28, 28, 28, 120)
        );

        // TOP HIGHLIGHT
        context.fill(
                x + 6,
                y + 3,
                x + width - 6,
                y + 4,
                new Color(255,255,255,45).getRGB()
        );

        // LEFT GLOW BAR
        context.fill(
                x + 4,
                y + 5,
                x + 6,
                y + height - 5,
                new Color(170,170,170,200).getRGB()
        );

        // TEXT SHADOW
        context.drawText(
                mc.textRenderer,
                text,
                x + 12,
                y + 9,
                new Color(50,50,50,180).getRGB(),
                false
        );

        // MAIN TEXT
        context.drawText(
                mc.textRenderer,
                text,
                x + 11,
                y + 8,
                new Color(235,235,235).getRGB(),
                false
        );
    }

    private void drawSmoothRect(
            DrawContext context,
            int x,
            int y,
            int width,
            int height,
            Color color
    ) {

        // center
        context.fill(
                x + 4,
                y,
                x + width - 4,
                y + height,
                color.getRGB()
        );

        // left
        context.fill(
                x,
                y + 4,
                x + 4,
                y + height - 4,
                color.getRGB()
        );

        // right
        context.fill(
                x + width - 4,
                y + 4,
                x + width,
                y + height - 4,
                color.getRGB()
        );

        // corners
        context.fill(
                x + 1,
                y + 1,
                x + 3,
                y + 3,
                color.getRGB()
        );

        context.fill(
                x + width - 3,
                y + 1,
                x + width - 1,
                y + 3,
                color.getRGB()
        );

        context.fill(
                x + 1,
                y + height - 3,
                x + 3,
                y + height - 1,
                color.getRGB()
        );

        context.fill(
                x + width - 3,
                y + height - 3,
                x + width - 1,
                y + height - 1,
                color.getRGB()
        );
    }
}

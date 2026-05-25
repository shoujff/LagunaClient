package me.deqes.module.impl.render;

import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import lombok.Setter;
import me.deqes.Laguna;
import me.deqes.event.EventRender;
import me.deqes.module.Category;
import me.deqes.module.Module;
import me.deqes.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static me.deqes.util.Wrapper.mc;

public class Hud extends Module {

    // Getters и Setters для настроек
    @Getter
    @Setter
    private boolean showWatermark = true;
    @Getter
    @Setter
    private boolean showEnabledModules = true;
    @Getter
    @Setter
    private boolean showCoordinates = true;
    @Getter
    @Setter
    private boolean showFPS = true;
    @Getter
    @Setter
    private boolean showPing = true;

    private List<String> enabledModulesList = new ArrayList<>();
    @Getter
    private int watermarkX = 5;
    @Getter
    private int watermarkY = 5;

    public Hud() {
        super("Hud", Category.RENDER, 0);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Subscribe
    public void onRender(EventRender e) {
        if (mc.player == null || mc.world == null) return;

        DrawContext context = e.getContext();
        int yOffset = watermarkY;

        // Watermark
        if (showWatermark) {
            String watermark = "§7[§fLaguna Client§7]";
            RenderUtil.drawText(context, watermarkX, yOffset, watermark, new Color(255, 255, 255));
            yOffset += 12;
        }

        // Включенные модули
        if (showEnabledModules) {
            updateEnabledModules();
            for (String moduleName : enabledModulesList) {
                RenderUtil.drawText(context, watermarkX + 5, yOffset, "§7» §f" + moduleName, new Color(200, 200, 200));
                yOffset += 11;
            }

            if (!enabledModulesList.isEmpty()) {
                yOffset += 2;
            }
        }

        // Координаты
        if (showCoordinates) {
            String coords = String.format("§7XYZ: §f%.0f §7%.0f §7%.0f",
                    mc.player.getX(), mc.player.getY(), mc.player.getZ());
            RenderUtil.drawText(context, watermarkX, yOffset, coords, new Color(200, 200, 200));
            yOffset += 11;
        }

        // FPS
        if (showFPS) {
            String fps = "§7FPS: §f" + mc.getCurrentFps();
            RenderUtil.drawText(context, watermarkX, yOffset, fps, new Color(200, 200, 200));
            yOffset += 11;
        }

        // Ping
        if (showPing && mc.getNetworkHandler() != null) {
            int ping = 0;
            if (mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()) != null) {
                ping = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency();
            }
            String pingText = "§7Ping: §f" + ping + "ms";
            RenderUtil.drawText(context, watermarkX, yOffset, pingText, new Color(200, 200, 200));
        }
    }

    private void updateEnabledModules() {
        enabledModulesList.clear();
        for (Module module : Laguna.getInstance().getModuleManager().getModules()) {
            if (module.isEnabled() && module != this) {
                enabledModulesList.add(module.getName());
            }
        }
    }

    public void setWatermarkX(int watermarkX) { this.watermarkX = Math.max(0, Math.min(watermarkX, mc.getWindow().getScaledWidth() - 100)); }

    public void setWatermarkY(int watermarkY) { this.watermarkY = Math.max(0, Math.min(watermarkY, mc.getWindow().getScaledHeight() - 100)); }
}
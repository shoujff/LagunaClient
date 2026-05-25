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
import net.minecraft.client.network.PlayerListEntry;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static me.deqes.util.Wrapper.mc;

@Getter
@Setter
public class Hud extends Module {

    // Настройки отображения
    private boolean showWatermark = true;
    private boolean showEnabledModules = true;
    private boolean showCoordinates = true;
    private boolean showFPS = true;
    private boolean showPing = true;
    private boolean showTime = true;
    private boolean showServerIP = true;
    private boolean showBiome = true;
    private boolean showArmorDurability = true;
    private boolean showDirection = true;
    private boolean showPotions = true;

    // Стили
    private boolean roundedBackground = true;
    private boolean gradientWatermark = true;
    private boolean animated = true;

    // Позиция
    private int hudX = 5;
    private int hudY = 5;

    // Анимация
    private int pulseTick = 0;

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

        if (animated) {
            pulseTick++;
            if (pulseTick > 40) pulseTick = 0;
        }

        DrawContext context = e.getContext();

        drawBackground(context);

        int yOffset = hudY;

        // Watermark
        if (showWatermark) {
            if (gradientWatermark) {
                drawGradientText(context, hudX, yOffset, "Laguna Client", new Color(255, 80, 120), new Color(255, 160, 80));
            } else {
                RenderUtil.drawText(context, hudX, yOffset, "§7[§fLaguna Client§7]", Color.WHITE);
            }
            yOffset += 14;
            if (showEnabledModules) {
                RenderUtil.drawRect(context, hudX, yOffset - 5, getMaxWidth() + 10, 1, new Color(255, 255, 255, 60));
            }
        }

        // Включенные модули
        if (showEnabledModules) {
            List<Module> enabled = getEnabledModules();
            if (!enabled.isEmpty()) {
                for (Module module : enabled) {
                    String name = module.getName();
                    Color color = getModuleColor(name);
                    RenderUtil.drawText(context, hudX + 6, yOffset, "» " + name, color);
                    yOffset += 11;
                }
                yOffset += 3;
            }
        }

        // Разделитель
        if (hasInfoElements() && (showWatermark || showEnabledModules)) {
            RenderUtil.drawRect(context, hudX, yOffset - 4, getMaxWidth() + 10, 1, new Color(255, 255, 255, 40));
        }

        // Направление
        if (showDirection) {
            String direction = getDirection();
            Color dirColor = direction.equals("N") || direction.equals("S") ? new Color(100, 255, 100) : new Color(255, 200, 100);
            RenderUtil.drawText(context, hudX, yOffset, "🧭 " + direction, dirColor);
            yOffset += 11;
        }

        // Координаты
        if (showCoordinates) {
            String coords = String.format("📍 %.0f %.0f %.0f",
                    mc.player.getX(), mc.player.getY(), mc.player.getZ());
            RenderUtil.drawText(context, hudX, yOffset, coords, new Color(130, 200, 255));
            yOffset += 11;
        }

        // Биом
        if (showBiome && mc.world != null) {
            String biome = mc.world.getBiome(mc.player.getBlockPos()).getKey().get().getValue().getPath();
            if (biome.length() > 20) biome = biome.substring(0, 17) + "...";
            RenderUtil.drawText(context, hudX, yOffset, "🌿 " + biome, new Color(100, 255, 150));
            yOffset += 11;
        }

        // FPS
        if (showFPS) {
            int fps = mc.getCurrentFps();
            Color fpsColor = fps >= 60 ? new Color(100, 255, 100) : (fps >= 30 ? new Color(255, 200, 80) : new Color(255, 80, 80));
            RenderUtil.drawText(context, hudX, yOffset, "⚡ FPS: " + fps, fpsColor);
            yOffset += 11;
        }

        // Ping
        if (showPing) {
            int ping = getPing();
            Color pingColor = ping <= 50 ? new Color(100, 255, 100) : (ping <= 150 ? new Color(255, 200, 80) : new Color(255, 80, 80));
            RenderUtil.drawText(context, hudX, yOffset, "📶 Ping: " + ping + "ms", pingColor);
            yOffset += 11;
        }

        // Время
        if (showTime) {
            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
            RenderUtil.drawText(context, hudX, yOffset, "🕒 " + time, new Color(200, 200, 200));
            yOffset += 11;
        }

        // IP сервера
        if (showServerIP && mc.getCurrentServerEntry() != null) {
            String ip = mc.getCurrentServerEntry().address;
            if (ip.length() > 35) ip = ip.substring(0, 32) + "...";
            RenderUtil.drawText(context, hudX, yOffset, "🌐 " + ip, new Color(200, 200, 200));
            yOffset += 11;
        }

        // Прочность брони


        // Эффекты зелий
        if (showPotions) {
            renderPotionEffects(context, yOffset);
        }
    }

    private void drawBackground(DrawContext context) {
        int width = getMaxWidth() + 15;
        int height = getTotalHeight() + 8;

        if (roundedBackground) {
            // Тень
            for (int i = 4; i > 0; i--) {
                RenderUtil.drawRect(context, hudX - i, hudY - i, width + i * 2, height + i * 2, new Color(0, 0, 0, 25));
            }

            // Основной фон
            RenderUtil.drawRect(context, hudX - 3, hudY - 3, width, height, new Color(15, 15, 20, 210));

            // Обводка
            RenderUtil.drawRect(context, hudX - 3, hudY - 3, width, 1, new Color(255, 255, 255, 100));
            RenderUtil.drawRect(context, hudX - 3, hudY + height - 4, width, 1, new Color(255, 255, 255, 100));
            RenderUtil.drawRect(context, hudX - 3, hudY - 3, 1, height, new Color(255, 255, 255, 50));
            RenderUtil.drawRect(context, hudX + width - 4, hudY - 3, 1, height, new Color(255, 255, 255, 50));

            // Цветная полоска сверху
            int pulseAlpha = animated ? (int)(150 + Math.sin(pulseTick / 10.0) * 50) : 200;
            RenderUtil.drawRect(context, hudX - 3, hudY - 3, width, 2, new Color(255, 100, 120, pulseAlpha));
        } else {
            RenderUtil.drawRect(context, hudX - 2, hudY - 2, width + 4, height + 4, new Color(0, 0, 0, 150));
            RenderUtil.drawRect(context, hudX, hudY, width + 2, height + 2, new Color(20, 20, 25, 220));
        }
    }

    private void drawGradientText(DrawContext context, int x, int y, String text, Color start, Color end) {
        int currentX = x;
        for (int i = 0; i < text.length(); i++) {
            float ratio = (float) i / text.length();
            int r = (int) (start.getRed() + (end.getRed() - start.getRed()) * ratio);
            int g = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * ratio);
            int b = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * ratio);
            RenderUtil.drawText(context, currentX, y, String.valueOf(text.charAt(i)), new Color(r, g, b));
            currentX += mc.textRenderer.getWidth(String.valueOf(text.charAt(i)));
        }
    }



    private void renderPotionEffects(DrawContext context, int y) {
        var effects = mc.player.getStatusEffects();
        if (effects.isEmpty()) return;

        int xOffset = hudX;
        int yOffset = y;

        for (var effect : effects) {
            // Исправленный способ получения имени эффекта
            String name = effect.getEffectType().toString();
            if (name.contains("speed")) name = "Speed";
            else if (name.contains("strength")) name = "Strength";
            else if (name.contains("regeneration")) name = "Regen";
            else if (name.contains("resistance")) name = "Resist";
            else if (name.contains("fire_resistance")) name = "FireRes";
            else if (name.contains("water_breathing")) name = "WaterBr";
            else if (name.contains("night_vision")) name = "NightV";
            else if (name.contains("haste")) name = "Haste";
            else if (name.contains("jump_boost")) name = "Jump";
            else if (name.contains("invisibility")) name = "Invis";
            else name = name.substring(name.lastIndexOf(".") + 1);

            int duration = effect.getDuration() / 20;
            String time = duration > 60 ? (duration / 60) + "m" + (duration % 60) + "s" : duration + "s";

            Color color;
            if (name.contains("Speed")) color = new Color(100, 255, 200);
            else if (name.contains("Strength")) color = new Color(255, 100, 100);
            else if (name.contains("Regen")) color = new Color(255, 100, 200);
            else color = new Color(200, 200, 200);

            String text = "✨ " + name + " " + time;
            if (text.length() > 25) text = text.substring(0, 22) + "...";

            RenderUtil.drawText(context, xOffset, yOffset, text, color);
            yOffset += 11;

            if (yOffset > hudY + 150) break;
        }
    }
    private String getDirection() {
        float yaw = mc.player.getYaw();
        if (yaw < 0) yaw += 360;

        if (yaw >= 315 || yaw < 45) return "N";
        if (yaw >= 45 && yaw < 135) return "E";
        if (yaw >= 135 && yaw < 225) return "S";
        return "W";
    }

    private int getPing() {
        if (mc.getNetworkHandler() == null) return 0;
        PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        return entry != null ? entry.getLatency() : 0;
    }

    private List<Module> getEnabledModules() {
        List<Module> enabled = new ArrayList<>();
        for (Module module : Laguna.getInstance().getModuleManager().getModules()) {
            if (module.isEnabled() && module != this) {
                enabled.add(module);
            }
        }
        return enabled;
    }

    private Color getModuleColor(String name) {
        switch (name) {
            case "KillAura": return new Color(255, 90, 90);
            case "AutoSprint": return new Color(90, 255, 90);
            case "FullBright": return new Color(255, 255, 90);
            case "NoDelay": return new Color(255, 200, 90);
            default: return new Color(200, 200, 200);
        }
    }

    private int getMaxWidth() {
        int max = 0;
        if (showWatermark) max = Math.max(max, mc.textRenderer.getWidth("Laguna Client"));
        if (showEnabledModules) {
            for (Module m : getEnabledModules()) {
                max = Math.max(max, mc.textRenderer.getWidth("» " + m.getName()));
            }
        }
        if (showDirection) max = Math.max(max, mc.textRenderer.getWidth("🧭 N"));
        if (showCoordinates) max = Math.max(max, mc.textRenderer.getWidth("📍 0 0 0"));
        if (showBiome) max = Math.max(max, mc.textRenderer.getWidth("🌿 Plains"));
        if (showFPS) max = Math.max(max, mc.textRenderer.getWidth("⚡ FPS: 999"));
        if (showPing) max = Math.max(max, mc.textRenderer.getWidth("📶 Ping: 999ms"));
        if (showTime) max = Math.max(max, mc.textRenderer.getWidth("🕒 00:00:00"));
        if (showServerIP) max = Math.max(max, mc.textRenderer.getWidth("🌐 localhost"));
        if (showArmorDurability) max = Math.max(max, mc.textRenderer.getWidth("🛡️ Armor: 100%"));
        return max;
    }

    private int getTotalHeight() {
        int height = 0;
        if (showWatermark) height += 14;
        if (showEnabledModules && !getEnabledModules().isEmpty()) height += getEnabledModules().size() * 11 + 3;
        if (showDirection) height += 11;
        if (showCoordinates) height += 11;
        if (showBiome) height += 11;
        if (showFPS) height += 11;
        if (showPing) height += 11;
        if (showTime) height += 11;
        if (showServerIP) height += 11;
        if (showArmorDurability) height += 15;
        return height;
    }

    private boolean hasInfoElements() {
        return showDirection || showCoordinates || showBiome || showFPS || showPing || showTime || showServerIP;
    }
}
package me.deqes.ui.element;

import me.deqes.module.Module;
import me.deqes.module.impl.combat.KillAura;
import me.deqes.module.impl.render.Hud;
import me.deqes.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

import static me.deqes.util.Wrapper.mc;

public class ModuleSettingsMenu {

    private int x, y;
    private int width = 170;
    private int height = 300; // Фиксированная высота
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private Module module;
    private boolean visible = false;
    private int hoveredOption = -1;
    private boolean bindingMode = false;

    public ModuleSettingsMenu(Module module) {
        this.module = module;
    }

    public void show(int moduleX, int moduleY, int moduleHeight) {
        this.x = moduleX;
        this.y = moduleY + moduleHeight + 2;

        if (this.x + width > mc.getWindow().getScaledWidth()) {
            this.x = mc.getWindow().getScaledWidth() - width - 5;
        }

        // Показываем меню ВНИЗУ от модуля, а не сверху
        if (this.y + height > mc.getWindow().getScaledHeight()) {
            this.y = moduleY - height - 2;
        }

        visible = true;
        bindingMode = false;
        scrollOffset = 0;
    }

    public void hide() {
        visible = false;
        hoveredOption = -1;
        bindingMode = false;
        scrollOffset = 0;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isBindingMode() {
        return bindingMode;
    }

    public void setBindingMode(boolean bindingMode) {
        this.bindingMode = bindingMode;
    }

    public void render(DrawContext context) {
        if (!visible) return;

        // Тень
        for (int i = 4; i > 0; i--) {
            RenderUtil.drawRect(context, x - i, y - i, width + i * 2, height + i * 2, new Color(0, 0, 0, 30));
        }

        // Основное меню
        RenderUtil.drawRect(context, x, y, width, height, new Color(20, 20, 25, 250));

        // Обводка
        RenderUtil.drawRect(context, x, y, width, 1, new Color(255, 255, 255, 100));
        RenderUtil.drawRect(context, x, y + height - 1, width, 1, new Color(255, 255, 255, 100));
        RenderUtil.drawRect(context, x, y, 1, height, new Color(255, 255, 255, 50));
        RenderUtil.drawRect(context, x + width - 1, y, 1, height, new Color(255, 255, 255, 50));

        // Цветная полоска сверху
        RenderUtil.drawRect(context, x, y, width, 2, new Color(255, 100, 120, 200));

        // Заголовок
        RenderUtil.drawRect(context, x + 1, y + 2, width - 2, 22, new Color(30, 30, 38, 255));
        RenderUtil.drawText(context, x + 8, y + 11, module.getName(), Color.WHITE);

        // Кнопка закрытия
        RenderUtil.drawRect(context, x + width - 20, y + 5, 15, 15, new Color(40, 40, 50));
        RenderUtil.drawText(context, x + width - 14, y + 10, "X", new Color(200, 200, 200));

        // Разделитель
        RenderUtil.drawRect(context, x, y + 24, width, 1, new Color(60, 60, 70));

        // Обрезаем содержимое по области меню
        RenderUtil.drawRect(context, x, y + 25, width, height - 25, new Color(20, 20, 25, 255));

        // Скролл бар
        if (maxScroll > 0) {
            int barHeight = (int)((height - 30) * ((height - 30) / (float)(getTotalContentHeight())));
            barHeight = Math.max(20, Math.min(barHeight, height - 50));
            int barY = y + 28 + (int)((scrollOffset / (float)maxScroll) * ((height - 30) - barHeight));

            RenderUtil.drawRect(context, x + width - 6, y + 26, 3, height - 28, new Color(40, 40, 50, 150));
            RenderUtil.drawRect(context, x + width - 6, barY, 3, barHeight, new Color(150, 150, 150, 200));
        }

        // Рендер содержимого с обрезкой
        int currentY = y + 32 - scrollOffset;

        if (module instanceof KillAura) {
            renderKillAuraSettings(context, currentY);
        } else if (module instanceof Hud) {
            renderHudSettings(context, currentY);
        } else {
            renderDefaultSettings(context, currentY);
        }
    }

    private int getTotalContentHeight() {
        if (module instanceof KillAura) {
            return 200;
        } else if (module instanceof Hud) {
            return 450;
        } else {
            return 50;
        }
    }

    private void renderKillAuraSettings(DrawContext context, int currentY) {
        KillAura ka = (KillAura) module;

        renderBindOption(context, currentY);
        currentY += 26;

        renderSettingRow(context, currentY, "Mode", ka.getCurrentMode().getName(), hoveredOption == 1);
        currentY += 26;

        renderSettingRow(context, currentY, "Range", String.format("%.1f", ka.getRange()), hoveredOption == 2);
        currentY += 26;

        renderSettingRow(context, currentY, "Delay", String.valueOf(ka.getAttackDelay()), hoveredOption == 3);
        currentY += 26;

        renderCheckbox(context, currentY, "Through Walls", ka.isThroughWalls(), hoveredOption == 4);
        currentY += 26;

        renderCheckbox(context, currentY, "Players Only", ka.isPlayersOnly(), hoveredOption == 5);

        if (!ka.isPlayersOnly()) {
            currentY += 26;
            renderCheckbox(context, currentY, "Attack Mobs", ka.isMobs(), hoveredOption == 6);
        }

        // Обновляем максимальный скролл
        maxScroll = Math.max(0, getTotalContentHeight() - (height - 32));
    }

    private void renderHudSettings(DrawContext context, int currentY) {
        Hud hud = (Hud) module;

        // Bind
        renderBindOption(context, currentY);
        currentY += 26;

        // Разделитель
        if (currentY + 16 > y + 25 && currentY < y + height) {
            RenderUtil.drawRect(context, x, currentY - 4, width, 1, new Color(60, 60, 70));
            RenderUtil.drawText(context, x + 8, currentY, "Display Settings", new Color(180, 180, 180));
        }
        currentY += 18;

        // Основные настройки отображения
        renderCheckbox(context, currentY, "Show Watermark", hud.isShowWatermark(), hoveredOption == 1);
        currentY += 24;

        renderCheckbox(context, currentY, "Show Enabled Modules", hud.isShowEnabledModules(), hoveredOption == 2);
        currentY += 24;

        renderCheckbox(context, currentY, "Show Coordinates", hud.isShowCoordinates(), hoveredOption == 3);
        currentY += 24;

        renderCheckbox(context, currentY, "Show FPS", hud.isShowFPS(), hoveredOption == 4);
        currentY += 24;

        renderCheckbox(context, currentY, "Show Ping", hud.isShowPing(), hoveredOption == 5);
        currentY += 24;

        renderCheckbox(context, currentY, "Show Time", hud.isShowTime(), hoveredOption == 6);
        currentY += 24;

        renderCheckbox(context, currentY, "Show Server IP", hud.isShowServerIP(), hoveredOption == 7);
        currentY += 24;

        renderCheckbox(context, currentY, "Show Direction", hud.isShowDirection(), hoveredOption == 8);
        currentY += 24;

        renderCheckbox(context, currentY, "Show Armor Durability", hud.isShowArmorDurability(), hoveredOption == 9);
        currentY += 28;

        // Разделитель стилей
        if (currentY + 18 < y + height && currentY > y + 25) {
            RenderUtil.drawRect(context, x, currentY - 6, width, 1, new Color(60, 60, 70));
            RenderUtil.drawText(context, x + 8, currentY - 4, "Style Settings", new Color(180, 180, 180));
        }
        currentY += 14;

        renderCheckbox(context, currentY, "Rounded Background", hud.isRoundedBackground(), hoveredOption == 10);
        currentY += 24;

        renderCheckbox(context, currentY, "Gradient Watermark", hud.isGradientWatermark(), hoveredOption == 11);
        currentY += 24;

        renderCheckbox(context, currentY, "Animated", hud.isAnimated(), hoveredOption == 12);
        currentY += 28;

        // Разделитель позиции
        if (currentY + 18 < y + height && currentY > y + 25) {
            RenderUtil.drawRect(context, x, currentY - 6, width, 1, new Color(60, 60, 70));
            RenderUtil.drawText(context, x + 8, currentY - 4, "Position Settings", new Color(180, 180, 180));
        }
        currentY += 14;

        // X позиция
        renderPositionRow(context, currentY, "Hud X", hud.getHudX(), hoveredOption == 13);
        currentY += 26;

        // Y позиция
        renderPositionRow(context, currentY, "Hud Y", hud.getHudY(), hoveredOption == 14);

        // Обновляем максимальный скролл
        maxScroll = Math.max(0, getTotalContentHeight() - (height - 32));
    }

    private void renderPositionRow(DrawContext context, int currentY, String label, int value, boolean hovered) {
        if (currentY + 22 < y + 25 || currentY > y + height) return;

        if (hovered) {
            RenderUtil.drawRect(context, x + 2, currentY, width - 4, 22, new Color(255, 255, 255, 30));
        }
        RenderUtil.drawText(context, x + 8, currentY + 8, label + ": " + value, new Color(220, 220, 220));

        // Кнопки -5 и +5
        RenderUtil.drawRect(context, x + width - 55, currentY, 22, 22, new Color(45, 45, 55));
        RenderUtil.drawText(context, x + width - 47, currentY + 8, "-5", new Color(200, 200, 200));
        RenderUtil.drawRect(context, x + width - 28, currentY, 22, 22, new Color(45, 45, 55));
        RenderUtil.drawText(context, x + width - 20, currentY + 8, "+5", new Color(200, 200, 200));
    }
    private void renderCheckbox(DrawContext context, int currentY, String text, boolean value, boolean hovered) {
        // Проверяем видимость в области скролла
        if (currentY + 22 < y + 25 || currentY > y + height) return;

        int bgY = currentY;

        if (hovered) {
            RenderUtil.drawRect(context, x + 2, bgY, width - 4, 22, new Color(255, 255, 255, 30));
        }
        RenderUtil.drawText(context, x + 8, bgY + 8, text, new Color(220, 220, 220));

        // Чекбокс - увеличил область клика
        int checkX = x + width - 28;
        int checkY = bgY + 4;
        int checkSize = 14;

        // Фон чекбокса
        RenderUtil.drawRect(context, checkX, checkY, checkSize, checkSize, new Color(35, 35, 42));
        // Обводка
        RenderUtil.drawRect(context, checkX, checkY, checkSize, 1, new Color(150, 150, 150));
        RenderUtil.drawRect(context, checkX, checkY + checkSize - 1, checkSize, 1, new Color(150, 150, 150));
        RenderUtil.drawRect(context, checkX, checkY, 1, checkSize, new Color(150, 150, 150));
        RenderUtil.drawRect(context, checkX + checkSize - 1, checkY, 1, checkSize, new Color(150, 150, 150));

        if (value) {
            RenderUtil.drawRect(context, checkX + 4, checkY + 4, 6, 6, Color.WHITE);
        }
    }

    private void renderBindOption(DrawContext context, int currentY) {
        if (currentY + 22 < y + 25 || currentY > y + height) return;

        if (hoveredOption == 0) {
            RenderUtil.drawRect(context, x + 2, currentY, width - 4, 22, new Color(255, 255, 255, 30));
        }
        RenderUtil.drawText(context, x + 8, currentY + 8, "Bind", new Color(220, 220, 220));

        String bindText = bindingMode ? "Press a key..." : getBindName(module.getBind());
        int bindWidth = mc.textRenderer.getWidth(bindText);
        Color bindColor = bindingMode ? new Color(255, 200, 100) : new Color(150, 150, 150);
        RenderUtil.drawText(context, x + width - bindWidth - 12, currentY + 8, bindText, bindColor);
    }

    private void renderSettingRow(DrawContext context, int currentY, String label, String value, boolean hovered) {
        if (currentY + 22 < y + 25 || currentY > y + height) return;

        if (hovered) {
            RenderUtil.drawRect(context, x + 2, currentY, width - 4, 22, new Color(255, 255, 255, 30));
        }
        RenderUtil.drawText(context, x + 8, currentY + 8, label, new Color(220, 220, 220));
        RenderUtil.drawText(context, x + width - 40, currentY + 8, value, new Color(100, 200, 255));
        RenderUtil.drawText(context, x + width - 55, currentY + 8, "<", new Color(150, 150, 150));
        RenderUtil.drawText(context, x + width - 18, currentY + 8, ">", new Color(150, 150, 150));
    }



    private String getBindName(int key) {
        if (key == 0) return "NONE";
        if (key >= 65 && key <= 90) return String.valueOf((char) key);
        if (key >= 48 && key <= 57) return String.valueOf((char) key);
        switch (key) {
            case 32: return "SPACE";
            case 54: return "RALT";
            case 344: return "RSHIFT";
            case 340: return "LSHIFT";
            case 341: return "LCTRL";
            case 345: return "RCTRL";
            case 342: return "LALT";
            case 256: return "ESC";
            case 257: return "ENTER";
            case 258: return "TAB";
            case 259: return "BACKSPACE";
            default: return "KEY_" + key;
        }
    }

    private void renderDefaultSettings(DrawContext context, int startY) {
        renderBindOption(context, startY);
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (!visible) return;

        // Закрытие по X
        if (mouseX >= x + width - 20 && mouseX <= x + width - 5 &&
                mouseY >= y + 5 && mouseY <= y + 20) {
            hide();
            return;
        }

        // Клик вне меню
        if (mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height) {
            if (!bindingMode) {
                hide();
            }
            return;
        }

        // Скролл бар
        if (mouseX >= x + width - 6 && mouseX <= x + width - 3) {
            return;
        }

        if (module instanceof KillAura) {
            handleKillAuraClick(mouseX, mouseY + scrollOffset, button);
        } else if (module instanceof Hud) {
            handleHudClick(mouseX, mouseY + scrollOffset, button);
        } else {
            handleDefaultClick(mouseX, mouseY + scrollOffset, button);
        }
    }

    private void handleDefaultClick(int mouseX, int mouseY, int button) {
        int currentY = y + 32;
        if (mouseY >= currentY && mouseY <= currentY + 22) {
            bindingMode = !bindingMode;
        }
    }

    private void handleKillAuraClick(int mouseX, int mouseY, int button) {
        KillAura ka = (KillAura) module;
        int currentY = y + 32;

        // Bind
        if (mouseY >= currentY && mouseY <= currentY + 22) {
            bindingMode = !bindingMode;
            return;
        }
        currentY += 26;

        // Mode - со стрелками
        if (mouseY >= currentY && mouseY <= currentY + 22) {
            if (mouseX >= x + width - 55 && mouseX <= x + width - 38) {
                KillAura.Mode[] modes = KillAura.Mode.values();
                int prev = (ka.getCurrentMode().ordinal() - 1 + modes.length) % modes.length;
                ka.setCurrentMode(modes[prev]);
            } else if (mouseX >= x + width - 30 && mouseX <= x + width - 13) {
                KillAura.Mode[] modes = KillAura.Mode.values();
                int next = (ka.getCurrentMode().ordinal() + 1) % modes.length;
                ka.setCurrentMode(modes[next]);
            }
            return;
        }
        currentY += 26;

        // Range
        if (mouseY >= currentY && mouseY <= currentY + 22) {
            if (mouseX >= x + width - 55 && mouseX <= x + width - 38) {
                ka.setRange(ka.getRange() - 0.2f);
            } else if (mouseX >= x + width - 30 && mouseX <= x + width - 13) {
                ka.setRange(ka.getRange() + 0.2f);
            }
            return;
        }
        currentY += 26;

        // Delay
        if (mouseY >= currentY && mouseY <= currentY + 22) {
            if (mouseX >= x + width - 55 && mouseX <= x + width - 38) {
                ka.setAttackDelay(ka.getAttackDelay() - 1);
            } else if (mouseX >= x + width - 30 && mouseX <= x + width - 13) {
                ka.setAttackDelay(ka.getAttackDelay() + 1);
            }
            return;
        }
        currentY += 26;

        // Through Walls - чекбокс (вся строка кликабельна)
        if (mouseY >= currentY && mouseY <= currentY + 22) {
            ka.setThroughWalls(!ka.isThroughWalls());
            return;
        }
        currentY += 26;

        // Players Only - чекбокс (вся строка кликабельна)
        if (mouseY >= currentY && mouseY <= currentY + 22) {
            ka.setPlayersOnly(!ka.isPlayersOnly());
            return;
        }
        currentY += 26;

        // Mobs - чекбокс (вся строка кликабельна)
        if (!ka.isPlayersOnly()) {
            if (mouseY >= currentY && mouseY <= currentY + 22) {
                ka.setMobs(!ka.isMobs());
            }
        }
    }

    private void handleHudClick(int mouseX, int mouseY, int button) {
        Hud hud = (Hud) module;
        int currentY = y + 32;

        // Bind - вся строка кликабельна
        if (mouseY >= currentY && mouseY <= currentY + 22) {
            bindingMode = !bindingMode;
            return;
        }
        currentY += 30;

        // Display Settings (1-9) - вся строка кликабельна
        for (int i = 1; i <= 9; i++) {
            if (mouseY >= currentY && mouseY <= currentY + 22) {
                switch (i) {
                    case 1: hud.setShowWatermark(!hud.isShowWatermark()); break;
                    case 2: hud.setShowEnabledModules(!hud.isShowEnabledModules()); break;
                    case 3: hud.setShowCoordinates(!hud.isShowCoordinates()); break;
                    case 4: hud.setShowFPS(!hud.isShowFPS()); break;
                    case 5: hud.setShowPing(!hud.isShowPing()); break;
                    case 6: hud.setShowTime(!hud.isShowTime()); break;
                    case 7: hud.setShowServerIP(!hud.isShowServerIP()); break;
                    case 8: hud.setShowDirection(!hud.isShowDirection()); break;
                    case 9: hud.setShowArmorDurability(!hud.isShowArmorDurability()); break;
                }
                return;
            }
            currentY += 24;
        }
        currentY += 4;

        // Style Settings (10-12)
        for (int i = 10; i <= 12; i++) {
            if (mouseY >= currentY && mouseY <= currentY + 22) {
                switch (i) {
                    case 10: hud.setRoundedBackground(!hud.isRoundedBackground()); break;
                    case 11: hud.setGradientWatermark(!hud.isGradientWatermark()); break;
                    case 12: hud.setAnimated(!hud.isAnimated()); break;
                }
                return;
            }
            currentY += 24;
        }
        currentY += 4;

        // Position Settings (13-14) - только по кнопкам
        if (mouseY >= currentY && mouseY <= currentY + 22) {
            // Кнопка -5
            if (mouseX >= x + width - 55 && mouseX <= x + width - 33) {
                hud.setHudX(hud.getHudX() - 5);
            }
            // Кнопка +5
            else if (mouseX >= x + width - 28 && mouseX <= x + width - 6) {
                hud.setHudX(hud.getHudX() + 5);
            }
            return;
        }
        currentY += 26;

        if (mouseY >= currentY && mouseY <= currentY + 22) {
            // Кнопка -5
            if (mouseX >= x + width - 55 && mouseX <= x + width - 33) {
                hud.setHudY(hud.getHudY() - 5);
            }
            // Кнопка +5
            else if (mouseX >= x + width - 28 && mouseX <= x + width - 6) {
                hud.setHudY(hud.getHudY() + 5);
            }
        }
    }

    public void updateHover(int mouseX, int mouseY) {
        if (!visible) {
            hoveredOption = -1;
            return;
        }

        hoveredOption = -1;

        if (module instanceof KillAura) {
            int currentY = y + 32;
            for (int i = 0; i <= 6; i++) {
                if (mouseY >= currentY && mouseY <= currentY + 22) {
                    if (i == 6 && ((KillAura) module).isPlayersOnly()) break;
                    hoveredOption = i;
                    break;
                }
                currentY += 26;
            }
        } else if (module instanceof Hud) {
            int currentY = y + 32;

            if (mouseY >= currentY && mouseY <= currentY + 22) {
                hoveredOption = 0;
                return;
            }
            currentY += 30;

            for (int i = 1; i <= 9; i++) {
                if (mouseY >= currentY && mouseY <= currentY + 22) {
                    hoveredOption = i;
                    return;
                }
                currentY += 24;
            }
            currentY += 4;

            for (int i = 10; i <= 12; i++) {
                if (mouseY >= currentY && mouseY <= currentY + 22) {
                    hoveredOption = i;
                    return;
                }
                currentY += 24;
            }
            currentY += 4;

            if (mouseY >= currentY && mouseY <= currentY + 22) {
                hoveredOption = 13;
                return;
            }
            currentY += 26;

            if (mouseY >= currentY && mouseY <= currentY + 22) {
                hoveredOption = 14;
            }
        } else {
            int currentY = y + 32;
            if (mouseY >= currentY && mouseY <= currentY + 22) {
                hoveredOption = 0;
            }
        }
    }

    public void mouseScrolled(int amount) {
        if (!visible) return;
        scrollOffset -= amount * 15;
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
    }

    public void keyPressed(int key) {
        if (bindingMode) {
            if (key == 256) {
                module.setBind(0);
            } else {
                module.setBind(key);
            }
            bindingMode = false;
        }
    }
}
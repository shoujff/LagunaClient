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
    private int width = 160;
    private int height = 280;
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

        if (this.y + height > mc.getWindow().getScaledHeight()) {
            this.y = moduleY - height - 2;
        }

        visible = true;
        bindingMode = false;
    }

    public void hide() {
        visible = false;
        hoveredOption = -1;
        bindingMode = false;
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

        // Затемнение фона
        RenderUtil.drawRect(context, x - 2, y - 2, width + 4, height + 4, new Color(0, 0, 0, 200));

        // Основное меню
        RenderUtil.drawRect(context, x, y, width, height, new Color(25, 25, 30, 255));

        // Обводка
        RenderUtil.drawRect(context, x, y, width, 1, Color.WHITE);
        RenderUtil.drawRect(context, x, y + height - 1, width, 1, Color.WHITE);
        RenderUtil.drawRect(context, x, y, 1, height, Color.WHITE);
        RenderUtil.drawRect(context, x + width - 1, y, 1, height, Color.WHITE);

        // Заголовок
        RenderUtil.drawRect(context, x + 1, y + 1, width - 2, 22, new Color(40, 40, 48, 255));
        RenderUtil.drawText(context, x + 8, y + 9, module.getName(), Color.WHITE);

        // Кнопка закрытия
        RenderUtil.drawText(context, x + width - 15, y + 8, "X", new Color(200, 200, 200));

        // Разделитель
        RenderUtil.drawRect(context, x, y + 23, width, 1, new Color(60, 60, 70));

        int currentY = y + 30;

        // Определяем тип модуля и рендерим соответствующие настройки
        if (module instanceof KillAura) {
            renderKillAuraSettings(context, currentY);
        } else if (module instanceof Hud) {
            renderHudSettings(context, currentY);
        } else {
            renderDefaultSettings(context, currentY);
        }
    }

    private void renderKillAuraSettings(DrawContext context, int startY) {
        KillAura ka = (KillAura) module;
        int currentY = startY;

        // Bind
        renderBindOption(context, currentY);
        currentY += 24;

        // Режим
        if (hoveredOption == 1) {
            RenderUtil.drawRect(context, x + 2, currentY, width - 4, 20, new Color(255, 255, 255, 30));
        }
        RenderUtil.drawText(context, x + 8, currentY + 7, "Mode", new Color(220, 220, 220));

        String modeText = ka.getCurrentMode().getName();
        int modeWidth = mc.textRenderer.getWidth(modeText);
        RenderUtil.drawText(context, x + width - modeWidth - 10, currentY + 7, modeText, new Color(100, 200, 255));

        RenderUtil.drawText(context, x + width - modeWidth - 25, currentY + 7, "<", new Color(150, 150, 150));
        RenderUtil.drawText(context, x + width - 8, currentY + 7, ">", new Color(150, 150, 150));

        currentY += 24;

        // Дальность
        if (hoveredOption == 2) {
            RenderUtil.drawRect(context, x + 2, currentY, width - 4, 20, new Color(255, 255, 255, 30));
        }
        RenderUtil.drawText(context, x + 8, currentY + 7, "Range: " + String.format("%.1f", ka.getRange()), new Color(220, 220, 220));
        RenderUtil.drawText(context, x + width - 25, currentY + 7, "<", new Color(150, 150, 150));
        RenderUtil.drawText(context, x + width - 8, currentY + 7, ">", new Color(150, 150, 150));

        currentY += 24;

        // Задержка
        if (hoveredOption == 3) {
            RenderUtil.drawRect(context, x + 2, currentY, width - 4, 20, new Color(255, 255, 255, 30));
        }
        RenderUtil.drawText(context, x + 8, currentY + 7, "Delay: " + ka.getAttackDelay(), new Color(220, 220, 220));
        RenderUtil.drawText(context, x + width - 25, currentY + 7, "<", new Color(150, 150, 150));
        RenderUtil.drawText(context, x + width - 8, currentY + 7, ">", new Color(150, 150, 150));

        currentY += 24;

        // Through Walls
        renderCheckbox(context, currentY, "Through Walls", ka.isThroughWalls(), hoveredOption == 4);
        currentY += 24;

        // Players Only
        renderCheckbox(context, currentY, "Players Only", ka.isPlayersOnly(), hoveredOption == 5);
        currentY += 24;

        // Mobs
        if (!ka.isPlayersOnly()) {
            renderCheckbox(context, currentY, "Attack Mobs", ka.isMobs(), hoveredOption == 6);
        }
    }

    private void renderHudSettings(DrawContext context, int startY) {
        Hud hud = (Hud) module;
        int currentY = startY;

        // Bind
        renderBindOption(context, currentY);
        currentY += 24;

        // Watermark
        renderCheckbox(context, currentY, "Show Watermark", hud.isShowWatermark(), hoveredOption == 1);
        currentY += 24;

        // Enabled Modules
        renderCheckbox(context, currentY, "Show Enabled Modules", hud.isShowEnabledModules(), hoveredOption == 2);
        currentY += 24;

        // Coordinates
        renderCheckbox(context, currentY, "Show Coordinates", hud.isShowCoordinates(), hoveredOption == 3);
        currentY += 24;

        // FPS
        renderCheckbox(context, currentY, "Show FPS", hud.isShowFPS(), hoveredOption == 4);
        currentY += 24;

        // Ping
        renderCheckbox(context, currentY, "Show Ping", hud.isShowPing(), hoveredOption == 5);
        currentY += 24;

        // Разделитель
        RenderUtil.drawRect(context, x, currentY, width, 1, new Color(60, 60, 70));
        currentY += 8;

        // Настройки позиции
        RenderUtil.drawText(context, x + 8, currentY, "Position Settings", new Color(180, 180, 180));
        currentY += 16;

        // X позиция
        if (hoveredOption == 6) {
            RenderUtil.drawRect(context, x + 2, currentY, width - 4, 20, new Color(255, 255, 255, 30));
        }
        RenderUtil.drawText(context, x + 8, currentY + 7, "X: " + hud.getWatermarkX(), new Color(220, 220, 220));
        RenderUtil.drawText(context, x + width - 25, currentY + 7, "<", new Color(150, 150, 150));
        RenderUtil.drawText(context, x + width - 8, currentY + 7, ">", new Color(150, 150, 150));
        currentY += 24;

        // Y позиция
        if (hoveredOption == 7) {
            RenderUtil.drawRect(context, x + 2, currentY, width - 4, 20, new Color(255, 255, 255, 30));
        }
        RenderUtil.drawText(context, x + 8, currentY + 7, "Y: " + hud.getWatermarkY(), new Color(220, 220, 220));
        RenderUtil.drawText(context, x + width - 25, currentY + 7, "<", new Color(150, 150, 150));
        RenderUtil.drawText(context, x + width - 8, currentY + 7, ">", new Color(150, 150, 150));
    }

    private void renderBindOption(DrawContext context, int currentY) {
        if (hoveredOption == 0) {
            RenderUtil.drawRect(context, x + 2, currentY, width - 4, 20, new Color(255, 255, 255, 30));
        }
        RenderUtil.drawText(context, x + 8, currentY + 7, "Bind", new Color(220, 220, 220));

        String bindText = bindingMode ? "Press a key..." : getBindName(module.getBind());
        int bindWidth = mc.textRenderer.getWidth(bindText);
        Color bindColor = bindingMode ? new Color(255, 200, 100) : new Color(150, 150, 150);
        RenderUtil.drawText(context, x + width - bindWidth - 10, currentY + 7, bindText, bindColor);
    }

    private void renderCheckbox(DrawContext context, int currentY, String text, boolean value, boolean hovered) {
        if (hovered) {
            RenderUtil.drawRect(context, x + 2, currentY, width - 4, 20, new Color(255, 255, 255, 30));
        }
        RenderUtil.drawText(context, x + 8, currentY + 7, text, new Color(220, 220, 220));

        int checkX = x + width - 25;
        int checkY = currentY + 3;
        RenderUtil.drawRect(context, checkX, checkY, 14, 14, new Color(30, 30, 35, 255));
        RenderUtil.drawRect(context, checkX, checkY, 14, 1, Color.WHITE);
        RenderUtil.drawRect(context, checkX, checkY + 13, 14, 1, Color.WHITE);
        RenderUtil.drawRect(context, checkX, checkY, 1, 14, Color.WHITE);
        RenderUtil.drawRect(context, checkX + 13, checkY, 1, 14, Color.WHITE);

        if (value) {
            RenderUtil.drawRect(context, checkX + 4, checkY + 4, 6, 6, Color.WHITE);
        }
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

        if (module instanceof KillAura) {
            handleKillAuraClick(mouseX, mouseY, button);
        } else if (module instanceof Hud) {
            handleHudClick(mouseX, mouseY, button);
        } else {
            handleDefaultClick(mouseX, mouseY, button);
        }
    }

    private void handleDefaultClick(int mouseX, int mouseY, int button) {
        int currentY = y + 30;

        if (mouseY >= currentY && mouseY <= currentY + 20) {
            bindingMode = !bindingMode;
        }
    }

    private void handleKillAuraClick(int mouseX, int mouseY, int button) {
        KillAura ka = (KillAura) module;
        int currentY = y + 30;

        // Bind
        if (mouseY >= currentY && mouseY <= currentY + 20) {
            bindingMode = !bindingMode;
            return;
        }
        currentY += 24;

        // Mode
        if (mouseY >= currentY && mouseY <= currentY + 20) {
            if (mouseX >= x + width - 35 && mouseX <= x + width - 20) {
                KillAura.Mode[] modes = KillAura.Mode.values();
                int prevIndex = (ka.getCurrentMode().ordinal() - 1 + modes.length) % modes.length;
                ka.setCurrentMode(modes[prevIndex]);
            } else if (mouseX >= x + width - 18 && mouseX <= x + width - 5) {
                KillAura.Mode[] modes = KillAura.Mode.values();
                int nextIndex = (ka.getCurrentMode().ordinal() + 1) % modes.length;
                ka.setCurrentMode(modes[nextIndex]);
            }
            return;
        }
        currentY += 24;

        // Range
        if (mouseY >= currentY && mouseY <= currentY + 20) {
            if (mouseX >= x + width - 35 && mouseX <= x + width - 20) {
                ka.setRange(ka.getRange() - 0.2f);
            } else if (mouseX >= x + width - 18 && mouseX <= x + width - 5) {
                ka.setRange(ka.getRange() + 0.2f);
            }
            return;
        }
        currentY += 24;

        // Delay
        if (mouseY >= currentY && mouseY <= currentY + 20) {
            if (mouseX >= x + width - 35 && mouseX <= x + width - 20) {
                ka.setAttackDelay(ka.getAttackDelay() - 1);
            } else if (mouseX >= x + width - 18 && mouseX <= x + width - 5) {
                ka.setAttackDelay(ka.getAttackDelay() + 1);
            }
            return;
        }
        currentY += 24;

        // Through Walls
        if (mouseY >= currentY && mouseY <= currentY + 20) {
            ka.setThroughWalls(!ka.isThroughWalls());
            return;
        }
        currentY += 24;

        // Players Only
        if (mouseY >= currentY && mouseY <= currentY + 20) {
            ka.setPlayersOnly(!ka.isPlayersOnly());
            if (!ka.isPlayersOnly()) {
                ka.setMobs(true);
            }
            return;
        }
        currentY += 24;

        // Mobs
        if (!ka.isPlayersOnly()) {
            if (mouseY >= currentY && mouseY <= currentY + 20) {
                ka.setMobs(!ka.isMobs());
            }
        }
    }

    private void handleHudClick(int mouseX, int mouseY, int button) {
        Hud hud = (Hud) module;
        int currentY = y + 30;

        // Bind
        if (mouseY >= currentY && mouseY <= currentY + 20) {
            bindingMode = !bindingMode;
            return;
        }
        currentY += 24;

        // Watermark
        if (mouseY >= currentY && mouseY <= currentY + 20) {
            hud.setShowWatermark(!hud.isShowWatermark());
            return;
        }
        currentY += 24;

        // Enabled Modules
        if (mouseY >= currentY && mouseY <= currentY + 20) {
            hud.setShowEnabledModules(!hud.isShowEnabledModules());
            return;
        }
        currentY += 24;

        // Coordinates
        if (mouseY >= currentY && mouseY <= currentY + 20) {
            hud.setShowCoordinates(!hud.isShowCoordinates());
            return;
        }
        currentY += 24;

        // FPS
        if (mouseY >= currentY && mouseY <= currentY + 20) {
            hud.setShowFPS(!hud.isShowFPS());
            return;
        }
        currentY += 24;

        // Ping
        if (mouseY >= currentY && mouseY <= currentY + 20) {
            hud.setShowPing(!hud.isShowPing());
            return;
        }
        currentY += 28; // +4 за разделитель

        // X position
        if (mouseY >= currentY && mouseY <= currentY + 20) {
            if (mouseX >= x + width - 35 && mouseX <= x + width - 20) {
                hud.setWatermarkX(hud.getWatermarkX() - 5);
            } else if (mouseX >= x + width - 18 && mouseX <= x + width - 5) {
                hud.setWatermarkX(hud.getWatermarkX() + 5);
            }
            return;
        }
        currentY += 24;

        // Y position
        if (mouseY >= currentY && mouseY <= currentY + 20) {
            if (mouseX >= x + width - 35 && mouseX <= x + width - 20) {
                hud.setWatermarkY(hud.getWatermarkY() - 5);
            } else if (mouseX >= x + width - 18 && mouseX <= x + width - 5) {
                hud.setWatermarkY(hud.getWatermarkY() + 5);
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
            updateKillAuraHover(mouseX, mouseY);
        } else if (module instanceof Hud) {
            updateHudHover(mouseX, mouseY);
        } else {
            int currentY = y + 30;
            if (mouseY >= currentY && mouseY <= currentY + 20) {
                hoveredOption = 0;
            }
        }
    }

    private void updateKillAuraHover(int mouseX, int mouseY) {
        int currentY = y + 30;
        for (int i = 0; i <= 6; i++) {
            if (mouseY >= currentY && mouseY <= currentY + 20) {
                KillAura ka = (KillAura) module;
                if (i == 6 && ka.isPlayersOnly()) {
                    break;
                }
                hoveredOption = i;
                break;
            }
            currentY += 24;
        }
    }

    private void updateHudHover(int mouseX, int mouseY) {
        int currentY = y + 30;
        for (int i = 0; i <= 7; i++) {
            if (mouseY >= currentY && mouseY <= currentY + 20) {
                hoveredOption = i;
                break;
            }
            currentY += 24;
            // Пропускаем разделитель
            if (i == 5) {
                currentY += 8;
            }
        }
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
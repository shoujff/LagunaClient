package me.deqes.ui.element;

import lombok.Getter;
import lombok.Setter;
import me.deqes.module.Module;
import me.deqes.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

import static me.deqes.util.Wrapper.mc;

public class ModuleElement {
    @Getter @Setter
    private int x, y, width, height;
    @Getter
    private Module module;
    @Getter
    private ModuleSettingsMenu settingsMenu;

    public ModuleElement(Module module) {
        this.module = module;
        this.settingsMenu = new ModuleSettingsMenu(module);
    }

    public void render(DrawContext context) {
        // Фон
        if (module.isEnabled()) {
            RenderUtil.drawRect(context, x, y, width, height, new Color(35, 70, 35, 220));
        } else {
            RenderUtil.drawRect(context, x, y, width, height, new Color(35, 35, 40, 200));
        }

        // Левая полоска
        Color barColor = module.isEnabled() ? new Color(80, 200, 80) : new Color(100, 100, 110);
        RenderUtil.drawRect(context, x, y, 3, height, barColor);

        // Эффект наведения
        if (isHovered()) {
            RenderUtil.drawRect(context, x, y, width, height, new Color(255, 255, 255, 15));
        }

        // Название
        Color textColor = module.isEnabled() ? new Color(255, 255, 255) : new Color(200, 200, 200);
        RenderUtil.drawText(context, x + 10, y + height/2 - 4, module.getName(), textColor);

        // Бинд (если есть)
        if (module.getBind() != 0) {
            String bindText = getBindName(module.getBind());
            int bindWidth = mc.textRenderer.getWidth(bindText);
            RenderUtil.drawText(context, x + width - bindWidth - 50, y + height/2 - 4, bindText, new Color(150, 150, 150));
        }

        // Статус
        String status = module.isEnabled() ? "ON" : "OFF";
        int statusWidth = mc.textRenderer.getWidth(status);
        Color statusColor = module.isEnabled() ? new Color(80, 200, 80) : new Color(150, 150, 150);
        RenderUtil.drawText(context, x + width - statusWidth - 8, y + height/2 - 4, status, statusColor);
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        // Сначала проверяем клик по меню
        if (settingsMenu.isVisible()) {
            settingsMenu.mouseClicked(mouseX, mouseY, button);
            return;
        }

        if (!isHovered(mouseX, mouseY)) return;

        if (button == 0) { // ЛКМ
            module.Toggle();
        } else if (button == 1) { // ПКМ - меню под модулем
            settingsMenu.show(this.x, this.y, this.height);
        }
    }

    public void updateHover(int mouseX, int mouseY) {
        settingsMenu.updateHover(mouseX, mouseY);
    }

    public void keyPressed(int key) {
        if (settingsMenu.isVisible() && settingsMenu.isBindingMode()) {
            settingsMenu.keyPressed(key);
        }
    }

    public void renderMenu(DrawContext context) {
        settingsMenu.render(context);
    }

    private String getBindName(int key) {
        if (key == 0) return "";
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

    private boolean isHovered() {
        return isHovered(-1, -1);
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
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
    private boolean binding = false;

    public ModuleElement(Module module) {
        this.module = module;
    }

    public void render(DrawContext context) {
        // Фон
        if (module.isToggled()) {
            RenderUtil.drawRect(context, x, y, width, height, new Color(35, 70, 35, 220));
        } else {
            RenderUtil.drawRect(context, x, y, width, height, new Color(35, 35, 40, 200));
        }

        // Левая цветная полоска
        Color barColor = module.isToggled() ? new Color(80, 200, 80) : new Color(100, 100, 110);
        RenderUtil.drawRect(context, x, y, 3, height, barColor);

        // Эффект при наведении
        if (isHovered()) {
            RenderUtil.drawRect(context, x, y, width, height, new Color(255, 255, 255, 15));
        }

        // Название
        if (binding) {
            RenderUtil.drawText(context, x + 10, y + height/2 - 4, "Press a key...",
                    new Color(255, 200, 100));
        } else {
            Color textColor = module.isToggled() ? new Color(255, 255, 255) : new Color(200, 200, 200);
            RenderUtil.drawText(context, x + 10, y + height/2 - 4, module.getName(), textColor);
        }

        // Статус ON/OFF
        String status = module.isToggled() ? "ON" : "OFF";
        int statusWidth = mc.textRenderer.getWidth(status);
        Color statusColor = module.isToggled() ? new Color(80, 200, 80) : new Color(150, 150, 150);
        RenderUtil.drawText(context, x + width - statusWidth - 8, y + height/2 - 4, status, statusColor);
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (!isHovered(mouseX, mouseY)) return;

        if (button == 0) { // ЛКМ - включить/выключить
            module.Toggle();
        } else if (button == 2) { // ПКМ - начать биндинг
            binding = true;
        }
    }

    public void keyPressed(int key) {
        if (binding) {
            module.setBind(key);
            binding = false;
        }
    }

    public boolean isBinding() {
        return binding;
    }

    public void setBinding(boolean binding) {
        this.binding = binding;
    }

    private boolean isHovered() {
        return isHovered(-1, -1);
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
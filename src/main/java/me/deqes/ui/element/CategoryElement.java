package me.deqes.ui.element;

import lombok.Getter;
import lombok.Setter;
import me.deqes.module.Category;
import me.deqes.ui.ClickGuiScreen;
import me.deqes.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

import static me.deqes.util.Wrapper.mc;

public class CategoryElement {
    @Getter @Setter
    private int x, y, width, height;
    @Getter
    private Category category;
    private boolean selected = false;
    private ClickGuiScreen parent;

    public CategoryElement(Category category) {
        this.category = category;
    }

    public void setParent(ClickGuiScreen parent) {
        this.parent = parent;
    }

    public void render(DrawContext context) {
        // Белая обводка
        RenderUtil.drawRect(context, x, y, width, 1, new Color(255, 255, 255, 255));
        RenderUtil.drawRect(context, x, y + height - 1, width, 1, new Color(255, 255, 255, 255));
        RenderUtil.drawRect(context, x, y, 1, height, new Color(255, 255, 255, 255));
        RenderUtil.drawRect(context, x + width - 1, y, 1, height, new Color(255, 255, 255, 255));

        // Фон
        if (selected) {
            RenderUtil.drawRect(context, x, y, width, height, new Color(40, 40, 40, 220));
        } else {
            RenderUtil.drawRect(context, x, y, width, height, new Color(25, 25, 25, 200));
        }

        // Левая полоска
        if (selected) {
            RenderUtil.drawRect(context, x, y, 3, height, new Color(255, 255, 255));
        }

        // Эффект наведения
        if (isHovered()) {
            RenderUtil.drawRect(context, x, y, width, height, new Color(255, 255, 255, 20));
        }

        // Иконка
        String icon = getIcon();
        RenderUtil.drawText(context, x + 8, y + height/2 - 4, icon, new Color(200, 200, 200));

        // Название
        Color textColor = selected ? new Color(255, 255, 255) : new Color(180, 180, 180);
        RenderUtil.drawText(context, x + 28, y + height/2 - 4, category.name(), textColor);

        // Разделитель
        RenderUtil.drawRect(context, x, y + height - 1, width, 1, new Color(50, 50, 60));
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private String getIcon() {
        switch (category) {
            case COMBAT: return "⚔";
            case MOVEMENT: return "🏃";
            case RENDER: return "🎨";
            case MISC: return "🔧";
            case PLAYER: return "👤";
            default: return "📦";
        }
    }

    public void mouseClicked(int mouseX, int mouseY) {
        if (isHovered(mouseX, mouseY)) {
            if (parent != null && parent.selectedCategory != null) {
                parent.selectedCategory.setSelected(false);
            }
            if (parent != null) {
                parent.selectedCategory = this;
            }
            selected = true;
        }
    }

    private boolean isHovered() {
        return isHovered(-1, -1);
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
package me.deqes.ui.element;

import lombok.Getter;
import lombok.Setter;
import me.deqes.module.Category;
import me.deqes.ui.ClickGuiScreen;
import me.deqes.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class CategoryElement {
    @Getter @Setter
    private int x, y, width, height;
    @Getter
    private Category category;
    private boolean selected = false;

    public CategoryElement(Category category) {
        this.category = category;
    }

    public void render(DrawContext context) {
        // Фон
        if (selected) {
            RenderUtil.drawRect(context, x, y, width, height, new Color(40, 120, 40, 220));
        } else {
            RenderUtil.drawRect(context, x, y, width, height, new Color(30, 30, 35, 200));
        }

        // Левая полоска для выбранной категории
        if (selected) {
            RenderUtil.drawRect(context, x, y, 3, height, new Color(80, 200, 80));
        }

        // Эффект при наведении
        if (isHovered()) {
            RenderUtil.drawRect(context, x, y, width, height, new Color(255, 255, 255, 20));
        }

        // Иконка
        String icon = getIcon();
        RenderUtil.drawText(context, x + 8, y + height/2 - 4, icon, new Color(180, 180, 180));

        // Название
        Color textColor = selected ? new Color(80, 200, 80) : new Color(220, 220, 220);
        RenderUtil.drawText(context, x + 28, y + height/2 - 4, category.name(), textColor);

        // Разделитель
        RenderUtil.drawRect(context, x, y + height - 1, width, 1, new Color(50, 50, 60));
    }

    private String getIcon() {
        switch (category) {
            case COMBAT: return "⚔️";
            case MOVEMENT: return "🏃";
            case RENDER: return "🎨";
            case MISC: return "🔧";
            case PLAYER: return "👤";
            default: return "📦";
        }
    }

    public void mouseClicked(int mouseX, int mouseY) {
        if (isHovered(mouseX, mouseY)) {
            if (ClickGuiScreen.selectedCategory != null) {
                ClickGuiScreen.selectedCategory.selected = false;
            }
            ClickGuiScreen.selectedCategory = this;
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
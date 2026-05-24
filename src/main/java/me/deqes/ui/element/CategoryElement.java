package me.deqes.ui.element;

import lombok.Getter;
import me.deqes.module.Category;
import me.deqes.ui.ClickGuiScreen;
import me.deqes.ui.buttons.ModuleButton;
import me.deqes.util.RenderUtil;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class CategoryElement {
    int x, y;
    int width = 100,  height = 20;
    @Getter
    Category category;
    boolean selected = false;

    public CategoryElement(Category category)
    {
        this.category = category;
    }
    Color color;
    public void render(DrawContext context, int x, int y){
        this.x = x;
        this.y = y;

        if(selected){
            color = new Color(4, 74, 4);
        } else{
            color = new Color(32, 32, 32);
        }
        RenderUtil.drawRect(context, x, y, width, height, color);
        RenderUtil.drawText(context, x, y, category.name(), new Color(255,255,255));

    }
    public void mouseClicked(int mouseX, int mouseY)
    {
        if(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height){

            if(ClickGuiScreen.selectedCategory != null){
                ClickGuiScreen.selectedCategory.selected = false;
            }
            ClickGuiScreen.selectedCategory = this;
            selected = true;
        }
    }
}

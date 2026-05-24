package me.deqes.ui.buttons;

import lombok.Getter;
import me.deqes.Laguna;
import me.deqes.module.Category;
import me.deqes.module.Module;
import me.deqes.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryButton
{
    @Getter
    Category category;
    @Getter
    int x, y;

    final int width=100, height = 20;
    @Getter
    List<ModuleButton> buttons = new ArrayList<>();

    public CategoryButton(Category category){
        this.category = category;
        for(Module module : Laguna.getInstance().getModuleManager().getModules()){
            buttons.add(new ModuleButton(module));
        }
    }
    public void render(DrawContext context, int x, int y){
       this.x = x;
       this.y = y;

        RenderUtil.drawRect(context, x, y, width, height, new Color(255,255,255));
        RenderUtil.drawText(context, x+5, y+5, category.name(), new Color(0,0,0));
        int moduleY = y + 30;

        for (ModuleButton button : buttons){
            if(button.getModule().getCategory() == category) {
                button.render(context, x, moduleY);
                moduleY += 35;
            }
        }
    }

}

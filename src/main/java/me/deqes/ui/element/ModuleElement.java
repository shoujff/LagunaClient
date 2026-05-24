package me.deqes.ui.element;

import lombok.Getter;
import me.deqes.module.Module;
import me.deqes.ui.ClickGuiScreen;
import me.deqes.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class ModuleElement {
    int x, y;
    int width = 100, height = 20;
    @Getter
    Module module;
    Color color;
    boolean binding = false;

    public ModuleElement(Module module){
        this.module = module;
    }
    public void render(DrawContext context, int x, int y){
        this.x = x;
        this.y = y;

        if(module.isToggled()){
            color = new Color(4, 74, 4);
        } else{
            color = new Color(32,32,32);
        }

        RenderUtil.drawRect(context, x, y, width, height, color);
        RenderUtil.drawText(context, x, y, binding ? "Press a key" : module.getName(), new Color(255,255,255));
    }
    public void mouseClicked(int mouseX, int mouseY, int key)
    {
        if(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height){
            if (key == 0){
                module.Toggle();
            } else if (key == 1){

            } else if (key == 2){
                binding = true;
            }

        }
    }
    public void keyPressed(int key){
        if(binding){
            module.setBind(key);
        }
    }


}

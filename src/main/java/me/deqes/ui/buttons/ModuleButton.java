package me.deqes.ui.buttons;

import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import me.deqes.Laguna;
import me.deqes.event.EventMouse;
import me.deqes.module.Module;
import me.deqes.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class ModuleButton {


    @Getter
    int x, y;

    @Getter
    Module module;

    @Getter
    final int width = 100, height = 30;

    Color buttonColor;

    public ModuleButton(Module module){
        this.module = module;
        Laguna.getInstance().getEventBus().register(this);
    }
    public void render(DrawContext context, int x, int y){
        this.x = x;
        this.y= y;
        if(module.isToggled()){
            buttonColor = new Color(0,255,0);

        } else {
            buttonColor = new Color(255,255,255);
        }

        RenderUtil.drawRect(context, x, y, width, height, buttonColor);
        RenderUtil.drawText(context, x + 5, y + 5, module.getName(), new Color(0,0,0));
    }
    @Subscribe
    public void onMouse(EventMouse e)
    {
        if(e.getAction() != 1) return;
        if (e.getMouseX() >= x && e.getMouseX() <= x + width && e.getMouseY() >= y && e.getMouseY() <= y + height) {
            module.Toggle();
        }

    }

}

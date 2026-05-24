package me.deqes.ui;

import com.google.common.eventbus.Subscribe;
import me.deqes.Laguna;
import me.deqes.event.EventKey;
import me.deqes.event.EventMessage;
import me.deqes.event.EventMouse;
import me.deqes.event.EventRender;
import me.deqes.module.Category;
import me.deqes.module.Module;
import me.deqes.ui.buttons.CategoryButton;
import me.deqes.ui.buttons.ModuleButton;
import me.deqes.ui.element.CategoryElement;
import me.deqes.ui.element.ModuleElement;
import me.deqes.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static me.deqes.util.Wrapper.mc;

public class ClickGuiScreen  extends Screen {

    public ClickGuiScreen(){
        super(Text.of("ClickGui"));
        for(Category category : Category.values()){
            categoryElements.add(new CategoryElement(category));
        }
        for(Module module : Laguna.getInstance().getModuleManager().getModules()){
            moduleElements.add(new ModuleElement(module));
        }
        Laguna.getInstance().getEventBus().register(this);
    }
    int centerX = mc.getWindow().getScaledWidth() / 2;
    int centerY = mc.getWindow().getScaledHeight() / 2;

    public static CategoryElement selectedCategory = null;

    List<CategoryElement> categoryElements = new ArrayList<>();
    List<ModuleElement> moduleElements = new ArrayList<>();

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks)
    {
        RenderUtil.drawRect(context, centerX - 400/2, centerY - 250/2, 400, 250, new Color(0, 0, 0));
        RenderUtil.drawText(context,0.9f, centerX - 195 , centerY - 120,"Laguna Client", new Color(255,255,255) );
        int categoryY = centerY - 100;
        for(CategoryElement categoryElement : categoryElements){
            categoryElement.render(context, centerX - 190, categoryY);
            categoryY += 20 + 5;
        }
        if(selectedCategory == null) return;

        List<ModuleElement> m1 = new ArrayList<>();
        List<ModuleElement> m2 = new ArrayList<>();
        int c = 0;
        for(ModuleElement moduleElement : moduleElements){

            if(moduleElement.getModule().getCategory() != selectedCategory.getCategory()) continue;

            if(c % 2 == 0){
                m1.add(moduleElement);
            } else{
                m2.add(moduleElement);
            }

            c++;
        }
        int m1Y = centerY - 100;
        for(ModuleElement moduleElement : m1){
            moduleElement.render(context, centerX - 50, m1Y);
            m1Y += 20+ 3;

        }
        int m2Y = centerY - 100;
        for(ModuleElement moduleElement : m2){
            moduleElement.render(context, centerX + 60, m2Y);
            m2Y += 20+ 3;

        }
    }
    @Subscribe
    public void onMouse(EventMouse e){
        if(e.getAction() != 1 ) return;
        for(CategoryElement categoryElement : categoryElements){
            categoryElement.mouseClicked((int) e.getMouseX(), (int) e.getMouseY());
        }
        for(ModuleElement moduleElement : moduleElements){
            if(moduleElement.getModule().getCategory() != selectedCategory.getCategory()) continue;

            moduleElement.mouseClicked((int) e.getMouseX(),(int) e.getMouseY(), e.getKey());

        }
    }
    @Subscribe
    public void onKey(EventKey e){
        if(e.getAction() != 1) return;
        for(ModuleElement moduleElement : moduleElements){
            moduleElement.keyPressed(e.getKey());

        }
    }

    @Override
    public void removed(){
        Laguna.getInstance().getEventBus().unregister(this);
    }
}

package me.deqes.module;

import lombok.Getter;
import lombok.Setter;
import me.deqes.Laguna;

public class Module {
    @Getter
    String name;
    @Getter
    Category category;
    @Getter @Setter
    int bind;
    @Getter @Setter
    boolean toggled;


    public Module(String name, Category category, int bind)
    {
        this.name = name;
        this.category = category;
        this.bind = bind;
    }

    public void onEnable(){
        Laguna.getInstance().getEventBus().register(this);
        System.out.println("e");
    }
    public void onDisable(){
        Laguna.getInstance().getEventBus().unregister(this);
        System.out.println("d");

    }
    public boolean isEnabled() {
        return toggled;
    }
    public void Toggle(){
        if(toggled){
            toggled = false;
            onDisable();
            System.out.println("d");
        } else{
            toggled = true;
            onEnable();
            System.out.println("e");
        }
    }
}

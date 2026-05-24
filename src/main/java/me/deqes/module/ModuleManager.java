package me.deqes.module;

import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import me.deqes.Laguna;
import me.deqes.event.EventKey;
import me.deqes.module.impl.render.Watermark;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    @Getter
    List<Module> modules = new ArrayList<>();
    public ModuleManager()
    {
        Laguna.getInstance().getEventBus().register(this);
        addModules( new Watermark());
    }
    private void addModules(Module... modules){
        for(Module module : modules){
            this.modules.add(module);
        }
    }
    @Subscribe
    public void onKey(EventKey e)
    {
        if(e.getAction() != 1) return;
        for(Module module : modules){
            if(module.getBind() == e.getKey()){
                module.Toggle();
            }
        }
    }
}

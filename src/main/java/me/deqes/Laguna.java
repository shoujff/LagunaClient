package me.deqes;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import lombok.Synchronized;
import me.deqes.command.CommandManager;
import me.deqes.event.EventKey;
import me.deqes.event.EventTick;
import me.deqes.module.ModuleManager;
import me.deqes.ui.ClickGuiScreen;
import net.fabricmc.api.ModInitializer;

import static me.deqes.util.Wrapper.mc;

public class Laguna implements ModInitializer {

    @Getter
    private static Laguna instance;
    @Getter
    private EventBus eventBus;
    @Getter
    private ModuleManager moduleManager;
    @Getter
    private CommandManager commandManager;

    @Override
    public void onInitialize() {
        instance = this;
        eventBus = new EventBus();
        eventBus.register(this);
        moduleManager = new ModuleManager();
        commandManager = new CommandManager();
    }

    @Subscribe
    public void onKey(EventKey e){
        if(e.getKey()== 344) {
            mc.setScreen(new ClickGuiScreen());
        }
    }

}

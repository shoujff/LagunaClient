package me.deqes.command.impl;

import com.google.common.eventbus.Subscribe;
import me.deqes.Laguna;
import me.deqes.command.Command;
import me.deqes.event.EventMessage;
import me.deqes.util.ChatColorUtil;
import net.minecraft.text.Text;
import me.deqes.module.Module;

import java.util.ArrayList;
import java.util.List;

import static me.deqes.util.Wrapper.mc;


public class BindCommand extends Command {
    public BindCommand(){
        super("bind", "устанавливает бинд модулю");
    }
    @Subscribe
    public void onMessage(EventMessage e){
        if (e.isCmd() && e.getMessage().startsWith(Command.prefix + "bind")){
            List<String> args = List.of(e.getMessage().split(" "));
            if(args.get(1).equals("set")){
                Module module = null;
                for(Module module1 : Laguna.getInstance().getModuleManager().getModules()){
                    if(args.get(2).equals(module1.getName())) {
                        module = module1;
                    }
                }
                module.setBind(Integer.parseInt(args.get(3)));
            }
            if(args.get(1).equals("remove")){
                Module module = null;
                for(Module module1 : Laguna.getInstance().getModuleManager().getModules()){
                    if(args.get(2).equals(module1.getName())) {
                        module = module1;
                    }
                }
                module.setBind(-1);
            }
        }
    }

}



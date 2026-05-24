package me.deqes.command.impl;

import com.google.common.eventbus.Subscribe;
import me.deqes.Laguna;
import me.deqes.command.Command;
import me.deqes.event.EventMessage;
import me.deqes.util.ChatColorUtil;
import net.minecraft.text.Text;

import java.util.List;

import static me.deqes.util.Wrapper.mc;

public class HelpCommand extends Command {
    public HelpCommand(){
        super("help", "выводит все команды клиента");
    }
    @Subscribe
    public void onMessage(EventMessage e){
        if (e.isCmd() && e.getMessage().startsWith(Command.prefix + "help")){
            List<Command> commands = Laguna.getInstance().getCommandManager().getCommands();
            for(Command command : commands){
                mc.inGameHud.getChatHud().addMessage(Text.of(ChatColorUtil.red + command.getAlias() + ChatColorUtil.white + " - " + ChatColorUtil.red + command.getDesc()));
            }
        }

    }
}

package me.deqes.command;

import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import me.deqes.Laguna;
import me.deqes.command.Command;
import me.deqes.command.impl.BindCommand;
import me.deqes.command.impl.HelpCommand;
import me.deqes.event.EventKey;
import me.deqes.module.impl.render.Watermark;


import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    @Getter
    List<Command> commands = new ArrayList<>();
    public CommandManager()
    {
        Laguna.getInstance().getEventBus().register(this);
        addCommands( new HelpCommand(), new BindCommand());
    }
    private void addCommands(Command... commands){
        for(Command command : commands){
            this.commands.add(command);
            Laguna.getInstance().getEventBus().register(command);
        }
    }

}
